// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

import com.reefkeeper.game.events.DeliveryAcceptedEvent;
import com.reefkeeper.game.events.DeliveryArrivedEvent;
import com.reefkeeper.game.events.DeliveryReturnedEvent;
import com.reefkeeper.game.events.FishPlacedEvent;
import com.reefkeeper.game.events.TreasureFoundEvent;
import com.reefkeeper.puzzle.Puzzle;
import com.reefkeeper.puzzle.PuzzleSource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.NoSuchElementException;

/**
 * The delivery rules: when crates arrive, opening one, checking the count, placing the fish.
 *
 * Plain Java on purpose (no Spring annotations). It gets everything it needs through the
 * constructor (dependency injection), which is what lets the tests swap in a fake puzzle
 * source, a clock they control and a list that records the events.
 *
 * Time: nothing runs in the background. The service always asks the Clock what time it
 * is "now" and works out what should have happened since the last visit (catchUp).
 */
public class DeliveryService {

    public static final Duration DELIVERY_INTERVAL = Duration.ofMinutes(3);
    public static final Duration ANSWER_TIME = Duration.ofSeconds(15);
    public static final int MAX_WAITING = 3;

    private final PuzzleSource puzzles;
    private final Clock clock;
    private final GameEventPublisher events;

    public DeliveryService(PuzzleSource puzzles, Clock clock, GameEventPublisher events) {
        this.puzzles = puzzles;
        this.clock = clock;
        this.events = events;
    }

    /** A brand-new game: the starter aquarium and one delivery already waiting. Given. */
    public PlayerGame startNewGame() {
        Instant now = clock.instant();
        PlayerGame game = new PlayerGame(Aquarium.starter(), now);
        Delivery first = game.addDelivery(now);
        events.publish(new DeliveryArrivedEvent(first.getId()));
        return game;
    }

    /** How long until the next crate is due (never negative). Given. */
    public Duration timeUntilNextDelivery(PlayerGame game) {
        Instant due = game.getLastDeliveryAt().plus(DELIVERY_INTERVAL);
        Duration left = Duration.between(clock.instant(), due);
        return left.isNegative() ? Duration.ZERO : left;
    }

    /**
     * Brings the game up to date with the clock. Call it before showing or changing a game.
     */
    public void catchUp(PlayerGame game) {
    	Instant now = clock.instant();

        // Catch up on missed deliveries based on the interval
        while (!game.getLastDeliveryAt().plus(DELIVERY_INTERVAL).isAfter(now)) {
            Instant newDeliveryTime = game.getLastDeliveryAt().plus(DELIVERY_INTERVAL);
            game.setLastDeliveryAt(newDeliveryTime);

            long waitingCount = game.getDeliveries().stream()
                    .filter(d -> d.getStatus() == DeliveryStatus.WAITING)
                    .count();

            if (waitingCount < MAX_WAITING) {
                Delivery delivery = game.addDelivery(newDeliveryTime);
                events.publish(new DeliveryArrivedEvent(delivery.getId()));
            }
        }

        // Handle expired open deliveries
        for (Delivery delivery : game.getDeliveries()) {
            if (delivery.getStatus() == DeliveryStatus.OPEN && delivery.isExpired(now)) {
                delivery.returnToSender();
                events.publish(new DeliveryReturnedEvent(delivery.getId(), DeliveryReturnedEvent.Reason.TOO_LATE));
            }
        }
    }

    /**
     * Opens the oldest waiting delivery and starts its 15-second countdown.
     */
    public Delivery openNext(PlayerGame game) {
    	catchUp(game);
        Instant now = clock.instant();

        // Check if there is already an open delivery
        Delivery openDelivery = game.getDeliveries().stream()
                .filter(d -> d.getStatus() == DeliveryStatus.OPEN)
                .findFirst()
                .orElse(null);

        if (openDelivery != null) {
            return openDelivery;
        }

        // Otherwise, get the first waiting delivery
        Delivery waitingDelivery = game.getDeliveries().stream()
                .filter(d -> d.getStatus() == DeliveryStatus.WAITING)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No deliveries waiting"));

        Puzzle puzzle = puzzles.nextPuzzle();
        waitingDelivery.open(puzzle, now.plus(ANSWER_TIME));
        return waitingDelivery;
    }

    /**
     * Checks the player's fish count for an open delivery.
     */
    public CountResult submitCount(PlayerGame game, int deliveryId, int count) {
    	Instant now = clock.instant();

        Delivery delivery = game.findDelivery(deliveryId)
                .orElseThrow(() -> new NoSuchElementException("Delivery " + deliveryId + " not found"));

        if (delivery.getStatus() != DeliveryStatus.OPEN) {
            throw new IllegalStateException("Delivery " + deliveryId + " is not open");
        }

        if (delivery.isExpired(now)) {
            delivery.returnToSender();
            events.publish(new DeliveryReturnedEvent(delivery.getId(), DeliveryReturnedEvent.Reason.TOO_LATE));
            return CountResult.TOO_LATE;
        }

        if (count == delivery.fishCount()) {
            delivery.accept();
            events.publish(new DeliveryAcceptedEvent(delivery.getId(), delivery.fishCount()));
            return CountResult.CORRECT;
        } else {
            delivery.returnToSender();
            events.publish(new DeliveryReturnedEvent(delivery.getId(), DeliveryReturnedEvent.Reason.WRONG_COUNT));
            return CountResult.WRONG;
        }
    }

    /**
     *Puts an accepted delivery's fish into the chosen tank.
     */
    public void placeFish(PlayerGame game, int deliveryId, int tankId) {
    	Delivery delivery = game.findDelivery(deliveryId)
                .orElseThrow(() -> new NoSuchElementException("Delivery " + deliveryId + " not found"));

        Tank tank = game.getAquarium().findTank(tankId)
                .orElseThrow(() -> new NoSuchElementException("Tank " + tankId + " not found"));

        if (delivery.getStatus() != DeliveryStatus.ACCEPTED) {
            throw new IllegalStateException("Delivery " + deliveryId + " is not accepted");
        }

        // Add fish first; will throw an exception if it doesn't fit
        tank.addFish(delivery.fishCount());
        tank.addDecorations(delivery.getDecorationsWon());

        delivery.place();
        events.publish(new FishPlacedEvent(delivery.getId(), tank.getId(), delivery.fishCount()));
    }
    
    /**
     * The treasure check for an accepted delivery. Returns true if the chest count was right.
     */
    public boolean submitTreasure(PlayerGame game, int deliveryId, int chests) {
    	Delivery delivery = game.findDelivery(deliveryId)
                .orElseThrow(() -> new NoSuchElementException("Delivery " + deliveryId + " not found"));

        boolean right = delivery.checkTreasure(chests);

        if (right && delivery.getDecorationsWon() > 0) {
            events.publish(new TreasureFoundEvent(deliveryId, delivery.getDecorationsWon()));
        }

        return right;
    }
}

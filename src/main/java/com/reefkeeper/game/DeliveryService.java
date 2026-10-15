// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.game;

import com.reefkeeper.game.events.DeliveryAcceptedEvent;
import com.reefkeeper.game.events.DeliveryArrivedEvent;
import com.reefkeeper.game.events.DeliveryReturnedEvent;
import com.reefkeeper.game.events.FishPlacedEvent;
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
    public static final Duration ANSWER_TIME = Duration.ofSeconds(30);
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
     * STEP 4. Brings the game up to date with the clock. Call it before showing or changing a game.
     *
     * Part A - new deliveries:
     *   now = clock.instant()
     *   while (lastDeliveryAt + DELIVERY_INTERVAL) is not after now:
     *       move lastDeliveryAt forward by DELIVERY_INTERVAL (game.setLastDeliveryAt)
     *       if fewer than MAX_WAITING deliveries are WAITING:
     *           add a delivery that arrived at the new lastDeliveryAt (game.addDelivery)
     *           publish a DeliveryArrivedEvent for it
     *
     * Part B - deliveries the player ran out of time on:
     *   for every OPEN delivery that isExpired(now):
     *       returnToSender() and publish DeliveryReturnedEvent with reason TOO_LATE
     *
     * Why a while loop and not an if: a player who comes back after 10 minutes has
     * missed several deliveries, and each one has to be worked out.
     */
    public void catchUp(PlayerGame game) {
        // TODO step 4
        throw new UnsupportedOperationException("TODO step 4: DeliveryService.catchUp");
    }

    /**
     * STEP 5. Opens the oldest waiting delivery and starts its 30-second countdown.
     *
     *   1. catchUp(game)
     *   2. If a delivery is already OPEN, return that one (so reloading the page doesn't skip it).
     *   3. Otherwise take the first WAITING delivery. If there is none,
     *      throw new IllegalStateException("No deliveries waiting").
     *   4. Get a puzzle from puzzles.nextPuzzle() and call delivery.open(puzzle, now + ANSWER_TIME).
     *   5. Return the delivery.
     */
    public Delivery openNext(PlayerGame game) {
        // TODO step 5
        throw new UnsupportedOperationException("TODO step 5: DeliveryService.openNext");
    }

    /**
     * STEP 6. Checks the player's fish count for an open delivery.
     *
     *   1. Find the delivery (game.findDelivery). Missing: throw NoSuchElementException.
     *   2. If it is not OPEN: throw IllegalStateException.
     *   3. If it isExpired(now): returnToSender(), publish DeliveryReturnedEvent(TOO_LATE),
     *      return CountResult.TOO_LATE.
     *   4. If count equals delivery.fishCount(): accept(), publish DeliveryAcceptedEvent,
     *      return CountResult.CORRECT.
     *   5. Otherwise: returnToSender(), publish DeliveryReturnedEvent(WRONG_COUNT),
     *      return CountResult.WRONG.
     */
    public CountResult submitCount(PlayerGame game, int deliveryId, int count) {
        // TODO step 6
        throw new UnsupportedOperationException("TODO step 6: DeliveryService.submitCount");
    }

    /**
     * STEP 7. Puts an accepted delivery's fish into the chosen tank.
     *
     *   1. Find the delivery and the tank (game.getAquarium().findTank).
     *      Either missing: throw NoSuchElementException.
     *   2. If the delivery is not ACCEPTED: throw IllegalStateException.
     *   3. tank.addFish(delivery.fishCount())  - this throws by itself if they don't fit.
     *   4. delivery.place() and publish a FishPlacedEvent.
     *
     * Order matters: add the fish BEFORE place(), so if the tank is full the
     * delivery stays ACCEPTED and the player can buy a tank and try again.
     */
    public void placeFish(PlayerGame game, int deliveryId, int tankId) {
        // TODO step 7
        throw new UnsupportedOperationException("TODO step 7: DeliveryService.placeFish");
    }
}
// Note: Eclipse will mark some imports as "never used" (yellow) until you write steps 4-7. That is fine.

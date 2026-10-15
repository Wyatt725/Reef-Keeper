// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.game;

import com.reefkeeper.game.events.DeliveryAcceptedEvent;
import com.reefkeeper.game.events.DeliveryArrivedEvent;
import com.reefkeeper.game.events.DeliveryReturnedEvent;
import com.reefkeeper.game.events.FishPlacedEvent;
import com.reefkeeper.puzzle.Puzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * No Fish API, no Spring, no waiting: a fake puzzle source, a clock we move by hand,
 * and a list that records every event. This is what low coupling buys.
 */
class DeliveryServiceTest {

    private static final Instant START = Instant.parse("2026-10-15T10:00:00Z");

    private MutableClock clock;
    private List<Object> events;
    private int fishInNextCrate;
    private DeliveryService service;
    private PlayerGame game;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(START);
        events = new ArrayList<>();
        fishInNextCrate = 4;
        service = new DeliveryService(() -> new Puzzle("data:image/png;base64,AAAA", fishInNextCrate, 1),
                clock, events::add);
        game = service.startNewGame();
        events.clear(); // ignore the starter delivery's event
    }

    private int waiting() {
        return game.deliveriesWithStatus(DeliveryStatus.WAITING).size();
    }

    // ---- Step 3: Delivery.isExpired

    @Test
    void waitingDeliveryIsNeverExpired() {
        Delivery delivery = new Delivery(1, START);
        assertFalse(delivery.isExpired(START.plus(Duration.ofHours(1))));
    }

    @Test
    void openDeliveryExpiresAfterItsDeadline() {
        Delivery delivery = new Delivery(1, START);
        delivery.open(new Puzzle("img", 3, 0), START.plusSeconds(30));
        assertFalse(delivery.isExpired(START.plusSeconds(30)));
        assertTrue(delivery.isExpired(START.plusSeconds(31)));
    }

    // ---- Step 4: catchUp

    @Test
    void newGameStartsWithOneDeliveryWaiting() {
        assertEquals(1, waiting());
    }

    @Test
    void aNewDeliveryArrivesEveryThreeMinutes() {
        clock.advance(Duration.ofMinutes(2));
        service.catchUp(game);
        assertEquals(1, waiting());

        clock.advance(Duration.ofMinutes(1));
        service.catchUp(game);
        assertEquals(2, waiting());
        assertTrue(events.get(0) instanceof DeliveryArrivedEvent);
    }

    @Test
    void comingBackLaterCatchesUpOnMissedDeliveries() {
        clock.advance(Duration.ofMinutes(6));
        service.catchUp(game);
        assertEquals(3, waiting());
        assertEquals(2, events.size());
    }

    @Test
    void neverMoreThanThreeWaiting() {
        clock.advance(Duration.ofMinutes(30));
        service.catchUp(game);
        assertEquals(3, waiting());
        // The schedule still moves on, so the next delivery is less than 3 minutes away.
        assertTrue(service.timeUntilNextDelivery(game).compareTo(Duration.ofMinutes(3)) <= 0);
    }

    @Test
    void catchUpReturnsADeliveryThatRanOutOfTime() {
        Delivery delivery = service.openNext(game);
        clock.advance(Duration.ofSeconds(31));
        service.catchUp(game);
        assertEquals(DeliveryStatus.RETURNED, delivery.getStatus());
        assertEquals(new DeliveryReturnedEvent(delivery.getId(), DeliveryReturnedEvent.Reason.TOO_LATE),
                events.get(events.size() - 1));
    }

    // ---- Step 5: openNext

    @Test
    void openingADeliveryGivesAPictureAndA30SecondDeadline() {
        Delivery delivery = service.openNext(game);
        assertEquals(DeliveryStatus.OPEN, delivery.getStatus());
        assertNotNull(delivery.getPuzzle());
        assertEquals(START.plusSeconds(30), delivery.getDeadline());
        assertEquals(0, waiting());
    }

    @Test
    void openingTwiceReturnsTheSameDelivery() {
        Delivery first = service.openNext(game);
        Delivery again = service.openNext(game);
        assertEquals(first.getId(), again.getId());
    }

    @Test
    void openingWithNothingWaitingIsRefused() {
        Delivery delivery = service.openNext(game);
        service.submitCount(game, delivery.getId(), fishInNextCrate);
        assertThrows(IllegalStateException.class, () -> service.openNext(game));
    }

    // ---- Step 6: submitCount

    @Test
    void rightCountAcceptsTheDelivery() {
        Delivery delivery = service.openNext(game);
        assertEquals(CountResult.CORRECT, service.submitCount(game, delivery.getId(), 4));
        assertEquals(DeliveryStatus.ACCEPTED, delivery.getStatus());
        assertEquals(new DeliveryAcceptedEvent(delivery.getId(), 4), events.get(events.size() - 1));
    }

    @Test
    void wrongCountReturnsTheDelivery() {
        Delivery delivery = service.openNext(game);
        assertEquals(CountResult.WRONG, service.submitCount(game, delivery.getId(), 5));
        assertEquals(DeliveryStatus.RETURNED, delivery.getStatus());
        assertEquals(new DeliveryReturnedEvent(delivery.getId(), DeliveryReturnedEvent.Reason.WRONG_COUNT),
                events.get(events.size() - 1));
    }

    @Test
    void answerAfter30SecondsIsTooLate() {
        Delivery delivery = service.openNext(game);
        clock.advance(Duration.ofSeconds(31));
        assertEquals(CountResult.TOO_LATE, service.submitCount(game, delivery.getId(), 4));
        assertEquals(DeliveryStatus.RETURNED, delivery.getStatus());
    }

    @Test
    void countingADeliveryThatIsNotOpenIsRefused() {
        Delivery waitingDelivery = game.deliveriesWithStatus(DeliveryStatus.WAITING).get(0);
        assertThrows(IllegalStateException.class,
                () -> service.submitCount(game, waitingDelivery.getId(), 4));
    }

    @Test
    void countingAnUnknownDeliveryIsRefused() {
        assertThrows(java.util.NoSuchElementException.class, () -> service.submitCount(game, 999, 4));
    }

    // ---- Step 7: placeFish

    @Test
    void placingPutsTheFishInTheTank() {
        Delivery delivery = service.openNext(game);
        service.submitCount(game, delivery.getId(), 4);
        int tankId = game.getAquarium().getTanks().get(0).getId();

        service.placeFish(game, delivery.getId(), tankId);

        assertEquals(4, game.getAquarium().totalFish());
        assertEquals(DeliveryStatus.PLACED, delivery.getStatus());
        assertEquals(new FishPlacedEvent(delivery.getId(), tankId, 4), events.get(events.size() - 1));
    }

    @Test
    void aFullTankRefusesAndTheDeliveryWaitsForAnotherTank() {
        fishInNextCrate = 8;
        int tankId = game.getAquarium().getTanks().get(0).getId();
        Delivery first = service.openNext(game);
        service.submitCount(game, first.getId(), 8);
        service.placeFish(game, first.getId(), tankId);

        clock.advance(Duration.ofMinutes(3));
        Delivery second = service.openNext(game);
        service.submitCount(game, second.getId(), 8);

        assertThrows(IllegalStateException.class, () -> service.placeFish(game, second.getId(), tankId));
        assertEquals(DeliveryStatus.ACCEPTED, second.getStatus());
        assertEquals(8, game.getAquarium().totalFish());
    }

    @Test
    void placingADeliveryThatWasNotAcceptedIsRefused() {
        Delivery delivery = service.openNext(game);
        int tankId = game.getAquarium().getTanks().get(0).getId();
        assertThrows(IllegalStateException.class, () -> service.placeFish(game, delivery.getId(), tankId));
        assertEquals(0, game.getAquarium().totalFish());
    }

    @Test
    void placingInAnUnknownTankIsRefused() {
        Delivery delivery = service.openNext(game);
        service.submitCount(game, delivery.getId(), 4);
        assertThrows(java.util.NoSuchElementException.class,
                () -> service.placeFish(game, delivery.getId(), 999));
    }
}

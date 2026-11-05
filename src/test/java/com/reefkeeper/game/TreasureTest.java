// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

import com.reefkeeper.game.events.TreasureFoundEvent;
import com.reefkeeper.puzzle.Puzzle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The treasure check: count the chests in a crate you just accepted, win decorations. */
class TreasureTest {

    private static final Instant START = Instant.parse("2026-11-09T10:00:00Z");

    private MutableClock clock;
    private List<Object> events;
    private int chestsInNextCrate;
    private DeliveryService service;
    private PlayerGame game;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(START);
        events = new ArrayList<>();
        chestsInNextCrate = 2;
        service = new DeliveryService(() -> new Puzzle("data:image/png;base64,AAAA", 4, chestsInNextCrate),
                clock, events::add);
        game = service.startNewGame();
    }

    /** Opens the waiting delivery and counts its 4 fish right, so it is ACCEPTED. */
    private Delivery accepted() {
        Delivery delivery = service.openNext(game);
        service.submitCount(game, delivery.getId(), 4);
        events.clear();
        return delivery;
    }

    private List<Object> treasureEvents() {
        return events.stream().filter(e -> e instanceof TreasureFoundEvent).toList();
    }

    private Tank tank() {
        return game.getAquarium().getTanks().get(0);
    }

    // ---- Step A1: Delivery.checkTreasure

    @Test
    void theRightChestCountWinsOneDecorationPerChest() {
        Delivery delivery = accepted();
        assertTrue(delivery.checkTreasure(2));
        assertEquals(2, delivery.getDecorationsWon());
        assertTrue(delivery.isTreasureChecked());
    }

    @Test
    void aWrongChestCountWinsNothingButUsesUpTheCheck() {
        Delivery delivery = accepted();
        assertFalse(delivery.checkTreasure(5));
        assertEquals(0, delivery.getDecorationsWon());
        assertTrue(delivery.isTreasureChecked());
    }

    @Test
    void theTreasureCanOnlyBeCheckedOnce() {
        Delivery delivery = accepted();
        delivery.checkTreasure(5);
        assertThrows(IllegalStateException.class, () -> delivery.checkTreasure(2));
        assertEquals(0, delivery.getDecorationsWon());
    }

    @Test
    void anOpenDeliveryHasNoTreasureCheckYet() {
        Delivery delivery = service.openNext(game); // opened, not counted
        assertThrows(IllegalStateException.class, () -> delivery.checkTreasure(2));
        assertFalse(delivery.isTreasureChecked());
    }

    // ---- Step A2: DeliveryService.submitTreasure (+ the extra line in placeFish)

    @Test
    void aRightCountPublishesTreasureFound() {
        Delivery delivery = accepted();
        assertTrue(service.submitTreasure(game, delivery.getId(), 2));
        assertEquals(List.of(new TreasureFoundEvent(delivery.getId(), 2)), treasureEvents());
    }

    @Test
    void aWrongCountPublishesNothing() {
        Delivery delivery = accepted();
        assertFalse(service.submitTreasure(game, delivery.getId(), 1));
        assertTrue(treasureEvents().isEmpty());
    }

    @Test
    void aCrateWithNoChestsIsRightButWinsNothing() {
        chestsInNextCrate = 0;
        Delivery delivery = accepted();
        assertTrue(service.submitTreasure(game, delivery.getId(), 0));
        assertEquals(0, delivery.getDecorationsWon());
        assertTrue(treasureEvents().isEmpty());
    }

    @Test
    void anUnknownDeliveryIsNotFound() {
        assertThrows(NoSuchElementException.class, () -> service.submitTreasure(game, 99, 2));
    }

    @Test
    void theDecorationsGoIntoTheTankWithTheFish() {
        Delivery delivery = accepted();
        service.submitTreasure(game, delivery.getId(), 2);
        int valueBefore = game.getAquarium().value();
        service.placeFish(game, delivery.getId(), tank().getId());
        assertEquals(4, tank().getFishCount());
        assertEquals(2, tank().getDecorations());
        assertEquals(valueBefore + 4 * 10 + 2 * 25, game.getAquarium().value());
    }

    @Test
    void skippingTheTreasureCheckStillPlacesTheFish() {
        Delivery delivery = accepted();
        service.placeFish(game, delivery.getId(), tank().getId());
        assertEquals(4, tank().getFishCount());
        assertEquals(0, tank().getDecorations());
    }
}

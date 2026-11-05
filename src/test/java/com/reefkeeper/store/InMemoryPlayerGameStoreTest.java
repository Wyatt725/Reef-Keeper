// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.store;

import com.reefkeeper.game.DeliveryService;
import com.reefkeeper.game.PlayerGame;
import com.reefkeeper.puzzle.Puzzle;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

class InMemoryPlayerGameStoreTest {

    private final DeliveryService deliveries =
            new DeliveryService(() -> new Puzzle("img", 3, 0), Clock.systemUTC(), event -> { });
    private final InMemoryPlayerGameStore store = new InMemoryPlayerGameStore(deliveries);

    @Test
    void firstVisitStartsANewGame() {
        PlayerGame game = store.gameFor("wyatt");
        assertEquals(50, game.getAquarium().getCoins());
        assertEquals(1, game.getAquarium().getTanks().size());
    }

    @Test
    void samePlayerAlwaysGetsTheSameGame() {
        assertSame(store.gameFor("wyatt"), store.gameFor("wyatt"));
    }

    @Test
    void eachPlayerHasTheirOwnGame() {
        assertNotSame(store.gameFor("wyatt"), store.gameFor("amaya"));
    }
}

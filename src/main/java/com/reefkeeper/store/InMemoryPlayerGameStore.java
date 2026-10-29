// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 29 Oct 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.store;

import com.reefkeeper.game.DeliveryService;
import com.reefkeeper.game.PlayerGame;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps every player's game in memory, keyed by username.
 *
 * Better than Week 5's HttpSession: the game now belongs to the ACCOUNT, not the browser,
 * so a player who logs out and back in (or uses another browser) finds the same aquarium.
 * Limit: games are lost when the server restarts. Accounts are not, they are in the database.
 */
@Component
public class InMemoryPlayerGameStore implements PlayerGameStore {

    /** ConcurrentHashMap: safe when several players' requests arrive at the same time. */
    private final Map<String, PlayerGame> games = new ConcurrentHashMap<>();
    private final DeliveryService deliveries;

    public InMemoryPlayerGameStore(DeliveryService deliveries) {
        this.deliveries = deliveries;
    }

    /**
     * STEP 3. Return the game stored for this username. If there is none yet,
     * start one with deliveries.startNewGame(), store it, and return it.
     * Hint: Map has a method that does all of this in one line: computeIfAbsent.
     */
    @Override
    public PlayerGame gameFor(String username) {
        // TODO step 3
        throw new UnsupportedOperationException("TODO step 3: InMemoryPlayerGameStore.gameFor");
    }
}

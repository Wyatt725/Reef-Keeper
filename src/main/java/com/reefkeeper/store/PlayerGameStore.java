// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.store;

import com.reefkeeper.game.PlayerGame;
import java.util.Map;

/**
 * Finds the game that belongs to a player. GameController only knows this interface,
 * so the in-memory version can later be swapped for one that saves to the database.
 */
public interface PlayerGameStore {

    /** This player's game, starting a new one the first time they play. */
    PlayerGame gameFor(String username);
    Map<String, PlayerGame> allGames();
}

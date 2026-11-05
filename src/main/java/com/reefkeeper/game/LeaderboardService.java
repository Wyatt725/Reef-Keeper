// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 3 Nov 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Ranks every aquarium by its value (10 per fish + 25 per decoration + the price of each tank).
 *
 * Plain Java again: it gets the games as a Map from username to game, so it doesn't care
 * where they are kept (memory now, maybe the database later), and the tests can hand it
 * a map they build themselves.
 */
public class LeaderboardService {

    /**
     * STEP B1. Every player, best aquarium first, as leaderboard rows.
     *
     * The sorting is given: `sorted` has the usernames from the highest value to the lowest,
     * and players with the same value in A-Z order. You write the loop that numbers them:
     *
     *   for each position i (0, 1, 2, ...) in sorted:
     *       name = sorted.get(i);  aquarium = games.get(name).getAquarium()
     *       rank = i + 1, EXCEPT when this aquarium's value equals the previous player's value:
     *              then it gets the same rank as the previous player
     *       add new LeaderboardEntry(rank, name, aquarium.totalFish(),
     *                                aquarium.totalDecorations(), aquarium.value())
     *
     * Equal values share a rank and the next rank is skipped, like a sports table:
     * values 400, 300, 300, 200 get ranks 1, 2, 2, 4.
     * Hint: the previous row is rows.get(rows.size() - 1).
     */
    public List<LeaderboardEntry> rank(Map<String, PlayerGame> games) {
        List<String> sorted = new ArrayList<>(games.keySet());
        sorted.sort(Comparator
                .comparingInt((String name) -> games.get(name).getAquarium().value()).reversed()
                .thenComparing(Comparator.naturalOrder()));

        List<LeaderboardEntry> rows = new ArrayList<>();
        // TODO step B1: fill rows
        return rows;
    }
}

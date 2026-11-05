// Reef Keeper - CIS045-3 coursework.
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
     * and players with the same value in A-Z order.
     *
     * Equal values share a rank and the next rank is skipped, like a sports table:
     * values 400, 300, 300, 200 get ranks 1, 2, 2, 4.
     */
    public List<LeaderboardEntry> rank(Map<String, PlayerGame> games) {
        List<String> sorted = new ArrayList<>(games.keySet());
        sorted.sort(Comparator.comparingInt((String name) -> games.get(name).getAquarium().value()).reversed().thenComparing(Comparator.naturalOrder()));

        List<LeaderboardEntry> rows = new ArrayList<>();
        
        for (int i = 0; i < sorted.size(); i++) {
            String name = sorted.get(i);
            Aquarium aquarium = games.get(name).getAquarium();
            int value = aquarium.value();
            
            int rank;
            if (i == 0) {
                rank = 1;
            } else {
                LeaderboardEntry previousRow = rows.get(rows.size() - 1);
                if (value == previousRow.value()) {
                    rank = previousRow.rank();
                } else {
                    rank = i + 1;
                }
            }
            
            rows.add(new LeaderboardEntry(
                    rank, 
                    name, 
                    aquarium.totalFish(), 
                    aquarium.totalDecorations(), 
                    value
            ));
        }
        
        return rows;
    }
}

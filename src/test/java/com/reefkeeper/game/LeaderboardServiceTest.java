// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 3 Nov 2026. These tests describe step B1.
package com.reefkeeper.game;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Ranking aquariums by value. No Spring, no store: just a Map built by hand. */
class LeaderboardServiceTest {

    private static final Instant START = Instant.parse("2026-11-09T10:00:00Z");

    private final LeaderboardService service = new LeaderboardService();

    /** A starter aquarium (one small tank, value 50) with this many fish: value 50 + 10 per fish. */
    private static PlayerGame withFish(int fish) {
        PlayerGame game = new PlayerGame(Aquarium.starter(), START);
        game.getAquarium().getTanks().get(0).addFish(fish);
        return game;
    }

    private static List<String> names(List<LeaderboardEntry> rows) {
        return rows.stream().map(LeaderboardEntry::username).toList();
    }

    private static List<Integer> ranks(List<LeaderboardEntry> rows) {
        return rows.stream().map(LeaderboardEntry::rank).toList();
    }

    @Test
    void noPlayersMeansAnEmptyBoard() {
        assertTrue(service.rank(Map.of()).isEmpty());
    }

    @Test
    void theHighestValueComesFirst() {
        List<LeaderboardEntry> rows = service.rank(Map.of(
                "ana", withFish(2), "ben", withFish(9), "cal", withFish(5)));
        assertEquals(List.of("ben", "cal", "ana"), names(rows));
        assertEquals(List.of(1, 2, 3), ranks(rows));
    }

    @Test
    void equalValuesShareARankAndTheNextRankIsSkipped() {
        List<LeaderboardEntry> rows = service.rank(Map.of(
                "dee", withFish(8), "eve", withFish(5), "fay", withFish(5), "gus", withFish(1)));
        assertEquals(List.of("dee", "eve", "fay", "gus"), names(rows));
        assertEquals(List.of(1, 2, 2, 4), ranks(rows));
    }

    @Test
    void aRowCarriesFishDecorationsAndValue() {
        PlayerGame game = withFish(6);
        game.getAquarium().getTanks().get(0).addDecorations(2);
        LeaderboardEntry row = service.rank(Map.of("wyatt", game)).get(0);
        assertEquals(new LeaderboardEntry(1, "wyatt", 6, 2, 50 + 6 * 10 + 2 * 25), row);
    }

    @Test
    void everyoneTiedIsEveryoneFirst() {
        List<LeaderboardEntry> rows = service.rank(Map.of("zed", withFish(3), "amy", withFish(3)));
        assertEquals(List.of("amy", "zed"), names(rows));
        assertEquals(List.of(1, 1), ranks(rows));
    }
}

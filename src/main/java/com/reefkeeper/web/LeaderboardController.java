// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.web;

import com.reefkeeper.game.LeaderboardEntry;
import com.reefkeeper.game.LeaderboardService;
import com.reefkeeper.store.PlayerGameStore;
import com.reefkeeper.web.ApiModels.LeaderboardView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * GET /api/leaderboard -> LeaderboardView: the top 10 aquariums plus your own row.
 *
 * Worked out when someone asks (pull), not kept up to date by listeners on every change
 * (push): with a handful of players, ranking every aquarium takes no time, and there is
 * nothing to get out of step. A good alternative to compare in the video.
 *
 * Virtual identity: there is no endpoint that takes a username, so nobody can ask for
 * another player's aquarium. The leaderboard only shows what LeaderboardEntry carries.
 */
@RestController
@RequestMapping("/api")
public class LeaderboardController {

    public static final int TOP = 10;

    private final PlayerGameStore games;
    private final LeaderboardService leaderboard;

    public LeaderboardController(PlayerGameStore games, LeaderboardService leaderboard) {
        this.games = games;
        this.leaderboard = leaderboard;
    }

    @GetMapping("/leaderboard")
    public LeaderboardView getLeaderboard(Principal principal) {
        String me = principal.getName();
        games.gameFor(me); // a player who opens this page first still gets an aquarium, and a row
        List<LeaderboardEntry> everyone = leaderboard.rank(games.allGames());
        LeaderboardEntry mine = everyone.stream().filter(row -> row.username().equals(me)).findFirst().orElse(null);
        return new LeaderboardView(everyone.stream().limit(TOP).toList(), mine);
    }
}

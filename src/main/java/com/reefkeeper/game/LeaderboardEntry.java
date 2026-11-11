// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

/**
 * One row of the leaderboard. Only what every player may see about another player:
 * their name and what is in their aquarium. Never their coins, deliveries or account.
 */
public record LeaderboardEntry(int rank, String username, int fish, int decorations, int value) {
}

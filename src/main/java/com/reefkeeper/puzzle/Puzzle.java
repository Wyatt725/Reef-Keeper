// Reef Keeper - CIS045-3 coursework.
// Source: starter skeleton generated with Claude (Anthropic AI), 8 Oct 2026.
package com.reefkeeper.puzzle;

/**
 * One round's data, already converted into the game's own format.
 *
 * imageDataUri is safe to send to the browser.
 * fishCount and chestCount are the answers: they must stay on the server.
 */
public record Puzzle(String imageDataUri, int fishCount, int chestCount) {
}

// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 3 Nov 2026. Complete as given.
package com.reefkeeper.game.events;

/** The player counted the treasure chests right and won this many decorations. */
public record TreasureFoundEvent(int deliveryId, int decorations) {
}

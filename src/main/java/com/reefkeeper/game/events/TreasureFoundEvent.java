// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game.events;

/** The player counted the treasure chests right and won this many decorations. */
public record TreasureFoundEvent(int deliveryId, int decorations) {
}

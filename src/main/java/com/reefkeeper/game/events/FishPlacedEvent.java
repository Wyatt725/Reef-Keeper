// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game.events;

/** Fish from an accepted delivery were put in a tank. */
public record FishPlacedEvent(int deliveryId, int tankId, int fish) {
}

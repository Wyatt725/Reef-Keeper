// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.game.events;

/** Fish from an accepted delivery were put in a tank. */
public record FishPlacedEvent(int deliveryId, int tankId, int fish) {
}

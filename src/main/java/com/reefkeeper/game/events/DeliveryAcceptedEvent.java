// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.game.events;

/** The player counted the fish correctly; the fish are waiting to be placed in a tank. */
public record DeliveryAcceptedEvent(int deliveryId, int fish) {
}

// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.game.events;

/** The player counted the fish correctly; the fish are waiting to be placed in a tank. */
public record DeliveryAcceptedEvent(int deliveryId, int fish) {
}

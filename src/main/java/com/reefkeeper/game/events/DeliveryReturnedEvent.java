// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game.events;

/** The delivery was sent back: a wrong count, or no answer within 30 seconds. */
public record DeliveryReturnedEvent(int deliveryId, Reason reason) {

    public enum Reason {
        WRONG_COUNT,
        TOO_LATE
    }
}

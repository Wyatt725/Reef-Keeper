// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.game.events;

/** The delivery was sent back: a wrong count, or no answer within 30 seconds. */
public record DeliveryReturnedEvent(int deliveryId, Reason reason) {

    public enum Reason {
        WRONG_COUNT,
        TOO_LATE
    }
}

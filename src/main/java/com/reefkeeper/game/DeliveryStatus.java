// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

/**
 * Where a delivery is in its life:
 *
 *   WAITING --open--> OPEN --right count--> ACCEPTED --placed in a tank--> PLACED
 *                       |
 *                       +--wrong count or too late--> RETURNED
 */
public enum DeliveryStatus {
    WAITING,
    OPEN,
    ACCEPTED,
    PLACED,
    RETURNED
}

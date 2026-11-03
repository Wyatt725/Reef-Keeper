// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.game.events;

/** Visitors came {@code visits} times since the last check and paid {@code coins} in total. */
public record VisitorsPaidEvent(int visits, int coins) {
}

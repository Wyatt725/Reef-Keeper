// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game.events;

/** The player bought food for {@code tanks} hungry tanks, which cost {@code coins}. */
public record TanksFedEvent(int tanks, int coins) {
}

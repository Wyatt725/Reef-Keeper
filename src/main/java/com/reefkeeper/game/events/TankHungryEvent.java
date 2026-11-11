// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game.events;

/** A tank ran out of food: visitors pay nothing for its fish until it is fed. */
public record TankHungryEvent(int tankId) {
}

// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game.events;

import com.reefkeeper.game.TankSize;

/** The player bought a new tank in the shop. */
public record TankBoughtEvent(int tankId, TankSize size, int price) {
}

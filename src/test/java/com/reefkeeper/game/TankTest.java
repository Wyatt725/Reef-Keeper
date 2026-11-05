// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankTest {

    @Test
    void newSmallTankIsEmptyWithTenSpaces() {
        Tank tank = new Tank(1, TankSize.SMALL);
        assertEquals(0, tank.getFishCount());
        assertEquals(10, tank.freeSpace());
    }

    @Test
    void addFishFillsTheTank() {
        Tank tank = new Tank(1, TankSize.SMALL);
        tank.addFish(7);
        assertEquals(7, tank.getFishCount());
        assertEquals(3, tank.freeSpace());
    }

    @Test
    void canHoldChecksTheFreeSpace() {
        Tank tank = new Tank(1, TankSize.SMALL);
        tank.addFish(7);
        assertTrue(tank.canHold(3));
        assertFalse(tank.canHold(4));
    }

    @Test
    void tooManyFishAreRefusedAndTheTankIsUnchanged() {
        Tank tank = new Tank(1, TankSize.SMALL);
        tank.addFish(8);
        assertThrows(IllegalStateException.class, () -> tank.addFish(5));
        assertEquals(8, tank.getFishCount());
    }

    @Test
    void negativeFishAreRefused() {
        Tank tank = new Tank(1, TankSize.LARGE);
        assertThrows(IllegalArgumentException.class, () -> tank.addFish(-1));
    }

    @Test
    void largeTankHoldsTwentyFive() {
        Tank tank = new Tank(2, TankSize.LARGE);
        tank.addFish(25);
        assertEquals(0, tank.freeSpace());
    }
}

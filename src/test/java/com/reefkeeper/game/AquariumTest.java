// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AquariumTest {

    @Test
    void starterAquariumHasOneSmallTankAndFiftyCoins() {
        Aquarium aquarium = Aquarium.starter();
        assertEquals(50, aquarium.getCoins());
        assertEquals(1, aquarium.getTanks().size());
        assertEquals(TankSize.SMALL, aquarium.getTanks().get(0).getSize());
    }

    @Test
    void eachNewTankGetsItsOwnId() {
        Aquarium aquarium = new Aquarium(0);
        Tank first = aquarium.addTank(TankSize.SMALL);
        Tank second = aquarium.addTank(TankSize.LARGE);
        assertNotEquals(first.getId(), second.getId());
        assertEquals(2, aquarium.getTanks().size());
    }

    @Test
    void findTankByIdOrEmpty() {
        Aquarium aquarium = new Aquarium(0);
        Tank large = aquarium.addTank(TankSize.LARGE);
        assertEquals(large, aquarium.findTank(large.getId()).orElseThrow());
        assertTrue(aquarium.findTank(999).isEmpty());
    }

    @Test
    void totalsAddUpAcrossTanks() {
        Aquarium aquarium = new Aquarium(0);
        aquarium.addTank(TankSize.SMALL).addFish(4);
        aquarium.addTank(TankSize.LARGE).addFish(10);
        assertEquals(14, aquarium.totalFish());
        assertEquals(6 + 15, aquarium.totalFreeSpace());
    }

    @Test
    void valueCountsFishDecorationsAndTanks() {
        Aquarium aquarium = new Aquarium(0);
        Tank small = aquarium.addTank(TankSize.SMALL);
        small.addFish(3);
        small.addDecorations(2);
        aquarium.addTank(TankSize.LARGE);
        // 3 fish x 10 + 2 decorations x 25 + tank prices 50 + 150
        assertEquals(30 + 50 + 50 + 150, aquarium.value());
    }
}

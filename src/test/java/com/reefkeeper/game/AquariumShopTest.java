// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 3 Nov 2026. These tests describe step 1.
package com.reefkeeper.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Coins in an Aquarium: earning them and spending them on tanks. */
class AquariumShopTest {

    // ---- addCoins (given)

    @Test
    void addCoinsIncreasesCoins() {
        Aquarium aquarium = new Aquarium(10);
        aquarium.addCoins(15);
        assertEquals(25, aquarium.getCoins());
    }

    @Test
    void addCoinsRejectsNegativeAmounts() {
        Aquarium aquarium = new Aquarium(10);
        assertThrows(IllegalArgumentException.class, () -> aquarium.addCoins(-1));
        assertEquals(10, aquarium.getCoins());
    }

    // ---- Step 1: buyTank

    @Test
    void buyingASmallTankCostsFiftyCoinsAndAddsTheTank() {
        Aquarium aquarium = Aquarium.starter(); // 50 coins, 1 small tank
        Tank tank = aquarium.buyTank(TankSize.SMALL);
        assertEquals(0, aquarium.getCoins());
        assertEquals(2, aquarium.getTanks().size());
        assertEquals(TankSize.SMALL, tank.getSize());
        assertEquals(2, tank.getId());
        assertEquals(0, tank.getFishCount());
    }

    @Test
    void buyingALargeTankCostsOneHundredFiftyCoins() {
        Aquarium aquarium = new Aquarium(200);
        aquarium.buyTank(TankSize.LARGE);
        assertEquals(50, aquarium.getCoins());
        assertEquals(TankSize.LARGE, aquarium.getTanks().get(0).getSize());
    }

    @Test
    void notEnoughCoinsThrowsAndChangesNothing() {
        Aquarium aquarium = Aquarium.starter(); // 50 coins
        assertThrows(IllegalStateException.class, () -> aquarium.buyTank(TankSize.LARGE));
        assertEquals(50, aquarium.getCoins());
        assertEquals(1, aquarium.getTanks().size());
    }

    @Test
    void cannotGoPastTheMaximumNumberOfTanks() {
        Aquarium aquarium = new Aquarium(10_000);
        for (int i = 0; i < Aquarium.MAX_TANKS; i++) {
            aquarium.buyTank(TankSize.SMALL);
        }
        int coinsBefore = aquarium.getCoins();
        assertThrows(IllegalStateException.class, () -> aquarium.buyTank(TankSize.SMALL));
        assertEquals(Aquarium.MAX_TANKS, aquarium.getTanks().size());
        assertEquals(coinsBefore, aquarium.getCoins());
    }

    @Test
    void aBoughtTankAddsItsPriceToTheValue() {
        Aquarium aquarium = new Aquarium(150);
        int before = aquarium.value();
        aquarium.buyTank(TankSize.LARGE);
        assertEquals(before + 150, aquarium.value());
    }
}

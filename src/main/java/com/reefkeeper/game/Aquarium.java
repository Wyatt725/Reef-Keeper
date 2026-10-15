// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** A player's whole aquarium: their tanks and their coins. */
public class Aquarium {

    public static final int STARTING_COINS = 50;
    public static final int VALUE_PER_FISH = 10;
    public static final int VALUE_PER_DECORATION = 25;

    private final List<Tank> tanks = new ArrayList<>();
    private int coins;
    private int nextTankId = 1;

    public Aquarium(int coins) {
        this.coins = coins;
    }

    /** What every new player gets: one small tank and 50 coins. Given (but needs step 2a to work). */
    public static Aquarium starter() {
        Aquarium aquarium = new Aquarium(STARTING_COINS);
        aquarium.addTank(TankSize.SMALL);
        return aquarium;
    }

    public int getCoins() {
        return coins;
    }

    /** Read-only view of the tanks, so nothing outside this class can add or remove tanks directly. */
    public List<Tank> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    /**
     * STEP 2a. Creates a new empty tank of the given size, adds it to the list and returns it.
     * Give it the id nextTankId, then increase nextTankId so the next tank gets a new id.
     */
    public Tank addTank(TankSize size) {
        // TODO step 2a
        throw new UnsupportedOperationException("TODO step 2a: Aquarium.addTank");
    }

    /**
     * STEP 2b. The tank with this id, or Optional.empty() if there is none.
     * Hint: loop over tanks, or use tanks.stream().filter(...).findFirst().
     */
    public Optional<Tank> findTank(int tankId) {
        // TODO step 2b
        throw new UnsupportedOperationException("TODO step 2b: Aquarium.findTank");
    }

    /** STEP 2c. Number of fish across all tanks. */
    public int totalFish() {
        // TODO step 2c
        throw new UnsupportedOperationException("TODO step 2c: Aquarium.totalFish");
    }

    /** STEP 2d. Free space across all tanks. */
    public int totalFreeSpace() {
        // TODO step 2d
        throw new UnsupportedOperationException("TODO step 2d: Aquarium.totalFreeSpace");
    }

    /**
     * STEP 2e. The leaderboard score:
     * 10 per fish + 25 per decoration + the price of every tank.
     * Use the constants above and TankSize.price().
     */
    public int value() {
        // TODO step 2e
        throw new UnsupportedOperationException("TODO step 2e: Aquarium.value");
    }
}

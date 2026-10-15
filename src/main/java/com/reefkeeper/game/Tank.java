// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.game;

/**
 * One tank in a player's aquarium. It knows its size, how many fish are in it
 * and how many decorations it has. It protects its own rule: never more fish
 * than its capacity (high cohesion: the rule lives with the data it is about).
 */
public class Tank {

    private final int id;
    private final TankSize size;
    private int fishCount;
    private int decorations;

    public Tank(int id, TankSize size) {
        this.id = id;
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public TankSize getSize() {
        return size;
    }

    public int getCapacity() {
        return size.capacity();
    }

    public int getFishCount() {
        return fishCount;
    }

    public int getDecorations() {
        return decorations;
    }

    /**
     * STEP 1a. How many more fish fit in this tank.
     * Example: a small tank (capacity 10) with 7 fish has 3 free spaces.
     */
    public int freeSpace() {
        // TODO step 1a
        throw new UnsupportedOperationException("TODO step 1a: Tank.freeSpace");
    }

    /**
     * STEP 1b. True if {@code fish} more fish would fit.
     * Hint: you can use freeSpace().
     */
    public boolean canHold(int fish) {
        // TODO step 1b
        throw new UnsupportedOperationException("TODO step 1b: Tank.canHold");
    }

    /**
     * STEP 1c. Puts {@code fish} more fish in the tank.
     *
     * @throws IllegalArgumentException if fish is negative
     * @throws IllegalStateException    if they do not fit (the tank must not change)
     */
    public void addFish(int fish) {
        // TODO step 1c
        throw new UnsupportedOperationException("TODO step 1c: Tank.addFish");
    }

    /** Used in Week 9 by the treasure check. Given. */
    public void addDecorations(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        decorations += count;
    }
}

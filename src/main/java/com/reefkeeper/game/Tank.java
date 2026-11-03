// Reef Keeper - CIS045-3 coursework.
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

    public int freeSpace() {
        return getCapacity()-fishCount;
    }

    public boolean canHold(int fish) {
        return freeSpace() >= fish;
    }

    public void addFish(int fish) {
    	if (fish < 0) {
        	throw new IllegalArgumentException("Fish count cannot be negative");
        }
        if (!canHold(fish)) {
        	throw new IllegalStateException("Not enough space in the tank");
        }
        fishCount += fish;
    }

    /** Used in Week 9 by the treasure check. Given. */
    public void addDecorations(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
        decorations += count;
    }
}

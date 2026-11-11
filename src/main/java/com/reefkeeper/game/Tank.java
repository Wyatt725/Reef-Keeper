// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

import java.time.Duration;
import java.time.Instant;

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
    
    /** How long one feeding lasts. */
    public static final Duration FOOD_TIME = Duration.ofMinutes(30);

    private Instant fedUntil;         // when the food runs out; null = never fed
    private boolean hungerAnnounced;  // a TankHungryEvent was already published for this hunger

    public Instant getFedUntil() {
        return fedUntil;
    }

    /** Fills the tank's food until the given time. */
    public void feed(Instant until) {
        fedUntil = until;
        hungerAnnounced = false;
    }

    /** True if the tank has no food at that time. Fed until 10:30 means fed at 10:30, hungry after. */
    public boolean isHungry(Instant at) {
        return fedUntil == null || at.isAfter(fedUntil);
    }

    /** How much food time is left (zero when hungry). */
    public Duration foodLeft(Instant now) {
        return isHungry(now) ? Duration.ZERO : Duration.between(now, fedUntil);
    }

    boolean isHungerAnnounced() {
        return hungerAnnounced;
    }

    void markHungerAnnounced() {
        hungerAnnounced = true;
    }
}

// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

/** A player's whole aquarium: their tanks and their coins. */
public class Aquarium {

    public static final int STARTING_COINS = 50;
    public static final int VALUE_PER_FISH = 10;
    public static final int VALUE_PER_DECORATION = 25;
    public static final int MAX_TANKS = 6;

    private final List<Tank> tanks = new ArrayList<>();
    private int coins;
    private int nextTankId = 1;

    public Aquarium(int coins) {
        this.coins = coins;
    }

    /** What every new player gets: one small tank and 50 coins.*/
    public static Aquarium starter() {
        Aquarium aquarium = new Aquarium(STARTING_COINS);
        aquarium.addTank(TankSize.SMALL);
        return aquarium;
    }

    public int getCoins() {
        return coins;
    }

    public List<Tank> getTanks() {
        return Collections.unmodifiableList(tanks);
    }

    public Tank addTank(TankSize size) {
        Tank tank = new Tank(nextTankId++, size);
        tanks.add(tank);
        return tank;
    }

    public Optional<Tank> findTank(int tankId) {
        return tanks.stream().filter(tank -> tank.getId() == tankId).findFirst();
    }

    /* Number of fish across all tanks.*/
    public int totalFish() {
        return tanks.stream().mapToInt(Tank::getFishCount).sum();
    }

    /* Free space across all tanks.*/
    public int totalFreeSpace() {
        return tanks.stream().mapToInt(Tank::freeSpace).sum();
    }

    /* The leaderboard score:
     * 10 per fish + 25 per decoration + the price of every tank. */
    public int value() {
        int fishValue = totalFish() * VALUE_PER_FISH;
        
        int decorationValue = tanks.stream().mapToInt(Tank::getDecorations).sum() * VALUE_PER_DECORATION;
        
        int tankValue = tanks.stream().mapToInt(tank -> tank.getSize().price()).sum();
        
        return fishValue + decorationValue + tankValue;
    }
    
    /** Visitors' coins go in here.*/
    public void addCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        coins += amount;
    }

    /**
     * Buys a new tank of the given size with coins and returns it.
     */
    public Tank buyTank(TankSize size) {
    	if (tanks.size() >= MAX_TANKS) {
            throw new IllegalStateException("Your aquarium is full: " + MAX_TANKS + " tanks at most");
        }
        if (coins < size.price()) {
            throw new IllegalStateException("Not enough coins: a " + size.label().toLowerCase() + " costs " + size.price());
        }
        
        coins -= size.price();
        return addTank(size);
    }
    
    public int totalDecorations() {
        return tanks.stream().mapToInt(Tank::getDecorations).sum();
    }

    /** Takes coins off, e.g. for food. Throws, and changes nothing, if there aren't enough. */
    public void spendCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative");
        }
        if (coins < amount) {
            throw new IllegalStateException("Not enough coins: this costs " + amount);
        }
        coins -= amount;
    }

    public int fishInFedTanks(Instant at) {
    	return tanks.stream().filter(tank -> !tank.isHungry(at)).mapToInt(Tank::getFishCount).sum();
    }
}

// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

/** The two kinds of tank a player can own, with how many fish they hold and what they cost. */
public enum TankSize {
    SMALL("Small tank", 10, 50),
    LARGE("Large tank", 25, 150);

    private final String label;
    private final int capacity;
    private final int price;

    TankSize(String label, int capacity, int price) {
        this.label = label;
        this.capacity = capacity;
        this.price = price;
    }

    public String label() {
        return label;
    }

    public int capacity() {
        return capacity;
    }

    public int price() {
        return price;
    }
}

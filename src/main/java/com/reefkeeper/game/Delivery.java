// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.game;

import com.reefkeeper.puzzle.Puzzle;

import java.time.Instant;

/**
 * One delivery crate. Its puzzle (the Fish API picture and the answer) is only
 * fetched when the player opens it, so waiting crates cost no API calls.
 */
public class Delivery {

    private final int id;
    private final Instant arrivedAt;
    private DeliveryStatus status = DeliveryStatus.WAITING;
    private Puzzle puzzle;    // null until opened
    private Instant deadline; // null until opened

    public Delivery(int id, Instant arrivedAt) {
        this.id = id;
        this.arrivedAt = arrivedAt;
    }

    public int getId() {
        return id;
    }

    public Instant getArrivedAt() {
        return arrivedAt;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public Instant getDeadline() {
        return deadline;
    }

    /** The number of fish in the crate: the answer. Server-side only, never send it before the player answers. */
    public int fishCount() {
        if (puzzle == null) {
            throw new IllegalStateException("Delivery " + id + " has not been opened yet");
        }
        return puzzle.fishCount();
    }

    public boolean isExpired(Instant now) {
        return status == DeliveryStatus.OPEN && deadline != null && now.isAfter(deadline);
    }

    // ---- State changes. Given. Package-private: only classes in the game package
    // ---- (DeliveryService) can move a delivery from one status to the next.

    void open(Puzzle puzzle, Instant deadline) {
        requireStatus(DeliveryStatus.WAITING);
        this.puzzle = puzzle;
        this.deadline = deadline;
        this.status = DeliveryStatus.OPEN;
    }

    void accept() {
        requireStatus(DeliveryStatus.OPEN);
        status = DeliveryStatus.ACCEPTED;
    }

    void returnToSender() {
        requireStatus(DeliveryStatus.OPEN);
        status = DeliveryStatus.RETURNED;
    }

    void place() {
        requireStatus(DeliveryStatus.ACCEPTED);
        status = DeliveryStatus.PLACED;
    }

    private void requireStatus(DeliveryStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Delivery " + id + " is " + status + ", expected " + expected);
        }
    }
    
    private boolean treasureChecked;  // the player can count the chests once per delivery
    private int decorationsWon;       // what the treasure check won; goes into the tank with the fish

    /** The number of treasure chests in the crate: the bonus answer. Server-side only. */
    public int chestCount() {
        if (puzzle == null) {
            throw new IllegalStateException("Delivery " + id + " has not been opened yet");
        }
        return puzzle.chestCount();
    }

    public boolean isTreasureChecked() {
        return treasureChecked;
    }

    public int getDecorationsWon() {
        return decorationsWon;
    }

    boolean checkTreasure(int chests) {
    	if (status != DeliveryStatus.ACCEPTED) {
            throw new IllegalStateException("Delivery " + id + " has no treasure check now");
        }
        if (treasureChecked) {
            throw new IllegalStateException("You already checked the treasure in delivery " + id);
        }
        treasureChecked = true;
        if (chests == chestCount()) {
            decorationsWon = chestCount();
            return true;
        }
        return false;
    }
}

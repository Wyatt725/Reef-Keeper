// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026.
// Method bodies marked TODO are written by me.
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

    /**
     * STEP 3. True if the delivery is OPEN and {@code now} is after its deadline.
     * A delivery that is not OPEN is never expired.
     * Hint: Instant has isAfter(...).
     */
    public boolean isExpired(Instant now) {
        // TODO step 3
        throw new UnsupportedOperationException("TODO step 3: Delivery.isExpired");
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
}

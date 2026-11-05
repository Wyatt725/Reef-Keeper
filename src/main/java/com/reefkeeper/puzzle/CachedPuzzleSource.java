// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Wraps another PuzzleSource and remembers the last few puzzles it produced.
 * If the wrapped source fails (e.g. the Fish API is down during your demo),
 * it serves a saved puzzle instead, so the game keeps working.
 *
 * Because it is itself a PuzzleSource, the game cannot tell the difference.
 * That is the point to make in the video about low coupling.
 */
public class CachedPuzzleSource implements PuzzleSource {

    private final PuzzleSource live;
    private final int capacity;
    private final Random random;
    private final List<Puzzle> saved = new ArrayList<>();

    public CachedPuzzleSource(PuzzleSource live, int capacity, Random random) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.live = live;
        this.capacity = capacity;
        this.random = random;
    }

    // synchronized: several players' requests can arrive at the same time
    @Override
    public synchronized Puzzle nextPuzzle() {
        try {
            Puzzle fresh = live.nextPuzzle();
            remember(fresh);
            return fresh;
        } catch (PuzzleUnavailableException e) {
            if (saved.isEmpty()) {
                throw e; // nothing saved yet, so we really cannot help
            }
            return saved.get(random.nextInt(saved.size()));
        }
    }

    public synchronized int savedCount() {
        return saved.size();
    }

    private void remember(Puzzle puzzle) {
        saved.add(puzzle);
        if (saved.size() > capacity) {
            saved.remove(0); // drop the oldest
        }
    }
}

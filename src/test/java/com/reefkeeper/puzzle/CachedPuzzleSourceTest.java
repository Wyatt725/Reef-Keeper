// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.puzzle;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Uses a fake PuzzleSource (a lambda) instead of the real Fish API.
 * This only works because CachedPuzzleSource depends on the interface,
 * not on FishApiPuzzleSource: low coupling makes testing easy.
 */
class CachedPuzzleSourceTest {

    @Test
    void passesThroughWhenLiveSourceWorks() {
        AtomicInteger counter = new AtomicInteger();
        PuzzleSource fake = () -> new Puzzle("img", counter.incrementAndGet(), 0);
        CachedPuzzleSource cached = new CachedPuzzleSource(fake, 10, new Random(1));

        assertEquals(1, cached.nextPuzzle().fishCount());
        assertEquals(2, cached.nextPuzzle().fishCount());
        assertEquals(2, cached.savedCount());
    }

    @Test
    void servesSavedPuzzleWhenLiveSourceFails() {
        AtomicBoolean apiDown = new AtomicBoolean(false);
        PuzzleSource fake = () -> {
            if (apiDown.get()) {
                throw new PuzzleUnavailableException("down");
            }
            return new Puzzle("img", 7, 1);
        };
        CachedPuzzleSource cached = new CachedPuzzleSource(fake, 10, new Random(1));

        cached.nextPuzzle();       // saves one puzzle
        apiDown.set(true);
        assertEquals(7, cached.nextPuzzle().fishCount());
    }

    @Test
    void failsWhenNothingSavedYet() {
        PuzzleSource broken = () -> {
            throw new PuzzleUnavailableException("down");
        };
        CachedPuzzleSource cached = new CachedPuzzleSource(broken, 10, new Random(1));
        assertThrows(PuzzleUnavailableException.class, cached::nextPuzzle);
    }

    @Test
    void keepsOnlyTheNewestPuzzles() {
        AtomicInteger counter = new AtomicInteger();
        PuzzleSource fake = () -> new Puzzle("img", counter.incrementAndGet(), 0);
        CachedPuzzleSource cached = new CachedPuzzleSource(fake, 3, new Random(1));

        for (int i = 0; i < 5; i++) {
            cached.nextPuzzle();
        }
        assertEquals(3, cached.savedCount());
    }
}

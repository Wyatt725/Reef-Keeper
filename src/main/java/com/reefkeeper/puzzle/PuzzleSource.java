// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.puzzle;

/**
 * Anything that can hand the game a new puzzle.
 *
 * The rest of the game depends only on this interface, never on the Fish API
 * directly (low coupling). Current implementations:
 *   - FishApiPuzzleSource: asks the live Fish API
 *   - CachedPuzzleSource:  wraps another source and falls back to saved puzzles
 * A source of puzzles saved in the database can be added later
 * without changing any game code.
 */
@FunctionalInterface
public interface PuzzleSource {

    /**
     * @return a new puzzle
     * @throws PuzzleUnavailableException if no puzzle can be produced right now
     */
    Puzzle nextPuzzle();
}

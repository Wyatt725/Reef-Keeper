// Reef Keeper - CIS045-3 coursework.
// Source: starter skeleton generated with Claude (Anthropic AI), 8 Oct 2026.
package com.reefkeeper.puzzle;

/** Thrown when a PuzzleSource cannot produce a puzzle (API down, bad reply, timeout). */
public class PuzzleUnavailableException extends RuntimeException {

    public PuzzleUnavailableException(String message) {
        super(message);
    }

    public PuzzleUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

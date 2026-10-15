// Reef Keeper - CIS045-3 coursework.
// Source: starter skeleton generated with Claude (Anthropic AI), 8 Oct 2026.
package com.reefkeeper.web;

import com.reefkeeper.puzzle.Puzzle;
import com.reefkeeper.puzzle.PuzzleSource;
import com.reefkeeper.puzzle.PuzzleUnavailableException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Week 3-4 test bench: one puzzle at a time, no oxygen or scoring yet.
 * In Week 5 the real GameController and GameEngine replace this class.
 *
 *   GET  /api/test-round        -> {"image": "data:image/png;base64,..."}
 *   POST /api/test-round/guess  {"guess": 7} -> {"correct": false, "fish": 8}
 *
 * The answer is kept in the player's HttpSession on the server. The browser only
 * holds the session cookie (JSESSIONID) that points to it, never the answer.
 */
@RestController
@RequestMapping("/api/test-round")
public class TestRoundController {

    private static final String ANSWER_KEY = "testRound.fishCount";

    private final PuzzleSource puzzles;

    // Constructor injection: Spring passes in whatever PuzzleSource PuzzleConfig built.
    public TestRoundController(PuzzleSource puzzles) {
        this.puzzles = puzzles;
    }

    @GetMapping
    public Map<String, String> newRound(HttpSession session) {
        Puzzle puzzle = puzzles.nextPuzzle();
        session.setAttribute(ANSWER_KEY, puzzle.fishCount());
        return Map.of("image", puzzle.imageDataUri());
    }

    @PostMapping("/guess")
    public ResponseEntity<Map<String, Object>> guess(@RequestBody GuessRequest request, HttpSession session) {
        Integer answer = (Integer) session.getAttribute(ANSWER_KEY);
        if (answer == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start a round first"));
        }
        session.removeAttribute(ANSWER_KEY); // one guess per puzzle
        boolean correct = request.guess() == answer;
        return ResponseEntity.ok(Map.of("correct", correct, "fish", answer));
    }

    /** JSON body of a guess, e.g. {"guess": 7}. */
    public record GuessRequest(int guess) {
    }

    @ExceptionHandler(PuzzleUnavailableException.class)
    public ResponseEntity<Map<String, String>> puzzleUnavailable(PuzzleUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", e.getMessage()));
    }
}

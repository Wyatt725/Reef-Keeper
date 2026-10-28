// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.web;

import com.reefkeeper.game.DeliveryService;
import com.reefkeeper.game.PlayerGame;
import com.reefkeeper.puzzle.PuzzleUnavailableException;
import com.reefkeeper.web.ApiModels.CountRequest;
import com.reefkeeper.web.ApiModels.CountResponse;
import com.reefkeeper.web.ApiModels.DeliveryView;
import com.reefkeeper.web.ApiModels.GameView;
import com.reefkeeper.web.ApiModels.PlaceRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The REST API the browser talks to. Thin on purpose: it only translates between HTTP/JSON
 * and the game. No game rules here; they live in DeliveryService (separation of concerns).
 *
 *   GET  /api/game                   -> GameView
 *   POST /api/deliveries/open        -> DeliveryView
 *   POST /api/deliveries/{id}/count  -> CountResponse   body {"count": 7}
 *   POST /api/deliveries/{id}/place  -> GameView        body {"tankId": 1}
 */
@RestController
@RequestMapping("/api")
public class GameController {

    private static final String GAME_KEY = "reefKeeper.game";

    private final DeliveryService deliveries;
    private final Clock clock;

    public GameController(DeliveryService deliveries, Clock clock) {
        this.deliveries = deliveries;
        this.clock = clock;
    }

    /**
     * This browser's game, kept in its HttpSession (the JSESSIONID cookie points to it).
     * Starts a new game the first time. Given. In Week 6 this changes to "load the game
     * of the logged-in player from the database".
     */
    private PlayerGame currentGame(HttpSession session) {
        PlayerGame game = (PlayerGame) session.getAttribute(GAME_KEY);
        if (game == null) {
            game = deliveries.startNewGame();
            session.setAttribute(GAME_KEY, game);
        }
        return game;
    }

    /** Worked example for the three TODO methods below. Given. */
    @GetMapping("/game")
    public GameView getGame(HttpSession session) {
        PlayerGame game = currentGame(session);
        deliveries.catchUp(game);
        return GameView.of(game, deliveries.timeUntilNextDelivery(game));
    }

    /**
     * STEP 9a. Open the next delivery.
     * Get the game, call deliveries.openNext(game), and return DeliveryView.of(delivery, clock.instant()).
     */
    @PostMapping("/deliveries/open")
    public DeliveryView openDelivery(HttpSession session) {
    	PlayerGame game = currentGame(session);
        com.reefkeeper.game.Delivery delivery = deliveries.openNext(game);
        return DeliveryView.of(delivery, clock.instant());
    }

    /**
     * STEP 9b. Check a count.
     * Get the game, call deliveries.submitCount(game, id, request.count()).
     * Then find the delivery again (game.findDelivery(id).orElseThrow()) to read its fishCount(),
     * and return new CountResponse(result.name(), fishCount).
     */
    @PostMapping("/deliveries/{id}/count")
    public CountResponse submitCount(@PathVariable("id") int id, @RequestBody CountRequest request, HttpSession session) {
    	PlayerGame game = currentGame(session);
        com.reefkeeper.game.CountResult result = deliveries.submitCount(game, id, request.count());
        int fishCount = game.findDelivery(id).orElseThrow().fishCount();
        return new CountResponse(result.name(), fishCount);
    }

    /**
     * STEP 9c. Place fish in a tank.
     * Get the game, call deliveries.placeFish(game, id, request.tankId()),
     * then return the updated GameView (same as getGame).
     */
    @PostMapping("/deliveries/{id}/place")
    public GameView placeFish(@PathVariable("id") int id, @RequestBody PlaceRequest request, HttpSession session) {
    	PlayerGame game = currentGame(session);
        deliveries.placeFish(game, id, request.tankId());
        deliveries.catchUp(game);
        return GameView.of(game, deliveries.timeUntilNextDelivery(game));
    }

    // ---- Error handling. Given. Turns the game's exceptions into HTTP status codes
    // ---- with a JSON body like {"error": "No deliveries waiting"} for the browser to show.

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> conflict(IllegalStateException e) {
        return error(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> notFound(NoSuchElementException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PuzzleUnavailableException.class)
    public ResponseEntity<Map<String, String>> puzzleUnavailable(PuzzleUnavailableException e) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private static ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message == null ? "Error" : message));
    }
}

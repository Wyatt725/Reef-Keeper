// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.web;

import com.reefkeeper.game.CoinService;
import com.reefkeeper.game.Delivery;
import com.reefkeeper.game.DeliveryService;
import com.reefkeeper.game.FeedingService;
import com.reefkeeper.game.PlayerGame;
import com.reefkeeper.puzzle.PuzzleUnavailableException;
import com.reefkeeper.store.PlayerGameStore;
import com.reefkeeper.web.ApiModels.BuyTankRequest;
import com.reefkeeper.web.ApiModels.CountRequest;
import com.reefkeeper.web.ApiModels.CountResponse;
import com.reefkeeper.web.ApiModels.DeliveryView;
import com.reefkeeper.web.ApiModels.GameView;
import com.reefkeeper.web.ApiModels.PlaceRequest;
import com.reefkeeper.web.ApiModels.TreasureRequest;
import com.reefkeeper.web.ApiModels.TreasureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Clock;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The REST API the browser talks to. Thin on purpose: it only translates between HTTP/JSON
 * and the game. No game rules here; they live in the game package (separation of concerns).
 *
 *   GET  /api/game                      -> GameView
 *   POST /api/deliveries/open           -> DeliveryView
 *   POST /api/deliveries/{id}/count     -> CountResponse     body {"count": 7}
 *   POST /api/deliveries/{id}/treasure  -> TreasureResponse  body {"chests": 3}   (Week 9)
 *   POST /api/deliveries/{id}/place     -> GameView          body {"tankId": 1}
 *   POST /api/tanks                     -> GameView          body {"size": "SMALL"}
 *   POST /api/tanks/feed                -> GameView                               (Week 9)
 */
@RestController
@RequestMapping("/api")
public class GameController {

    private final DeliveryService deliveries;
    private final CoinService coinService;
    private final FeedingService feeding;
    private final Clock clock;
    private final PlayerGameStore games;

    public GameController(DeliveryService deliveries, CoinService coinService, FeedingService feeding,
                          Clock clock, PlayerGameStore games) {
        this.deliveries = deliveries;
        this.coinService = coinService;
        this.feeding = feeding;
        this.clock = clock;
        this.games = games;
    }

    /**
     * The logged-in player's game, brought up to date before anything else happens:
     * visitors pay for the minutes since the last request, then any tank that has gone
     * hungry since then is announced (Week 9).
     */
    private PlayerGame currentGame(Principal principal) {
        PlayerGame game = games.gameFor(principal.getName());
        coinService.collectIncome(game);
        feeding.announceHunger(game);
        return game;
    }

    /** The GameView for this game right now. */
    private GameView view(PlayerGame game) {
        return GameView.of(game, clock.instant(), deliveries.timeUntilNextDelivery(game),
                coinService.timeUntilVisitors(game), coinService.incomePerVisit(game));
    }

    @GetMapping("/game")
    public GameView getGame(Principal principal) {
        PlayerGame game = currentGame(principal);
        deliveries.catchUp(game);
        return view(game);
    }

    /** Open the next delivery. */
    @PostMapping("/deliveries/open")
    public DeliveryView openDelivery(Principal principal) {
        PlayerGame game = currentGame(principal);
        Delivery delivery = deliveries.openNext(game);
        return DeliveryView.of(delivery, clock.instant());
    }

    /** Check a count. */
    @PostMapping("/deliveries/{id}/count")
    public CountResponse submitCount(@PathVariable("id") int id, @RequestBody CountRequest request, Principal principal) {
        PlayerGame game = currentGame(principal);
        com.reefkeeper.game.CountResult result = deliveries.submitCount(game, id, request.count());
        int fishCount = game.findDelivery(id).orElseThrow().fishCount();
        return new CountResponse(result.name(), fishCount);
    }

    /**
     * Check the player's treasure-chest count for an accepted delivery.
     */
    @PostMapping("/deliveries/{id}/treasure")
    public TreasureResponse checkTreasure(@PathVariable("id") int id, @RequestBody TreasureRequest request, Principal principal) {
    	PlayerGame game = currentGame(principal);
        boolean right = deliveries.submitTreasure(game, id, request.chests());
        Delivery delivery = game.findDelivery(id).orElseThrow();
        return new TreasureResponse( right ? "CORRECT" : "WRONG", delivery.chestCount(), delivery.getDecorationsWon());
    }

    /** Place fish in a tank. */
    @PostMapping("/deliveries/{id}/place")
    public GameView placeFish(@PathVariable("id") int id, @RequestBody PlaceRequest request, Principal principal) {
        PlayerGame game = currentGame(principal);
        deliveries.placeFish(game, id, request.tankId());
        deliveries.catchUp(game);
        return view(game);
    }

    /** Buy a tank. */
    @PostMapping("/tanks")
    public GameView buyTank(@RequestBody BuyTankRequest request, Principal principal) {
        if (request.size() == null) {
            throw new IllegalArgumentException("Choose a tank size: SMALL or LARGE");
        }
        PlayerGame game = currentGame(principal);
        coinService.buyTank(game, request.size());
        return view(game);
    }

    /**
     * Feed every hungry tank.
     */
    @PostMapping("/tanks/feed")
    public GameView feedTanks(Principal principal) {
    	PlayerGame game = currentGame(principal);
        feeding.feedHungryTanks(game);
        return view(game);
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

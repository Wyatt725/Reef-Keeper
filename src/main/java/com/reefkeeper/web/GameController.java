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
import com.reefkeeper.store.PlayerGameStore;
import com.reefkeeper.game.CoinService;
import com.reefkeeper.web.ApiModels.BuyTankRequest;
import java.security.Principal;
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


@RestController
@RequestMapping("/api")
public class GameController {

	private final DeliveryService deliveries;
    private final CoinService coinService;
    private final Clock clock;
    private final PlayerGameStore games;

    public GameController(DeliveryService deliveries, CoinService coinService, Clock clock, PlayerGameStore games) {
        this.deliveries = deliveries;
        this.coinService = coinService;
        this.clock = clock;
        this.games = games;
    }

    /** The logged-in player's game, with the visitors' coins brought up to date. */
    private PlayerGame currentGame(Principal principal) {
        PlayerGame game = games.gameFor(principal.getName());
        coinService.collectIncome(game); // pay for the visits since the last request
        return game;
    }
    
    /** The GameView for this game right now. also says what the visitors pay and when. */
    private GameView view(PlayerGame game) {
        return GameView.of(game, deliveries.timeUntilNextDelivery(game),
                coinService.timeUntilVisitors(game), coinService.incomePerVisit(game));
    }

    @GetMapping("/game")
    public GameView getGame(Principal principal) {
        PlayerGame game = currentGame(principal);
        deliveries.catchUp(game);
        return view(game);
    }
    
    /**
     * Open the next delivery.
     */
    @PostMapping("/deliveries/open")
    public DeliveryView openDelivery(Principal principal) {
    	PlayerGame game = currentGame(principal);
        com.reefkeeper.game.Delivery delivery = deliveries.openNext(game);
        return DeliveryView.of(delivery, clock.instant());
    }

    /**
     * Check a count.
     */
    @PostMapping("/deliveries/{id}/count")
    public CountResponse submitCount(@PathVariable("id") int id, @RequestBody CountRequest request, Principal principal) {
    	PlayerGame game = currentGame(principal);
        com.reefkeeper.game.CountResult result = deliveries.submitCount(game, id, request.count());
        int fishCount = game.findDelivery(id).orElseThrow().fishCount();
        return new CountResponse(result.name(), fishCount);
    }

    /**
     * Place fish in a tank.
     */
    @PostMapping("/deliveries/{id}/place")
    public GameView placeFish(@PathVariable("id") int id, @RequestBody PlaceRequest request, Principal principal) {
    	PlayerGame game = currentGame(principal);
        deliveries.placeFish(game, id, request.tankId());
        deliveries.catchUp(game);
        return view(game);
    }
    
    @PostMapping("/tanks")
    public GameView buyTank(@RequestBody BuyTankRequest request, Principal principal) {
    	if (request.size() == null) {
            throw new IllegalArgumentException("Choose a tank size: SMALL or LARGE");
        }
        PlayerGame game = currentGame(principal);
        coinService.buyTank(game, request.size());
        return view(game);
    }

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

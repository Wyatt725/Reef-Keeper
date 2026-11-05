// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.web;

import com.reefkeeper.game.Delivery;
import com.reefkeeper.game.DeliveryStatus;
import com.reefkeeper.game.LeaderboardEntry;
import com.reefkeeper.game.PlayerGame;
import com.reefkeeper.game.Tank;
import com.reefkeeper.game.TankSize;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * The JSON shapes the server sends and receives. Spring (Jackson) turns these records
 * into JSON and back automatically.
 *
 * They are separate from the game classes on purpose: they decide exactly what the
 * browser is allowed to see. A DeliveryView has the picture but NOT the fish count.
 */
public final class ApiModels {

    private ApiModels() {
    }

    /** One tank as the browser sees it. Week 9 added whether it is hungry and how much food is left. */
    public record TankView(int id, String name, int fishCount, int capacity, int decorations,
                           boolean hungry, long foodSecondsLeft) {

        public static TankView of(Tank tank, Instant now) {
            return new TankView(tank.getId(), tank.getSize().label(), tank.getFishCount(),
                    tank.getCapacity(), tank.getDecorations(),
                    tank.isHungry(now), tank.foodLeft(now).toSeconds());
        }
    }

    /**
     * An accepted delivery whose fish still need a tank. Its fish count is no longer secret.
     * Week 9: decorations = what the treasure check won; they go into the same tank.
     */
    public record PendingFish(int deliveryId, int fish, int decorations) {
    }

    /**
     * Everything the aquarium screen needs.
     * Week 7 added incomePerVisit (coins each visit pays now) and secondsUntilVisitors.
     * Week 9: of() takes the time "now", so each tank can say whether it is hungry.
     */
    public record GameView(int coins, int value, List<TankView> tanks, int waitingDeliveries,
                           long secondsUntilNextDelivery, List<PendingFish> fishToPlace,
                           int incomePerVisit, long secondsUntilVisitors) {

        public static GameView of(PlayerGame game, Instant now, Duration untilNextDelivery,
                                  Duration untilVisitors, int incomePerVisit) {
            List<TankView> tanks = game.getAquarium().getTanks().stream()
                    .map(tank -> TankView.of(tank, now))
                    .toList();
            List<PendingFish> toPlace = game.deliveriesWithStatus(DeliveryStatus.ACCEPTED).stream()
                    .map(d -> new PendingFish(d.getId(), d.fishCount(), d.getDecorationsWon()))
                    .toList();
            return new GameView(game.getAquarium().getCoins(), game.getAquarium().value(), tanks,
                    game.deliveriesWithStatus(DeliveryStatus.WAITING).size(),
                    untilNextDelivery.toSeconds(), toPlace,
                    incomePerVisit, untilVisitors.toSeconds());
        }
    }

    /** An open delivery: the picture and how long is left. Deliberately no fish count. */
    public record DeliveryView(int id, String image, long secondsLeft) {

        public static DeliveryView of(Delivery delivery, Instant now) {
            long secondsLeft = Math.max(0, Duration.between(now, delivery.getDeadline()).toSeconds());
            return new DeliveryView(delivery.getId(), delivery.getPuzzle().imageDataUri(), secondsLeft);
        }
    }

    /** Body of POST /api/deliveries/{id}/count, e.g. {"count": 7}. */
    public record CountRequest(int count) {
    }

    /** Answer to a count. The real fish count is only revealed once the delivery is closed. */
    public record CountResponse(String result, int fish) {
    }

    /** Body of POST /api/deliveries/{id}/place, e.g. {"tankId": 1}. */
    public record PlaceRequest(int tankId) {
    }

    /**
     * Body of POST /api/tanks, e.g. {"size": "SMALL"}. Week 7.
     * Jackson turns the text "SMALL" or "LARGE" into the TankSize enum by itself.
     * Note what is NOT in here: a price. The server decides what a tank costs, never the browser.
     */
    public record BuyTankRequest(TankSize size) {
    }

    /** Body of POST /api/deliveries/{id}/treasure, e.g. {"chests": 3}. Week 9. */
    public record TreasureRequest(int chests) {
    }

    /**
     * Answer to a treasure check: "CORRECT" or "WRONG", the real number of chests (safe to show
     * now: each delivery's treasure can only be checked once), and the decorations won.
     */
    public record TreasureResponse(String result, int chests, int decorations) {
    }

    /**
     * GET /api/leaderboard. Week 9. The top rows, plus the logged-in player's own row
     * (me), which is null only if they have no aquarium yet.
     */
    public record LeaderboardView(List<LeaderboardEntry> top, LeaderboardEntry me) {
    }
}

// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026; visitor fields and
// BuyTankRequest added 3 Nov 2026. Complete as given.
package com.reefkeeper.web;

import com.reefkeeper.game.Delivery;
import com.reefkeeper.game.DeliveryStatus;
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

    /** One tank as the browser sees it. */
    public record TankView(int id, String name, int fishCount, int capacity, int decorations) {

        public static TankView of(Tank tank) {
            return new TankView(tank.getId(), tank.getSize().label(), tank.getFishCount(),
                    tank.getCapacity(), tank.getDecorations());
        }
    }

    /** An accepted delivery whose fish still need a tank. Its fish count is no longer secret. */
    public record PendingFish(int deliveryId, int fish) {
    }

    /**
     * Everything the aquarium screen needs.
     * Week 7 added incomePerVisit (coins each visit pays now) and secondsUntilVisitors.
     */
    public record GameView(int coins, int value, List<TankView> tanks, int waitingDeliveries,
                           long secondsUntilNextDelivery, List<PendingFish> fishToPlace,
                           int incomePerVisit, long secondsUntilVisitors) {

        public static GameView of(PlayerGame game, Duration untilNextDelivery,
                                  Duration untilVisitors, int incomePerVisit) {
            List<TankView> tanks = game.getAquarium().getTanks().stream().map(TankView::of).toList();
            List<PendingFish> toPlace = game.deliveriesWithStatus(DeliveryStatus.ACCEPTED).stream()
                    .map(d -> new PendingFish(d.getId(), d.fishCount()))
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
}

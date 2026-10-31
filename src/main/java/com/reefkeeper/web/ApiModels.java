// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.web;

import com.reefkeeper.game.Delivery;
import com.reefkeeper.game.DeliveryStatus;
import com.reefkeeper.game.PlayerGame;
import com.reefkeeper.game.Tank;

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

    /** Everything the aquarium screen needs. */
    public record GameView(int coins, int value, List<TankView> tanks, int waitingDeliveries,
                           long secondsUntilNextDelivery, List<PendingFish> fishToPlace) {

        public static GameView of(PlayerGame game, Duration untilNextDelivery) {
            List<TankView> tanks = game.getAquarium().getTanks().stream().map(TankView::of).toList();
            List<PendingFish> toPlace = game.deliveriesWithStatus(DeliveryStatus.ACCEPTED).stream()
                    .map(d -> new PendingFish(d.getId(), d.fishCount()))
                    .toList();
            return new GameView(game.getAquarium().getCoins(), game.getAquarium().value(), tanks,
                    game.deliveriesWithStatus(DeliveryStatus.WAITING).size(),
                    untilNextDelivery.toSeconds(), toPlace);
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
}

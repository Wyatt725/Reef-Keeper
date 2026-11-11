// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Everything about one player's game: their aquarium, their deliveries, and the two
 * timestamps the game is worked out from (last delivery, last visitors).
 *
 * One PlayerGame per account, kept in memory by InMemoryPlayerGameStore.
 */
public class PlayerGame {

    private final Aquarium aquarium;
    private final List<Delivery> deliveries = new ArrayList<>();
    private Instant lastDeliveryAt;
    private Instant lastIncomeAt;
    private int nextDeliveryId = 1;

    /**
     * A game that starts at {@code startedAt}: deliveries and visitors are both counted from then,
     * and every tank it starts with is fed for Tank.FOOD_TIME (Week 9), so a new player isn't
     * greeted by a hungry tank.
     */
    public PlayerGame(Aquarium aquarium, Instant startedAt) {
        this.aquarium = aquarium;
        this.lastDeliveryAt = startedAt;
        this.lastIncomeAt = startedAt;
        for (Tank tank : aquarium.getTanks()) {
            tank.feed(startedAt.plus(Tank.FOOD_TIME));
        }
    }

    public Aquarium getAquarium() {
        return aquarium;
    }

    /** When the most recent delivery was due. The next one is due DELIVERY_INTERVAL after this. */
    public Instant getLastDeliveryAt() {
        return lastDeliveryAt;
    }

    void setLastDeliveryAt(Instant lastDeliveryAt) {
        this.lastDeliveryAt = lastDeliveryAt;
    }

    /** When visitors last paid. The next visit is due CoinService.VISIT_INTERVAL after this. Week 7. */
    public Instant getLastIncomeAt() {
        return lastIncomeAt;
    }

    void setLastIncomeAt(Instant lastIncomeAt) {
        this.lastIncomeAt = lastIncomeAt;
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }

    /** All deliveries with this status, oldest first. */
    public List<Delivery> deliveriesWithStatus(DeliveryStatus status) {
        return deliveries.stream().filter(d -> d.getStatus() == status).toList();
    }

    public Optional<Delivery> findDelivery(int deliveryId) {
        return deliveries.stream().filter(d -> d.getId() == deliveryId).findFirst();
    }

    /** Creates a new WAITING delivery that arrived at the given time. */
    Delivery addDelivery(Instant arrivedAt) {
        Delivery delivery = new Delivery(nextDeliveryId++, arrivedAt);
        deliveries.add(delivery);
        return delivery;
    }
}

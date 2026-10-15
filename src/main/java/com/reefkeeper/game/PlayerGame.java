// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.game;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Everything about one player's game: their aquarium and their deliveries.
 *
 * Week 5: one PlayerGame is kept in each browser's HttpSession.
 * Week 6: it is saved in the database and linked to the player's account.
 */
public class PlayerGame {

    private final Aquarium aquarium;
    private final List<Delivery> deliveries = new ArrayList<>();
    private Instant lastDeliveryAt;
    private int nextDeliveryId = 1;

    public PlayerGame(Aquarium aquarium, Instant lastDeliveryAt) {
        this.aquarium = aquarium;
        this.lastDeliveryAt = lastDeliveryAt;
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

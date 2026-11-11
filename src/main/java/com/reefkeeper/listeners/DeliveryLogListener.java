// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.listeners;

import com.reefkeeper.game.events.DeliveryAcceptedEvent;
import com.reefkeeper.game.events.DeliveryArrivedEvent;
import com.reefkeeper.game.events.DeliveryReturnedEvent;
import com.reefkeeper.game.events.FishPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to game events by writing a line to the Eclipse Console.
 *
 * The game never calls this class: it only publishes events, and Spring delivers each
 * event to every @EventListener method whose parameter type matches.
 */
@Component
public class DeliveryLogListener {

    private static final Logger log = LoggerFactory.getLogger(DeliveryLogListener.class);

    @EventListener
    public void onDeliveryArrived(DeliveryArrivedEvent event) {
        log.info("Delivery {} arrived", event.deliveryId());
    }

    @EventListener
    public void onDeliveryAccepted(DeliveryAcceptedEvent event) {
        log.info("Delivery {} accepted: {} fish", event.deliveryId(), event.fish());
    }

    @EventListener
    public void onDeliveryReturned(DeliveryReturnedEvent event) {
        log.info("Delivery {} returned: {}", event.deliveryId(), event.reason());
    }

    @EventListener
    public void onFishPlaced(FishPlacedEvent event) {
        log.info("{} fish from delivery {} placed in tank {}", event.fish(), event.deliveryId(), event.tankId());
    }
}

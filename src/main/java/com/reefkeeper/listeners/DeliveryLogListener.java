// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026.
// Methods marked TODO are written by me.
package com.reefkeeper.listeners;

import com.reefkeeper.game.events.DeliveryArrivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Reacts to game events by writing a line to the Eclipse Console.
 *
 * The game never calls this class: it only publishes events, and Spring delivers each
 * event to every @EventListener method whose parameter type matches. In Week 7 an
 * IncomeListener and in Week 9 a leaderboard listener are added the same way,
 * without changing DeliveryService at all.
 */
@Component
public class DeliveryLogListener {

    private static final Logger log = LoggerFactory.getLogger(DeliveryLogListener.class);

    @EventListener
    public void onDeliveryArrived(DeliveryArrivedEvent event) {
        log.info("Delivery {} arrived", event.deliveryId());
    }

    // TODO step 8: add three more @EventListener methods, one each for
    //   DeliveryAcceptedEvent  -> log "Delivery {} accepted: {} fish"
    //   DeliveryReturnedEvent  -> log "Delivery {} returned: {}" with the reason
    //   FishPlacedEvent        -> log "{} fish from delivery {} placed in tank {}"
    // Copy the shape of onDeliveryArrived above and add the imports.
}

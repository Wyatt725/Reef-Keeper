// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.listeners;

import com.reefkeeper.game.events.TankBoughtEvent;
import com.reefkeeper.game.events.VisitorsPaidEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Writes a Console line whenever coins come in or go out.
 *
 * A new listener for new events, added without changing a single line of CoinService
 * or DeliveryService: the game publishes, Spring delivers, and this class only has to
 * say which event types it cares about (the parameter type of each method).
 */
@Component
public class CoinLogListener {

    private static final Logger log = LoggerFactory.getLogger(CoinLogListener.class);

    /**
     * Log how many visits there were and how many coins they paid.
     */
    @EventListener
    public void onVisitorsPaid(VisitorsPaidEvent event) {
    	log.info("Visitors came {} time(s) and paid {} coins", event.visits(), event.coins());
    }

    /**
     * Log the tank that was bought.
     */
    @EventListener
    public void onTankBought(TankBoughtEvent event) {
    	log.info("Bought tank {} ({}) for {} coins", event.tankId(), event.size(), event.price());
    }
}

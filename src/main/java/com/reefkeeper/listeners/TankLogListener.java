// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 3 Nov 2026. Complete as given.
package com.reefkeeper.listeners;

import com.reefkeeper.game.events.TankHungryEvent;
import com.reefkeeper.game.events.TanksFedEvent;
import com.reefkeeper.game.events.TreasureFoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Writes a Console line for the Week 9 events: treasure found, a tank going hungry,
 * tanks being fed. Like the other listeners, the game never calls this class.
 */
@Component
public class TankLogListener {

    private static final Logger log = LoggerFactory.getLogger(TankLogListener.class);

    @EventListener
    public void onTreasureFound(TreasureFoundEvent event) {
        log.info("Treasure in delivery {}: {} decoration(s) won", event.deliveryId(), event.decorations());
    }

    @EventListener
    public void onTankHungry(TankHungryEvent event) {
        log.info("Tank {} is hungry: visitors pay nothing for it until it is fed", event.tankId());
    }

    @EventListener
    public void onTanksFed(TanksFedEvent event) {
        log.info("Fed {} tank(s) for {} coins", event.tanks(), event.coins());
    }
}

// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.config;

import com.reefkeeper.game.DeliveryService;
import com.reefkeeper.game.GameEventPublisher;
import com.reefkeeper.puzzle.PuzzleSource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Builds the game objects and connects them to Spring (the game package itself stays plain Java). */
@Configuration
public class GameConfig {

    /** The real clock. Tests use their own clock instead, so they can "move time forward". */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public DeliveryService deliveryService(PuzzleSource puzzles, Clock clock,
                                           ApplicationEventPublisher springEvents) {
        // Adapter: our small GameEventPublisher interface, implemented by Spring's event system.
        // Every event published by the game reaches any @EventListener method in the app.
        GameEventPublisher events = springEvents::publishEvent;
        return new DeliveryService(puzzles, clock, events);
    }
}

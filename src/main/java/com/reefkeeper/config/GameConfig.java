// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026; CoinService bean added 3 Nov 2026.
// Complete as given.
package com.reefkeeper.config;

import com.reefkeeper.game.CoinService;
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

    /**
     * Adapter: our small GameEventPublisher interface, implemented by Spring's event system.
     * Every event published by the game reaches any @EventListener method in the app.
     */
    @Bean
    public GameEventPublisher gameEventPublisher(ApplicationEventPublisher springEvents) {
        return springEvents::publishEvent;
    }

    @Bean
    public DeliveryService deliveryService(PuzzleSource puzzles, Clock clock, GameEventPublisher events) {
        return new DeliveryService(puzzles, clock, events);
    }

    /** Week 7: the coin rules share the same clock and the same event publisher. */
    @Bean
    public CoinService coinService(Clock clock, GameEventPublisher events) {
        return new CoinService(clock, events);
    }
}

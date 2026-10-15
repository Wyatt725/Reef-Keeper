// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. Complete as given.
package com.reefkeeper.game;

/**
 * How the game announces that something happened (a delivery arrived, was accepted...).
 *
 * The game package depends only on this small interface, not on Spring. GameConfig
 * connects it to Spring's ApplicationEventPublisher, and the tests connect it to a
 * simple list. That is low coupling: the game rules don't know who is listening.
 */
@FunctionalInterface
public interface GameEventPublisher {

    void publish(Object event);
}

// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

import com.reefkeeper.game.events.TankBoughtEvent;
import com.reefkeeper.game.events.VisitorsPaidEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Coins worked out from timestamps: the clock is moved by hand, so "wait 3 minutes"
 * takes no real time, and every event the service publishes lands in a list.
 */
class CoinServiceTest {

    private static final Instant START = Instant.parse("2026-11-03T10:00:00Z");

    private MutableClock clock;
    private List<Object> events;
    private CoinService service;
    private PlayerGame game;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(START);
        events = new ArrayList<>();
        service = new CoinService(clock, events::add);
        game = new PlayerGame(Aquarium.starter(), START); // 50 coins, 1 small tank
        game.getAquarium().getTanks().get(0).addFish(5);  // 5 fish -> 5 coins a visit
    }

    private int coins() {
        return game.getAquarium().getCoins();
    }

    // ---- Given: incomePerVisit and timeUntilVisitors

    @Test
    void incomePerVisitIsOneCoinPerFish() {
        assertEquals(5, service.incomePerVisit(game));
    }

    @Test
    void timeUntilVisitorsCountsDownFromOneMinute() {
        assertEquals(Duration.ofMinutes(1), service.timeUntilVisitors(game));
        clock.advance(Duration.ofSeconds(45));
        assertEquals(Duration.ofSeconds(15), service.timeUntilVisitors(game));
    }

    // ---- Step 2: collectIncome

    @Test
    void noVisitorsBeforeTheFirstMinute() {
        clock.advance(Duration.ofSeconds(59));
        assertEquals(0, service.collectIncome(game));
        assertEquals(50, coins());
        assertTrue(events.isEmpty());
    }

    @Test
    void oneVisitAfterOneMinutePaysOneCoinPerFish() {
        clock.advance(Duration.ofMinutes(1));
        assertEquals(5, service.collectIncome(game));
        assertEquals(55, coins());
        assertEquals(List.of(new VisitorsPaidEvent(1, 5)), events);
    }

    @Test
    void aPlayerWhoWasAwayGetsPaidForEveryVisitTheyMissed() {
        clock.advance(Duration.ofMinutes(10));
        assertEquals(50, service.collectIncome(game));
        assertEquals(100, coins());
        assertEquals(List.of(new VisitorsPaidEvent(10, 50)), events);
    }

    @Test
    void collectingTwiceDoesNotPayTwice() {
        clock.advance(Duration.ofMinutes(2));
        service.collectIncome(game);
        assertEquals(0, service.collectIncome(game));
        assertEquals(60, coins());
        assertEquals(1, events.size());
    }

    @Test
    void leftoverSecondsAreKeptForTheNextVisit() {
        clock.advance(Duration.ofSeconds(90)); // 1 visit + 30 s left over
        service.collectIncome(game);
        assertEquals(55, coins());
        assertEquals(Duration.ofSeconds(30), service.timeUntilVisitors(game));
        clock.advance(Duration.ofSeconds(30)); // the next visit is now due
        assertEquals(5, service.collectIncome(game));
        assertEquals(60, coins());
    }

    @Test
    void anEmptyAquariumEarnsNothingAndPublishesNothing() {
        PlayerGame empty = new PlayerGame(Aquarium.starter(), START);
        clock.advance(Duration.ofMinutes(3));
        assertEquals(0, service.collectIncome(empty));
        assertEquals(50, empty.getAquarium().getCoins());
        assertTrue(events.isEmpty());
        // The clock still moved on: fish placed now don't earn for the empty minutes
        assertEquals(START.plus(Duration.ofMinutes(3)), empty.getLastIncomeAt());
    }

    // ---- Step 3: buyTank

    @Test
    void buyingATankSpendsCoinsAndPublishesAnEvent() {
        Tank tank = service.buyTank(game, TankSize.SMALL);
        assertEquals(0, coins());
        assertEquals(2, game.getAquarium().getTanks().size());
        assertEquals(List.of(new TankBoughtEvent(tank.getId(), TankSize.SMALL, 50)), events);
    }

    @Test
    void aFailedPurchasePublishesNothing() {
        assertThrows(IllegalStateException.class, () -> service.buyTank(game, TankSize.LARGE));
        assertEquals(50, coins());
        assertTrue(events.isEmpty());
    }

    @Test
    void visitorCoinsCanPayForALargeTank() {
        clock.advance(Duration.ofMinutes(20)); // 20 visits x 5 coins = 100
        service.collectIncome(game);
        assertEquals(150, coins());
        service.buyTank(game, TankSize.LARGE);
        assertEquals(0, coins());
    }
}

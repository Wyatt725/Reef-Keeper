// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 3 Nov 2026. These tests describe steps C1 to C3.
package com.reefkeeper.game;

import com.reefkeeper.game.events.TankHungryEvent;
import com.reefkeeper.game.events.TanksFedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Food: tanks are fed for 30 minutes, then hungry, and visitors only pay for fish in fed
 * tanks. Time is a clock moved by hand, so "an hour later" takes no time at all.
 */
class FeedingTest {

    private static final Instant START = Instant.parse("2026-11-09T10:00:00Z");

    private MutableClock clock;
    private List<Object> events;
    private CoinService coins;
    private FeedingService feeding;
    private PlayerGame game;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(START);
        events = new ArrayList<>();
        coins = new CoinService(clock, events::add);
        feeding = new FeedingService(clock, events::add);
        game = new PlayerGame(Aquarium.starter(), START); // 50 coins, 1 small tank fed until 10:30
        tank(0).addFish(5);
    }

    private Tank tank(int index) {
        return game.getAquarium().getTanks().get(index);
    }

    private int coins() {
        return game.getAquarium().getCoins();
    }

    // ---- Given: Tank food and PlayerGame feeding the starter tank

    @Test
    void aNewGamesTankIsFedForThirtyMinutes() {
        assertEquals(START.plus(Tank.FOOD_TIME), tank(0).getFedUntil());
        assertFalse(tank(0).isHungry(START.plus(Duration.ofMinutes(30))));
        assertTrue(tank(0).isHungry(START.plus(Duration.ofMinutes(31))));
    }

    @Test
    void aTankThatWasNeverFedIsHungry() {
        assertTrue(new Tank(9, TankSize.SMALL).isHungry(START));
    }

    // ---- Step C1: Aquarium.fishInFedTanks

    @Test
    void onlyFishInFedTanksCount() {
        Aquarium aquarium = game.getAquarium();
        Tank second = aquarium.addTank(TankSize.SMALL); // never fed
        second.addFish(3);
        assertEquals(8, aquarium.totalFish());
        assertEquals(5, aquarium.fishInFedTanks(START));
        assertEquals(0, aquarium.fishInFedTanks(START.plus(Duration.ofHours(1))));
    }

    // ---- Step C2: CoinService pays only for fed tanks

    @Test
    void visitorsPayNothingForAHungryTank() {
        clock.advance(Duration.ofMinutes(31));
        assertEquals(0, coins.incomePerVisit(game));
    }

    @Test
    void aPlayerAwayForAnHourIsOnlyPaidUntilTheFoodRanOut() {
        clock.advance(Duration.ofHours(1));
        assertEquals(30 * 5, coins.collectIncome(game)); // 30 fed visits x 5 fish
        assertEquals(50 + 150, coins());
    }

    @Test
    void aBoughtTankComesWithFood() {
        Tank bought = coins.buyTank(game, TankSize.SMALL);
        assertEquals(START.plus(Tank.FOOD_TIME), bought.getFedUntil());
    }

    // ---- Step C3: FeedingService.feedHungryTanks

    @Test
    void feedingCostsFiveCoinsPerHungryTankAndFeedsThemForThirtyMinutes() {
        game.getAquarium().addTank(TankSize.SMALL); // never fed: hungry
        clock.advance(Duration.ofMinutes(40));      // now the starter tank is hungry too
        assertEquals(10, feeding.feedHungryTanks(game));
        assertEquals(40, coins());
        Instant fedUntil = START.plus(Duration.ofMinutes(40)).plus(Tank.FOOD_TIME);
        assertEquals(fedUntil, tank(0).getFedUntil());
        assertEquals(fedUntil, tank(1).getFedUntil());
        assertEquals(List.of(new TanksFedEvent(2, 10)), events);
    }

    @Test
    void tanksThatStillHaveFoodAreNotFedOrCharged() {
        game.getAquarium().addTank(TankSize.SMALL); // hungry; the starter tank is still fed
        assertEquals(5, feeding.feedHungryTanks(game));
        assertEquals(START.plus(Tank.FOOD_TIME), tank(0).getFedUntil());
    }

    @Test
    void noHungryTanksMeansNothingToBuy() {
        assertThrows(IllegalStateException.class, () -> feeding.feedHungryTanks(game));
        assertEquals(50, coins());
        assertTrue(events.isEmpty());
    }

    @Test
    void notEnoughCoinsFeedsNothing() {
        game.getAquarium().spendCoins(48); // 2 coins left
        clock.advance(Duration.ofMinutes(40));
        assertThrows(IllegalStateException.class, () -> feeding.feedHungryTanks(game));
        assertEquals(2, coins());
        assertTrue(tank(0).isHungry(clock.instant()));
        assertTrue(events.isEmpty());
    }

    @Test
    void aFedTankEarnsAgain() {
        clock.advance(Duration.ofMinutes(40));
        coins.collectIncome(game);            // 30 fed visits paid, then hungry
        feeding.feedHungryTanks(game);
        int before = coins();
        clock.advance(Duration.ofMinutes(1));
        assertEquals(5, coins.collectIncome(game));
        assertEquals(before + 5, coins());
    }

    // ---- Given: FeedingService.announceHunger

    @Test
    void hungerIsAnnouncedOncePerHunger() {
        clock.advance(Duration.ofMinutes(31));
        feeding.announceHunger(game);
        feeding.announceHunger(game);
        assertEquals(List.of(new TankHungryEvent(tank(0).getId())), events);
    }
}

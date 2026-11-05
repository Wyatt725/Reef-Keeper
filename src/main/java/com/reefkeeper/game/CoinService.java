// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.game;

import com.reefkeeper.game.events.TankBoughtEvent;
import com.reefkeeper.game.events.VisitorsPaidEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * The coin rules: visitors paying to see the fish, and spending coins in the shop.
 *
 * Its own class, not more methods in DeliveryService: deliveries and coins change for
 * different reasons, so each gets a class with one job (high cohesion). Like
 * DeliveryService it is plain Java and gets its Clock and event publisher through the
 * constructor, so the tests can control time and record events.
 *
 * Time works the same way as deliveries: nothing runs in the background. The game
 * remembers when visitors last paid (PlayerGame.getLastIncomeAt), and collectIncome
 * works out how many visits have happened since then.
 */
public class CoinService {

    /** Visitors come once a minute... */
    public static final Duration VISIT_INTERVAL = Duration.ofMinutes(1);
    /** ...and pay this many coins for every fish in the aquarium. */
    public static final int COINS_PER_FISH = 1;

    private final Clock clock;
    private final GameEventPublisher events;

    public CoinService(Clock clock, GameEventPublisher events) {
        this.clock = clock;
        this.events = events;
    }

    /** How many coins one visit pays right now. Given. */
    public int incomePerVisit(PlayerGame game) {
    	return game.getAquarium().fishInFedTanks(clock.instant()) * COINS_PER_FISH;
    }

    /** How long until the next visit (never negative). Given: same idea as timeUntilNextDelivery. */
    public Duration timeUntilVisitors(PlayerGame game) {
        Instant due = game.getLastIncomeAt().plus(VISIT_INTERVAL);
        Duration left = Duration.between(clock.instant(), due);
        return left.isNegative() ? Duration.ZERO : left;
    }

    /**
     * Pays the player for every visit since the last time this was called, and returns how many coins were paid.
     */
    public int collectIncome(PlayerGame game) {
    	Instant now = clock.instant();
    	int visits = 0;
        int coins = 0;

        while (!game.getLastIncomeAt().plus(VISIT_INTERVAL).isAfter(now)) {
            game.setLastIncomeAt(game.getLastIncomeAt().plus(VISIT_INTERVAL));
            visits++;
            coins += game.getAquarium().fishInFedTanks(game.getLastIncomeAt()) * COINS_PER_FISH;
        }

        if (coins > 0) {
            game.getAquarium().addCoins(coins);
            events.publish(new VisitorsPaidEvent(visits, coins));
        }

        return coins;
    }

    /**
     * Buys a tank of the given size and returns it.
     */
    public Tank buyTank(PlayerGame game, TankSize size) {
    	Tank tank = game.getAquarium().buyTank(size);
    	tank.feed(clock.instant().plus(Tank.FOOD_TIME));
        events.publish(new TankBoughtEvent(tank.getId(), size, size.price()));
        return tank;
    }
}

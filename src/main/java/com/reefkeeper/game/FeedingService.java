// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.game;

import com.reefkeeper.game.events.TankHungryEvent;
import com.reefkeeper.game.events.TanksFedEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * The food rules. Every tank is fed for Tank.FOOD_TIME; after that it is hungry, and
 * visitors pay nothing for the fish in it until the player buys food in the shop.
 *
 * Time works like deliveries and coins: each tank stores when its food runs out
 * (Tank.getFedUntil), and the game compares that with the clock when it needs to know.
 * Nothing counts down in the background, so a player who logs out for an hour comes
 * back to tanks that went hungry at the right moment and earned nothing after that.
 */
public class FeedingService {

    /** What feeding one hungry tank costs. */
    public static final int PRICE_PER_TANK = 5;

    private final Clock clock;
    private final GameEventPublisher events;

    public FeedingService(Clock clock, GameEventPublisher events) {
        this.clock = clock;
        this.events = events;
    }

    /** The tanks that are hungry right now. Given. */
    public List<Tank> hungryTanks(PlayerGame game) {
        Instant now = clock.instant();
        return game.getAquarium().getTanks().stream().filter(tank -> tank.isHungry(now)).toList();
    }

    /**
     * Publishes a TankHungryEvent once for every tank that has gone hungry since the last
     * look. Called on every request, like collectIncome. Given.
     *
     * The tank remembers that its hunger was announced, so a hungry tank produces ONE event,
     * not one per request; feeding it resets that.
     */
    public void announceHunger(PlayerGame game) {
        for (Tank tank : hungryTanks(game)) {
            if (!tank.isHungerAnnounced()) {
                tank.markHungerAnnounced();
                events.publish(new TankHungryEvent(tank.getId()));
            }
        }
    }

    public int feedHungryTanks(PlayerGame game) {
    	List<Tank> hungry = hungryTanks(game);
        if (hungry.isEmpty()) {
            throw new IllegalStateException("No tanks are hungry");
        }
        int cost = hungry.size() * PRICE_PER_TANK;
        game.getAquarium().spendCoins(cost);

        Instant feedUntil = clock.instant().plus(Tank.FOOD_TIME);
        for (Tank tank : hungry) {
            tank.feed(feedUntil);
        }

        events.publish(new TanksFedEvent(hungry.size(), cost));
        return cost;
    }
}

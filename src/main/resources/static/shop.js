// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
//
// The shop page (wireframe 06). The page only SHOWS prices and greys out what the player
// can't afford; the server decides the real price and checks the coins again on every
// purchase, so editing this page in the browser can't buy a free tank or free food.

const MAX_TANKS = 6;       // the same rule as Aquarium.MAX_TANKS on the server
const FEED_PRICE = 5;      // the same rule as FeedingService.PRICE_PER_TANK on the server

/** Shows a message at the bottom of the screen (toast.js). kind: 'correct', 'wrong' or ''. */
function showShopMessage(text, kind) {
    showToast(text, kind);
}

/**
 * Draws the shop from a GameView: coins, income, tank count, hungry tanks, and which
 * buttons can be pressed. A button that can't be pressed says why.
 */
function updateShop(view) {
    updateTopBar(view.coins, view.value); // nav.js
    document.getElementById('shop-coins').textContent = view.coins;
    document.getElementById('shop-income').textContent = view.incomePerVisit;
    document.getElementById('shop-tanks').textContent = view.tanks.length;

    for (const button of document.querySelectorAll('button.buy')) {
        const price = Number(button.dataset.price);
        if (view.tanks.length >= MAX_TANKS) {
            button.textContent = 'Aquarium full';
            button.disabled = true;
        } else if (view.coins < price) {
            button.textContent = 'Need ' + (price - view.coins) + ' more coins';
            button.disabled = true;
        } else {
            button.textContent = 'Buy';
            button.disabled = false;
        }
    }

    const hungry = view.tanks.filter(tank => tank.hungry).length;
    const cost = hungry * FEED_PRICE;
    const feed = document.getElementById('feed-button');
    document.getElementById('shop-hungry').textContent = hungry;
    if (hungry === 0) {
        feed.textContent = 'No hungry tanks';
        feed.disabled = true;
    } else if (view.coins < cost) {
        feed.textContent = 'Need ' + (cost - view.coins) + ' more coins';
        feed.disabled = true;
    } else {
        feed.textContent = 'Feed ' + hungry + (hungry === 1 ? ' tank' : ' tanks') + ' (' + cost + ' coins)';
        feed.disabled = false;
    }
}

/** Loads the player's game and draws the shop. */
async function loadShop() {
    const res = await api('GET', '/api/game');
    if (!res.ok) {
        showShopMessage(res.data.error, 'wrong');
        return;
    }
    updateShop(res.data);
}

/** buys a tank. */
async function buyTank(size) {
	const res = await api('POST', '/api/tanks', { size });
	if (!res.ok) {
	    showShopMessage(res.data.error, 'wrong');
	    return;
	}
	updateShop(res.data);
	showShopMessage('New tank bought! Fill it from your next delivery.', 'correct');
}

/** feeds every hungry tank. */
async function feedTanks() {
	const res = await api('POST', '/api/tanks/feed');
	if (!res.ok) {
	    showShopMessage(res.data.error, 'wrong');
	    return;
	}
	updateShop(res.data);
	showShopMessage('Fed! Your tanks have food for 30 minutes.', 'correct');
}

// ---------- Connect events to handlers ----------

for (const button of document.querySelectorAll('button.buy')) {
    button.addEventListener('click', () => buyTank(button.dataset.size));
}
document.getElementById('feed-button').addEventListener('click', feedTanks);
loadShop();

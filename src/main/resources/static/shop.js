// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
//
// The page only SHOWS prices and greys out what the player
// can't afford; the server decides the real price and checks the coins again on every
// purchase, so editing this page in the browser can't buy a free tank or free food.

/** Shows a message above the shop items. kind: 'correct', 'wrong' or '' (plain). Given. */
function showShopMessage(text, kind) {
    const message = document.getElementById('shop-message');
    message.textContent = text;
    message.className = 'message ' + (kind || '');
}

/**
 * Draws the shop from a GameView: coins, income, tank count, hungry tanks, and which
 * buttons can be pressed. Also keeps the coins and value in the top bar up to date. Given.
 */
function updateShop(view) {
    document.getElementById('coins').textContent = view.coins;
    document.getElementById('value').textContent = view.value;
    document.getElementById('shop-coins').textContent = view.coins;
    document.getElementById('shop-income').textContent = view.incomePerVisit;
    document.getElementById('shop-tanks').textContent = view.tanks.length;
    for (const button of document.querySelectorAll('button.buy')) {
        button.disabled = view.coins < Number(button.dataset.price);
    }
    // Week 9: food. 5 coins for every hungry tank; nothing to buy when none are hungry.
    const hungry = view.tanks.filter(tank => tank.hungry).length;
    document.getElementById('shop-hungry').textContent = hungry;
    document.getElementById('feed-button').disabled = hungry === 0 || view.coins < hungry * 5;
}

/** Loads the player's game and draws the shop. Given. */
async function loadShop() {
    const res = await api('GET', '/api/game');
    if (!res.ok) {
        showShopMessage(res.data.error, 'wrong');
        return;
    }
    updateShop(res.data);
}

/** Week 7: buys a tank. Written by me. */
async function buyTank(size) {
	const res = await api('POST', '/api/tanks', { size: size });
	if (!res.ok) {
	    showShopMessage(res.data.error, 'wrong');
	    return;
	}
	updateShop(res.data);
	showShopMessage('New tank bought! Fill it from your next delivery.', 'correct');
}

/**
 * Runs when the Feed button is clicked.
 */
async function feedTanks() {
	const res = await api('POST', '/api/tanks/feed');
	if (!res.ok) {
	    showShopMessage(res.data.error, 'wrong');
	    return;
	}
	updateShop(res.data);
	showShopMessage('Fed! Your tanks have food for 30 minutes.', 'correct');
}

// ---------- Given: connect events to handlers ----------

for (const button of document.querySelectorAll('button.buy')) {
    button.addEventListener('click', () => buyTank(button.dataset.size));
}
document.getElementById('feed-button').addEventListener('click', feedTanks);
loadShop();

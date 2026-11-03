// Reef Keeper - CIS045-3 coursework.
//
// The shop page (wireframe 06). The page only SHOWS prices and greys out what the player
// can't afford; the server decides the real price and checks the coins again on every purchase.

/** Shows a message above the shop items. kind: 'correct', 'wrong' or '' (plain). Given. */
function showShopMessage(text, kind) {
    const message = document.getElementById('shop-message');
    message.textContent = text;
    message.className = 'message ' + (kind || '');
}

/**
 * Draws the shop from a GameView: coins, income, tank count, and which Buy buttons
 * can be pressed. Also keeps the coins and value in the top bar up to date. Given.
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

async function buyTank(size) {
	const res = await api('POST', '/api/tanks', { size: size });
	if (!res.ok) {
	    showShopMessage(res.data.error, 'wrong');
	    return;
	}
	updateShop(res.data);
	showShopMessage('New tank bought! Fill it from your next delivery.', 'correct');
}

// ---------- Given: connect events to handlers ----------

for (const button of document.querySelectorAll('button.buy')) {
    button.addEventListener('click', () => buyTank(button.dataset.size));
}
loadShop();

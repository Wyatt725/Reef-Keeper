// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 3 Nov 2026.
// Functions marked TODO are written by me.
//
// The shop page (wireframe 06). The page only SHOWS prices and greys out what the player
// can't afford; the server decides the real price and checks the coins again on every
// purchase, so editing this page in the browser can't buy a free tank.

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

/**
 * STEP 7. Runs when a Buy button is clicked. size is 'SMALL' or 'LARGE'.
 *   1. POST to '/api/tanks' with the body { size } (use api(), like placeFish in aquarium.js).
 *   2. If !res.ok: showShopMessage(res.data.error, 'wrong') and stop.
 *      (The server's message says why, e.g. "Not enough coins: a large tank costs 150".)
 *   3. Otherwise: updateShop(res.data) and
 *      showShopMessage('New tank bought! Fill it from your next delivery.', 'correct').
 */
async function buyTank(size) {
    // TODO step 7
}

// ---------- Given: connect events to handlers ----------

for (const button of document.querySelectorAll('button.buy')) {
    button.addEventListener('click', () => buyTank(button.dataset.size));
}
loadShop();

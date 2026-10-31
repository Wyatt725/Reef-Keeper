// Reef Keeper - CIS045-3 coursework.

/** Loads the player's game and name, then fills in their row. Given. */
async function loadLeaderboard() {
    const [gameResponse, meResponse] = await Promise.all([fetch('/api/game'), fetch('/api/me')]);
    if (gameResponse.status === 401 || meResponse.status === 401) {
        window.location.href = '/login.html';
        return;
    }
    const game = await gameResponse.json();
    const me = await meResponse.json();
    renderMyRow(game, me.username);
}

function renderMyRow(game, username) {
	document.getElementById('my-name').textContent = username;

	let totalFish = 0;
	let totalDecorations = 0;

	for (const tank of game.tanks) {
	    totalFish += tank.fishCount;
	    totalDecorations += tank.decorations;
	}

	document.getElementById('my-fish').textContent = totalFish;
	document.getElementById('my-decorations').textContent = totalDecorations;
	document.getElementById('my-value').textContent = game.value;
}

loadLeaderboard();

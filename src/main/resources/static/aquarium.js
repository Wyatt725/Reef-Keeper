// Reef Keeper - CIS045-3 coursework.
//
// Event-driven: nothing here runs on its own. Every function runs because an event
// happened: the page loaded, a button was clicked, the form was submitted, a timer
// ticked, or a reply arrived from the server.

// ---------- Given: small helpers ----------

/** Shortcut for document.getElementById. */
const el = (id) => document.getElementById(id);

/**
 * Calls our Java server and returns { ok, status, data }.
 */
async function api(method, url, body) {
    const options = { method, headers: {} };
    if (body !== undefined) {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(body);
    }
    // every request that changes something must carry the CSRF token
    // (csrfToken() is in account.js, which index.html loads before this file)
    if (method !== 'GET') {
        options.headers['X-XSRF-TOKEN'] = csrfToken();
    }
    const response = await fetch(url, options);
	
    // not logged in, or the session ended -> back to the login page
    if (response.status === 401) {
        window.location.href = '/login.html';
        return { ok: false, status: 401, data: { error: 'Please log in' } };
    }
    let data = null;
    try {
        data = await response.json();
    } catch (e) {
        data = { error: 'The server sent no JSON (HTTP ' + response.status + ')' };
    }
    return { ok: response.ok, status: response.status, data };
}

/** Shows a message above the banner. kind: 'correct', 'wrong' or '' (plain). */
function showMessage(text, kind) {
    el('message').textContent = text;
    el('message').className = 'message ' + (kind || '');
}

// ---------- State the page remembers ----------

let game = null;           // the latest GameView from the server
let openDelivery = null;   // the DeliveryView while the player is counting, else null
let countdownTimer = null; // the id returned by setInterval, so we can stop it

// ---------- Given: drawing the aquarium ----------

/** Draws everything on the aquarium screen from a GameView (see ApiModels.GameView). */
function renderGame(view) {
    el('coins').textContent = view.coins;
    el('value').textContent = view.value;
    el('waiting').textContent = view.waitingDeliveries;
    const min = Math.floor(view.secondsUntilNextDelivery / 60);
    const sec = String(view.secondsUntilNextDelivery % 60).padStart(2, '0');
    el('next-in').textContent = min + ':' + sec;
    el('open-delivery').disabled = view.waitingDeliveries === 0 && openDelivery === null;

    // Tanks
    const tanks = el('tanks');
    tanks.replaceChildren();
    for (const tank of view.tanks) {
        const card = document.createElement('div');
        card.className = 'tank';
        const title = document.createElement('h3');
        title.textContent = tank.name + ' ' + tank.id;
        const fish = document.createElement('div');
        fish.textContent = tank.fishCount + ' / ' + tank.capacity + ' fish';
        const bar = document.createElement('div');
        bar.className = 'bar';
        const fill = document.createElement('span');
        fill.style.width = Math.round(100 * tank.fishCount / tank.capacity) + '%';
        bar.append(fill);
        const decor = document.createElement('div');
        decor.className = 'hint';
        decor.textContent = 'Decorations: ' + tank.decorations;
        card.append(title, fish, bar, decor);
        tanks.append(card);
    }

    // Fish waiting for a tank: one button per tank that has room
    const panel = el('place-panel');
    if (view.fishToPlace.length === 0) {
        panel.hidden = true;
        return;
    }
    const pending = view.fishToPlace[0];
    el('place-fish').textContent = pending.fish;
    const buttons = el('place-tanks');
    buttons.replaceChildren();
    for (const tank of view.tanks) {
        const button = document.createElement('button');
        button.type = 'button';
        button.textContent = tank.name + ' ' + tank.id + ' (room for ' + (tank.capacity - tank.fishCount) + ')';
        button.disabled = tank.capacity - tank.fishCount < pending.fish;
        button.addEventListener('click', () => placeFish(pending.deliveryId, tank.id));
        buttons.append(button);
    }
    panel.hidden = false;
}

/** Asks the server for the latest game and draws it. Given. */
async function loadGame() {
    const res = await api('GET', '/api/game');
    if (!res.ok) {
        showMessage(res.data.error, 'wrong');
        return;
    }
    game = res.data;
    renderGame(game);
}

// ---------- TODO: the four event handlers ----------

/**
 *Runs when "Open delivery" is clicked.
 */
async function openNextDelivery() {
	const res = await api('POST', '/api/deliveries/open');
	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	    return;
	}
	openDelivery = res.data;
	el('delivery-id').textContent = openDelivery.id;
	el('delivery-image').src = openDelivery.image;

	const panel = el('delivery-panel');
	panel.hidden = false;
	const input = el('count-input');
	input.value = '';
	input.focus();

	startCountdown(openDelivery.secondsLeft);
}

/**
 * Shows the 30-second countdown.
 */
function startCountdown(seconds) {
	if (countdownTimer) {
	    clearInterval(countdownTimer);
	}
	el('delivery-timer').textContent = seconds;

	countdownTimer = setInterval(() => {
	    seconds--;
	    el('delivery-timer').textContent = seconds;

	    if (seconds <= 0) {
	        clearInterval(countdownTimer);
	        el('delivery-panel').hidden = true;
	        openDelivery = null;
	        showMessage('Too late: the driver left.', 'wrong');
	        loadGame();
	    }
	}, 1000);
}

/**
 *Runs when the count form is submitted.
 */
async function sendCount(event) {
	event.preventDefault();
	if (countdownTimer) {
	    clearInterval(countdownTimer);
	}

	const countVal = Number(el('count-input').value);
	const deliveryId = openDelivery.id;

	el('delivery-panel').hidden = true;
	openDelivery = null;

	const res = await api('POST', `/api/deliveries/${deliveryId}/count`, { count: countVal });

	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	} else {
	    if (res.data.result === 'CORRECT') {
	        showMessage(`Correct! ${res.data.fish} fish accepted.`, 'correct');
	    } else if (res.data.result === 'WRONG') {
	        showMessage(`Not quite: there were ${res.data.fish} fish. Delivery returned.`, 'wrong');
	    } else if (res.data.result === 'TOO_LATE') {
	        showMessage('Too late: the driver left.', 'wrong');
	    }
	}

	loadGame();
}

/**
 * Runs when a tank button in the place-the-fish panel is clicked.
 */
async function placeFish(deliveryId, tankId) {
	const res = await api('POST', `/api/deliveries/${deliveryId}/place`, { tankId: tankId });

	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	    return;
	}

	game = res.data;
	renderGame(game);
	showMessage('Fish placed in your tank.', 'correct');
}

// ---------- Given: connect events to handlers ----------

el('open-delivery').addEventListener('click', openNextDelivery);
el('count-form').addEventListener('submit', sendCount);
loadGame();

// Ask the server for news every 10 seconds, so new deliveries and the "next delivery"
// time stay up to date. This is polling; the alternative is the server pushing updates
// (WebSockets). A good comparison for the video.
setInterval(loadGame, 10000);

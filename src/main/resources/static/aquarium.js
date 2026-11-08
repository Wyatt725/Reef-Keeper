// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 15 Oct 2026. openNextDelivery,
// startCountdown, sendCount and placeFish written by me (Weeks 5-9).
// Revised by Claude (Anthropic AI), 8 Nov 2026, to fix the design review: messages as
// toasts, "1 delivery" grammar, timers that tick every second, the last 5 seconds in red,
// tank bars that grow, a glow on the tank that got fish, a Feed button on hungry tanks,
// reasons on greyed-out tank buttons, panels that slide in and out, and no repeated
// "Correct!" message.
//
// Event-driven: nothing here runs on its own. Every function runs because an event
// happened: the page loaded, a button was clicked, the form was submitted, a timer
// ticked, or a reply arrived from the server.

// ---------- Small helpers ----------

/** Shortcut for document.getElementById. */
const el = (id) => document.getElementById(id);

/** Shows a message at the bottom of the screen (toast.js). kind: 'correct', 'wrong' or ''. */
function showMessage(text, kind) {
    showToast(text, kind);
}

/** True when the player's device asks for less motion. Then panels just appear and vanish. */
function reduceMotion() {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
}

/** Shows a panel; style.css slides it in from the banner above. */
function showPanel(panel) {
    panel.classList.remove('leaving');
    panel.hidden = false;
}

/** Hides a panel, sliding it back out the way it came in. */
function hidePanel(panel) {
    if (panel.hidden) {
        return;
    }
    if (reduceMotion()) {
        panel.hidden = true;
        return;
    }
    panel.classList.add('leaving');
    setTimeout(() => {
        if (panel.classList.contains('leaving')) {
            panel.hidden = true;
            panel.classList.remove('leaving');
        }
    }, 160);
}

/** "1:05" from 65 seconds. */
function minSec(seconds) {
    return Math.floor(seconds / 60) + ':' + String(seconds % 60).padStart(2, '0');
}

// ---------- State the page remembers ----------

let game = null;             // the latest GameView from the server
let openDelivery = null;     // the DeliveryView while the player is counting, else null
let countdownTimer = null;   // the id returned by setInterval, so we can stop it
let nextDeliveryAt = 0;      // when the next delivery arrives, as Date.now() time
let nextVisitAt = 0;         // when visitors next pay, as Date.now() time
let lastRefresh = 0;         // when we last asked the server, so the clock doesn't ask twice
let fillBefore = new Map();  // tank id -> how full its bar was last time (%), so bars can grow
let justFilledTankId = null; // the tank that just got fish: it glows once

// ---------- Drawing the aquarium ----------

/** Draws everything on the aquarium screen from a GameView (see ApiModels.GameView). */
function renderGame(view) {
    updateTopBar(view.coins, view.value); // nav.js

    const waiting = view.waitingDeliveries;
    el('waiting-text').textContent = waiting === 0 ? 'No deliveries waiting'
        : waiting === 1 ? '1 delivery waiting'
        : waiting + ' deliveries waiting';

    // The server says how many seconds are left; the page counts them down itself
    // every second (tickClocks) until the next refresh corrects it.
    nextDeliveryAt = Date.now() + view.secondsUntilNextDelivery * 1000;
    nextVisitAt = Date.now() + view.secondsUntilVisitors * 1000;
    tickClocks();

    el('income-text').replaceChildren();
    if (view.incomePerVisit > 0) {
        const amount = document.createElement('strong');
        amount.id = 'income';
        amount.textContent = view.incomePerVisit;
        el('income-text').append('Visitors pay ', amount, ' coins every minute');
    } else {
        el('income-text').textContent = 'Visitors pay nothing yet: they pay for fish in fed tanks';
    }

    el('open-delivery').disabled = waiting === 0 && openDelivery === null;

    // Tanks
    const hungryTanks = view.tanks.filter(tank => tank.hungry).length;
    const feedCost = hungryTanks * 5;
    const tanks = el('tanks');
    tanks.replaceChildren();
    const fills = [];
    for (const tank of view.tanks) {
        const card = document.createElement('div');
        card.className = 'tank';
        if (tank.hungry) {
            card.classList.add('hungry');
        }
        if (tank.id === justFilledTankId) {
            card.classList.add('pop');
        }
        const title = document.createElement('h3');
        title.textContent = tank.name + ' ' + tank.id;
        const fish = document.createElement('div');
        fish.textContent = tank.fishCount + ' / ' + tank.capacity + ' fish';
        const bar = document.createElement('div');
        bar.className = 'bar';
        const fill = document.createElement('span');
        const percent = Math.round(100 * tank.fishCount / tank.capacity);
        // Start from last time's width, then grow to the new one (style.css animates it)
        fill.style.width = (fillBefore.has(tank.id) ? fillBefore.get(tank.id) : percent) + '%';
        fills.push([fill, percent]);
        fillBefore.set(tank.id, percent);
        bar.append(fill);
        const food = document.createElement('div');
        food.className = tank.hungry ? 'food hungry' : 'food';
        food.textContent = tank.hungry
            ? 'Hungry! Visitors pay nothing for this tank'
            : 'Food: ' + Math.ceil(tank.foodSecondsLeft / 60) + ' min left';
        const decor = document.createElement('div');
        decor.className = 'hint';
        decor.textContent = 'Decorations: ' + tank.decorations;
        card.append(title, fish, bar, food, decor);

        // A hungry tank gets its own Feed button: the fix sits next to the problem
        if (tank.hungry) {
            const feed = document.createElement('button');
            feed.type = 'button';
            feed.className = 'secondary';
            if (view.coins < feedCost) {
                feed.textContent = 'Need ' + (feedCost - view.coins) + ' more coins to feed';
                feed.disabled = true;
            } else {
                feed.textContent = hungryTanks === 1
                    ? 'Feed (5 coins)'
                    : 'Feed all ' + hungryTanks + ' hungry (' + feedCost + ' coins)';
            }
            feed.addEventListener('click', feedHungryTanks);
            card.append(feed);
        }
        tanks.append(card);
    }
    void tanks.offsetWidth; // let the browser draw the old widths first...
    for (const [fill, percent] of fills) {
        fill.style.width = percent + '%'; // ...then grow each bar to its new width
    }
    justFilledTankId = null;

    // Fish waiting for a tank: one button per tank, saying why a tank can't take them
    const panel = el('place-panel');
    if (view.fishToPlace.length === 0) {
        hidePanel(panel);
        return;
    }
    const pending = view.fishToPlace[0];
    el('place-fish').textContent = pending.fish;
    const buttons = el('place-tanks');
    buttons.replaceChildren();
    let anyRoom = false;
    for (const tank of view.tanks) {
        const room = tank.capacity - tank.fishCount;
        const button = document.createElement('button');
        button.type = 'button';
        const name = tank.name + ' ' + tank.id;
        if (room === 0) {
            button.textContent = name + ': full';
        } else if (room < pending.fish) {
            button.textContent = name + ': only room for ' + room;
        } else {
            button.textContent = name + ' (room for ' + room + ')';
            anyRoom = true;
        }
        button.disabled = room < pending.fish;
        button.addEventListener('click', () => placeFish(pending.deliveryId, tank.id));
        buttons.append(button);
    }
    el('no-room').hidden = anyRoom;
    showPanel(panel);
}

/**
 * Runs every second: counts down "Next delivery in" and "Next visit in" between refreshes.
 * When one of them reaches zero, asks the server straight away, so a new delivery or the
 * visitors' coins show up on time instead of up to 10 seconds late.
 */
function tickClocks() {
    const now = Date.now();
    const deliveryLeft = Math.max(0, Math.ceil((nextDeliveryAt - now) / 1000));
    const visitLeft = Math.max(0, Math.ceil((nextVisitAt - now) / 1000));
    el('next-in').textContent = minSec(deliveryLeft);
    el('visit-in').textContent = visitLeft + ' s';
    const due = (nextDeliveryAt > 0 && deliveryLeft === 0) || (nextVisitAt > 0 && visitLeft === 0);
    if (due && now - lastRefresh > 2000) {
        loadGame();
    }
}

/** Asks the server for the latest game and draws it. */
async function loadGame() {
    lastRefresh = Date.now();
    const res = await api('GET', '/api/game');
    if (!res.ok) {
        showMessage(res.data.error, 'wrong');
        return;
    }
    game = res.data;
    renderGame(game);
}

/** Runs when "Open delivery" is clicked. */
async function openNextDelivery() {
	const res = await api('POST', '/api/deliveries/open');
	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	    return;
	}
	openDelivery = res.data;
	el('delivery-id').textContent = openDelivery.id;
	el('delivery-image').src = openDelivery.image;

	showPanel(el('delivery-panel'));
	const input = el('count-input');
	input.value = '';
	input.focus();

	startCountdown(openDelivery.secondsLeft);
}

/** Shows the delivery countdown; the last 5 seconds turn red. */
function startCountdown(seconds) {
	if (countdownTimer) {
	    clearInterval(countdownTimer);
	}
	showSecondsLeft(seconds);

	countdownTimer = setInterval(() => {
	    seconds--;
	    showSecondsLeft(seconds);

	    if (seconds <= 0) {
	        clearInterval(countdownTimer);
	        hidePanel(el('delivery-panel'));
	        openDelivery = null;
	        showMessage('Too late: the driver left.', 'wrong');
	        loadGame();
	    }
	}, 1000);
}

/** Writes the seconds left on the delivery, in red for the last 5. */
function showSecondsLeft(seconds) {
	el('delivery-timer').textContent = Math.max(0, seconds);
	el('timer').classList.toggle('urgent', seconds <= 5);
}

/** Runs when the count form is submitted. */
async function sendCount(event) {
	event.preventDefault();
	if (countdownTimer) {
	    clearInterval(countdownTimer);
	}

	const countVal = Number(el('count-input').value);
	const deliveryId = openDelivery.id;

	hidePanel(el('delivery-panel'));
	openDelivery = null;

	const res = await api('POST', `/api/deliveries/${deliveryId}/count`, { count: countVal });

	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	} else {
	    if (res.data.result === 'CORRECT') {
	        // No message here: the treasure panel's heading already says "Correct!"
			showTreasureCheck(res.data.fish, deliveryId);
	    } else if (res.data.result === 'WRONG') {
	        showMessage(`Not quite: there were ${res.data.fish} fish. Delivery returned.`, 'wrong');
	    } else if (res.data.result === 'TOO_LATE') {
	        showMessage('Too late: the driver left.', 'wrong');
	    }
	}

	loadGame();
}

/** Runs when a tank button in the place-the-fish panel is clicked. */
async function placeFish(deliveryId, tankId) {
	const res = await api('POST', `/api/deliveries/${deliveryId}/place`, { tankId: tankId });

	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	    return;
	}

	justFilledTankId = tankId;
	game = res.data;
	renderGame(game);
	showMessage('Fish placed in your tank.', 'correct');
}

/** Runs when a hungry tank's Feed button is clicked: feeds every hungry tank. */
async function feedHungryTanks() {
	const res = await api('POST', '/api/tanks/feed');
	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	    return;
	}
	game = res.data;
	renderGame(game);
	showMessage('Fed! Your tanks have food for 30 minutes.', 'correct');
}

// ---------- Connect events to handlers ----------

el('open-delivery').addEventListener('click', openNextDelivery);
el('count-form').addEventListener('submit', sendCount);
loadGame();

// Count the clocks down every second on the page...
setInterval(tickClocks, 1000);

// ...and ask the server for news every 10 seconds, so new deliveries and coins stay up to
// date. This is polling; the alternative is the server pushing updates (WebSockets).
// A good comparison for the video.
setInterval(loadGame, 10000);

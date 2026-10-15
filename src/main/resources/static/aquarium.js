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
 * method: 'GET' or 'POST'; url: e.g. '/api/game'; body: an object to send as JSON (optional).
 */
async function api(method, url, body) {
    const options = { method, headers: {} };
    if (body !== undefined) {
        options.headers['Content-Type'] = 'application/json';
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
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
 * STEP 10a. Runs when "Open delivery" is clicked.
 *   1. POST to /api/deliveries/open (use api()).
 *   2. If !res.ok: showMessage(res.data.error, 'wrong') and stop.
 *   3. Save res.data in openDelivery.
 *   4. Put openDelivery.id in #delivery-id and openDelivery.image in #delivery-image's src.
 *   5. Show #delivery-panel (set .hidden = false), empty #count-input and focus() it.
 *   6. startCountdown(openDelivery.secondsLeft).
 */
async function openNextDelivery() {
    // TODO step 10a
    showMessage('TODO step 10a: openNextDelivery() in aquarium.js', 'wrong');
}

/**
 * STEP 10b. Shows the 30-second countdown.
 *   1. Write seconds into #delivery-timer.
 *   2. countdownTimer = setInterval(() => { ... }, 1000) where the arrow function:
 *        - takes 1 off seconds and writes it into #delivery-timer
 *        - when seconds reaches 0: clearInterval(countdownTimer), hide #delivery-panel,
 *          openDelivery = null, showMessage('Too late: the driver left.', 'wrong'), loadGame()
 * The server keeps the real deadline; this countdown is only for the player to see.
 */
function startCountdown(seconds) {
    // TODO step 10b
}

/**
 * STEP 10c. Runs when the count form is submitted.
 *   1. event.preventDefault() so the page doesn't reload.
 *   2. clearInterval(countdownTimer) to stop the countdown.
 *   3. POST to '/api/deliveries/' + openDelivery.id + '/count' with { count: Number(#count-input value) }.
 *   4. Hide #delivery-panel and set openDelivery = null.
 *   5. If !res.ok: showMessage(res.data.error, 'wrong'). Otherwise, depending on res.data.result:
 *        'CORRECT'  -> 'Correct! ' + res.data.fish + ' fish accepted.'       (kind 'correct')
 *        'WRONG'    -> 'Not quite: there were ' + res.data.fish + ' fish. Delivery returned.' (kind 'wrong')
 *        'TOO_LATE' -> 'Too late: the driver left.'                           (kind 'wrong')
 *   6. loadGame()  (renderGame then shows the place-the-fish buttons if the count was right)
 */
async function sendCount(event) {
    // TODO step 10c
    event.preventDefault();
}

/**
 * STEP 10d. Runs when a tank button in the place-the-fish panel is clicked.
 *   1. POST to '/api/deliveries/' + deliveryId + '/place' with { tankId }.
 *   2. If !res.ok: showMessage(res.data.error, 'wrong') and stop.
 *   3. Otherwise: game = res.data, renderGame(game), showMessage('Fish placed in your tank.', 'correct').
 */
async function placeFish(deliveryId, tankId) {
    // TODO step 10d
}

// ---------- Given: connect events to handlers ----------

el('open-delivery').addEventListener('click', openNextDelivery);
el('count-form').addEventListener('submit', sendCount);
loadGame();

// Ask the server for news every 10 seconds, so new deliveries and the "next delivery"
// time stay up to date. This is polling; the alternative is the server pushing updates
// (WebSockets). A good comparison for the video.
setInterval(loadGame, 10000);

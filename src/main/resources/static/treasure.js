// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
//
// The treasure-check panel (wireframe 04). It appears after a right fish count and
// before the place-the-fish panel. The player gets ONE try at counting the chests in the
// same picture; each chest becomes a decoration in the tank the fish go into. While this
// panel is open, ui.css hides the place-the-fish panel, so the two never show together.

let treasureDeliveryId = null; // which delivery the open treasure check is for

/** Opens the treasure check for a delivery of `fish` fish. Called from sendCount() in aquarium.js. */
function showTreasureCheck(fish, deliveryId) {
    treasureDeliveryId = deliveryId;
    document.getElementById('treasure-fish').textContent = fish;
    // Show the same picture the player just counted
    document.getElementById('treasure-image').src = document.getElementById('delivery-image').src;
    document.getElementById('chest-input').value = '';
    showPanel(document.getElementById('treasure-panel')); // aquarium.js
    document.getElementById('chest-input').focus();
}

/** Closes the panel and shows the aquarium again, which brings up the place-the-fish panel. */
function closeTreasureCheck() {
    treasureDeliveryId = null;
    hidePanel(document.getElementById('treasure-panel')); // aquarium.js
    loadGame(); // aquarium.js
}

/** Runs when the chest count is submitted (the Check button or Enter). */
async function checkTreasure(event) {
	event.preventDefault();

	const chestsVal = Number(document.getElementById('chest-input').value);
	const deliveryId = treasureDeliveryId;

	const res = await api('POST', `/api/deliveries/${deliveryId}/treasure`, { chests: chestsVal });

	if (!res.ok) {
	    showMessage(res.data.error, 'wrong');
	} else {
	    if (res.data.result === 'CORRECT') {
	        showMessage(`Treasure! ${res.data.decorations} decoration(s) will go in with the fish.`, 'correct');
	    } else if (res.data.result === 'WRONG') {
	        showMessage(`Not quite: there were ${res.data.chests} chests. No decorations this time.`, 'wrong');
	    }
	}

	closeTreasureCheck();
}

// ---------- Connect events to handlers ----------

document.getElementById('treasure-form').addEventListener('submit', checkTreasure);
document.getElementById('treasure-skip').addEventListener('click', closeTreasureCheck);

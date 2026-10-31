// Reef Keeper - CIS045-3 coursework.

/** Opens the treasure check for a delivery of `fish` fish. Called from sendCount() in aquarium.js. */
function showTreasureCheck(fish) {
    document.getElementById('treasure-fish').textContent = fish;
    // Show the same picture the player just counted
    document.getElementById('treasure-image').src = document.getElementById('delivery-image').src;
    document.getElementById('treasure-panel').hidden = false;
    document.getElementById('treasure-skip').focus();
}

/** Closes the panel and shows the aquarium again, which brings up the place-the-fish panel. */
function closeTreasureCheck() {
    document.getElementById('treasure-panel').hidden = true;
    loadGame(); // from aquarium.js
}

document.getElementById('treasure-skip').addEventListener('click', closeTreasureCheck);

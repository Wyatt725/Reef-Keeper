// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 31 Oct 2026; revised by Claude (Anthropic AI),
// 8 Nov 2026 (design review fixes: Log out as a quiet text button, short labels on phones,
// and updateTopBar() so a changed coin count gets a small "bump"). Complete as given.
//
// The top bar shared by every page after login: links, coins, value, player, Log out.
// Each page has an empty <header id="topbar" data-page="..."> that this file fills in.

/**
 * Sets the coins and value in the top bar. A number that changed gets a short bump
 * animation (only when the device allows motion; see style.css), so the player sees
 * the effect of what they just did.
 */
function updateTopBar(coins, value) {
    setStat('coins', coins);
    setStat('value', value);
}

function setStat(id, number) {
    const stat = document.getElementById(id);
    if (!stat) {
        return;
    }
    const text = String(number);
    const changed = stat.textContent !== '-' && stat.textContent !== text;
    stat.textContent = text;
    if (changed) {
        stat.classList.remove('bump');
        void stat.offsetWidth; // restart the animation if it is already running
        stat.classList.add('bump');
    }
}

(function buildTopBar() {
    const bar = document.getElementById('topbar');
    if (!bar) {
        return;
    }

    bar.innerHTML = `
        <h1><a href="/">Reef Keeper</a></h1>
        <nav class="nav" aria-label="Main">
            <a href="/" data-page="aquarium">Aquarium</a>
            <a href="shop.html" data-page="shop">Shop</a>
            <a href="leaderboard.html" data-page="leaderboard">Leaderboard</a>
        </nav>
        <div class="stats">
            <span><span class="label">Coins: </span><strong id="coins">-</strong><span class="unit"> coins</span></span>
            <span><span class="label">Value: </span><strong id="value">-</strong><span class="unit"> value</span></span>
        </div>
        <div class="player">
            <span><span class="label">Keeper: </span><strong id="player-name">-</strong></span>
            <form method="post" action="/logout">
                <input type="hidden" name="_csrf">
                <button type="submit" class="text-button">Log out</button>
            </form>
        </div>`;

    // Mark the link for the page we're on
    const current = bar.dataset.page;
    for (const link of bar.querySelectorAll('.nav a')) {
        if (link.dataset.page === current) {
            link.classList.add('active');
            link.setAttribute('aria-current', 'page');
        }
    }

    // Logging out is a POST that needs the CSRF token (csrfToken() comes from account.js)
    bar.querySelector('input[name="_csrf"]').value = csrfToken();

    // Who is logged in
    fetch('/api/me')
        .then((response) => {
            if (response.status === 401) {
                window.location.href = '/login.html';
                return null;
            }
            return response.ok ? response.json() : null;
        })
        .then((data) => {
            if (data) {
                document.getElementById('player-name').textContent = data.username;
            }
        });

    // Coins and value. On the aquarium and shop pages, those pages keep them up to date too.
    fetch('/api/game')
        .then((response) => (response.ok ? response.json() : null))
        .then((game) => {
            if (game) {
                updateTopBar(game.coins, game.value);
            }
        });
})();

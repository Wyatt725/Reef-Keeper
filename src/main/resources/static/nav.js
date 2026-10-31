// Reef Keeper - CIS045-3 coursework.

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
            <span>Coins: <strong id="coins">-</strong></span>
            <span>Value: <strong id="value">-</strong></span>
        </div>
        <div class="player">
            <span>Keeper: <strong id="player-name">-</strong></span>
            <form method="post" action="/logout">
                <input type="hidden" name="_csrf">
                <button type="submit">Log out</button>
            </form>
        </div>`;

    // Highlight the link for the page we're on
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

    // Coins and value. On the aquarium page renderGame() keeps these up to date too.
    fetch('/api/game')
        .then((response) => (response.ok ? response.json() : null))
        .then((game) => {
            if (game) {
                document.getElementById('coins').textContent = game.coins;
                document.getElementById('value').textContent = game.value;
            }
        });
})();

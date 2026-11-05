// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 31 Oct 2026; every player's ranking added 3 Nov 2026.
// Functions marked TODO are written by me.
//
// Leaderboard page (wireframe 07). GET /api/leaderboard gives a LeaderboardView:
//   { top: [ {rank, username, fish, decorations, value}, ... up to 10 ],
//     me:  {rank, username, fish, decorations, value} }

/** Makes one table row for a leaderboard entry. isMe highlights it. Given. */
function makeRow(entry, isMe) {
    const row = document.createElement('tr');
    if (isMe) {
        row.className = 'me';
    }
    const name = isMe ? entry.username + ' (you)' : entry.username;
    for (const value of [entry.rank, name, entry.fish, entry.decorations, entry.value]) {
        const cell = document.createElement('td');
        cell.textContent = value;
        row.append(cell);
    }
    return row;
}

/** A thin row with "..." in it, for the gap between the top 10 and your own row. Given. */
function makeGapRow() {
    const row = document.createElement('tr');
    row.className = 'gap';
    const cell = document.createElement('td');
    cell.colSpan = 5;
    cell.textContent = '...';
    row.append(cell);
    return row;
}

/**
 * STEP B2. Fills the table from a LeaderboardView.
 *   1. const rows = document.getElementById('board-rows'); then rows.replaceChildren() to empty it.
 *   2. For each entry in board.top: rows.append(makeRow(entry, entry.username === board.me.username))
 *   3. If you are NOT in the top list (no entry in board.top has your username), add
 *      makeGapRow() and then makeRow(board.me, true) at the end, so you always see your own rank.
 * Hint for 3: board.top.some(entry => entry.username === board.me.username) is true if you are in it.
 */
function renderBoard(board) {
    // TODO step B2
}

/** Loads the leaderboard and draws it. Given. */
async function loadLeaderboard() {
    const res = await api('GET', '/api/leaderboard');
    if (!res.ok) {
        const message = document.getElementById('board-message');
        message.textContent = res.data.error;
        message.className = 'message wrong';
        return;
    }
    renderBoard(res.data);
}

loadLeaderboard();

// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI

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
 * Fills the table from a LeaderboardView.
 */
function renderBoard(board) {
	const rows = document.getElementById('board-rows');
	rows.replaceChildren();

	for (const entry of board.top) {
	    const isMe = entry.username === board.me.username;
	    rows.append(makeRow(entry, isMe));
	}

	const inTop = board.top.some(entry => entry.username === board.me.username);
	if (!inTop) {
	    rows.append(makeGapRow());
	    rows.append(makeRow(board.me, true));
	}
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

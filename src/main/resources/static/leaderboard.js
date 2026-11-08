// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 31 Oct 2026; every player's ranking
// added 3 Nov 2026. renderBoard written by me (Week 9, step B2). Revised by Claude
// (Anthropic AI), 8 Nov 2026, to fix the design review: number cells line up on the right,
// the Decorations cell can hide on phones, and errors show as a toast.
//
// Leaderboard page (wireframe 07). GET /api/leaderboard gives a LeaderboardView:
//   { top: [ {rank, username, fish, decorations, value}, ... up to 10 ],
//     me:  {rank, username, fish, decorations, value} }

/** Makes one table cell. className styles it (see ui.css): 'num' lines numbers up. */
function makeCell(value, className) {
    const cell = document.createElement('td');
    cell.textContent = value;
    if (className) {
        cell.className = className;
    }
    return cell;
}

/** Makes one table row for a leaderboard entry. isMe highlights it. */
function makeRow(entry, isMe) {
    const row = document.createElement('tr');
    if (isMe) {
        row.className = 'me';
    }
    row.append(
        makeCell(entry.rank, 'num'),
        makeCell(isMe ? entry.username + ' (you)' : entry.username),
        makeCell(entry.fish, 'num'),
        makeCell(entry.decorations, 'num col-decor'),
        makeCell(entry.value, 'num'));
    return row;
}

/** A thin row with "..." in it, for the gap between the top 10 and your own row. */
function makeGapRow() {
    const row = document.createElement('tr');
    row.className = 'gap';
    const cell = document.createElement('td');
    cell.colSpan = 5;
    cell.textContent = '...';
    row.append(cell);
    return row;
}

/** Fills the table from a LeaderboardView. */
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

/** Loads the leaderboard and draws it. */
async function loadLeaderboard() {
    const res = await api('GET', '/api/leaderboard');
    if (!res.ok) {
        showToast(res.data.error, 'wrong');
        return;
    }
    renderBoard(res.data);
}

loadLeaderboard();

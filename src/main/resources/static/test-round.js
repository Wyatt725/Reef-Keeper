// Reef Keeper - CIS045-3 coursework.
// Source: starter skeleton generated with Claude (Anthropic AI), 8 Oct 2026.
//
// Event-driven: nothing here runs on its own. Each function runs because an
// event happened: the page loaded, the form was submitted, a button was clicked,
// or a reply arrived from the server (the Promise returned by fetch).

const image = document.getElementById('puzzle-image');
const loading = document.getElementById('loading');
const form = document.getElementById('guess-form');
const input = document.getElementById('guess');
const result = document.getElementById('result');
const nextButton = document.getElementById('next');

async function loadPuzzle() {
    image.hidden = true;
    loading.hidden = false;
    loading.textContent = 'Loading a puzzle...';
    result.textContent = '';
    result.className = '';
    input.value = '';

    const response = await fetch('/api/test-round');
    const data = await response.json();
    if (!response.ok) {
        loading.textContent = 'Could not get a puzzle: ' + data.error;
        return;
    }
    image.src = data.image; // a data: URI, so the answer is not in any URL
    image.hidden = false;
    loading.hidden = true;
    input.focus();
}

async function sendGuess(event) {
    event.preventDefault(); // stop the browser reloading the page on submit
    const response = await fetch('/api/test-round/guess', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ guess: Number(input.value) })
    });
    const data = await response.json();
    if (!response.ok) {
        result.textContent = data.error;
        return;
    }
    result.textContent = data.correct
        ? 'Correct! There were ' + data.fish + ' fish.'
        : 'Not quite: there were ' + data.fish + ' fish.';
    result.className = data.correct ? 'correct' : 'wrong';
}

// Register the event handlers
form.addEventListener('submit', sendGuess);
nextButton.addEventListener('click', loadPuzzle);
document.addEventListener('DOMContentLoaded', loadPuzzle);

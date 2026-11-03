// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 15 Oct 2026; CSRF header and 401 redirect
// added 31 Oct 2026; moved into its own file 3 Nov 2026. Complete as given.
//
// The one function every page uses to talk to the Java server. It used to live in
// aquarium.js; the shop page needs it too, so it now has its own file instead of being
// copied (one place to change it). Load it after account.js, which gives csrfToken().

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
    // Every request that changes something must carry the CSRF token
    if (method !== 'GET') {
        options.headers['X-XSRF-TOKEN'] = csrfToken();
    }
    const response = await fetch(url, options);
    // Not logged in, or the session ended: back to the login page
    if (response.status === 401) {
        window.location.href = '/login.html';
        return { ok: false, status: 401, data: { error: 'Please log in' } };
    }
    let data = null;
    try {
        data = await response.json();
    } catch (e) {
        data = { error: 'The server sent no JSON (HTTP ' + response.status + ')' };
    }
    return { ok: response.ok, status: response.status, data };
}

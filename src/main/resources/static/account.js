// Reef Keeper - CIS045-3 coursework.
// Used on the login, register and aquarium pages.

/**
 * The CSRF token. The server puts it in a cookie called XSRF-TOKEN; only pages from our own
 * site can read that cookie, so sending its value back proves the request came from our page.
 */
function csrfToken() {
    const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]*)/);
    return match ? decodeURIComponent(match[1]) : '';
}

// Put the token into every hidden _csrf field (the login form and the logout button).
for (const field of document.querySelectorAll('input[name="_csrf"]')) {
    field.value = csrfToken();
}

// Messages on the login page, from the address: /login.html?error, ?logout or ?registered
const authMessage = document.getElementById('auth-message');
if (authMessage) {
    const params = new URLSearchParams(window.location.search);
    if (params.has('error')) {
        authMessage.textContent = 'Wrong username or password.';
        authMessage.className = 'message wrong';
    } else if (params.has('logout')) {
        authMessage.textContent = 'You have logged out.';
        authMessage.className = 'message';
    } else if (params.has('registered')) {
        authMessage.textContent = 'Account created. You can log in now.';
        authMessage.className = 'message correct';
    }
}

// The create-account form: send it as JSON, then go to the login page.
const registerForm = document.getElementById('register-form');
if (registerForm) {
    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const response = await fetch('/api/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'X-XSRF-TOKEN': csrfToken() },
            body: JSON.stringify({
                username: document.getElementById('username').value,
                password: document.getElementById('password').value,
                confirmPassword: document.getElementById('confirm-password').value
            })
        });
        if (response.status === 201) {
            window.location.href = '/login.html?registered';
            return;
        }
        let error = 'Could not create the account (HTTP ' + response.status + ')';
        try {
            error = (await response.json()).error || error;
        } catch (e) {
            // no JSON body: keep the default message
        }
        authMessage.textContent = error;
        authMessage.className = 'message wrong';
    });
}

// On the aquarium page: show who is logged in.
const playerName = document.getElementById('player-name');
if (playerName) {
    fetch('/api/me')
        .then((response) => (response.ok ? response.json() : null))
        .then((data) => {
            if (data) {
                playerName.textContent = data.username;
            }
        });
}

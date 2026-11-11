// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
//
// One small message ("toast") at the bottom of the screen. It is always in view, even on
// a phone scrolled to the bottom, and it disappears by itself after a few seconds.
// Screen readers announce it too (role="status").

let toastTimer = null;  // hides the toast after a few seconds
let leaveTimer = null;  // finishes the fade-out

/** Shows text at the bottom of the screen. kind: 'correct', 'wrong' or '' (plain). */
function showToast(text, kind) {
    let toast = document.getElementById('toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toast';
        toast.setAttribute('role', 'status');
        toast.setAttribute('aria-live', 'polite');
        document.body.append(toast);
    }
    clearTimeout(toastTimer);
    clearTimeout(leaveTimer);
    toast.className = 'toast ' + (kind || '');
    toast.textContent = text;
    toast.hidden = false;
    // Errors stay a little longer, so there is time to read why
    toastTimer = setTimeout(hideToast, kind === 'wrong' ? 6000 : 4000);
}

/** Fades the toast out (or hides it at once when the device asks for less motion). */
function hideToast() {
    const toast = document.getElementById('toast');
    if (!toast || toast.hidden) {
        return;
    }
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        toast.hidden = true;
        return;
    }
    toast.classList.add('leaving');
    leaveTimer = setTimeout(() => {
        toast.hidden = true;
        toast.classList.remove('leaving');
    }, 200);
}

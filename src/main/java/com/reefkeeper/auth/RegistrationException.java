// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 29 Oct 2026. Complete as given.
package com.reefkeeper.auth;

/** Thrown when a new account can't be created. The message is shown to the player as it is. */
public class RegistrationException extends RuntimeException {

    public RegistrationException(String message) {
        super(message);
    }
}

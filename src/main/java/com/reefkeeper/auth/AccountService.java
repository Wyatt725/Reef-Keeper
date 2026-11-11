// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;

/** Creating accounts: checks the details, hashes the password, saves the account. */
@Service
public class AccountService {

    /** 3 to 20 letters, digits or underscores. */
    public static final String USERNAME_PATTERN = "[A-Za-z0-9_]{3,20}";
    public static final int MIN_PASSWORD_LENGTH = 8;

    private final AccountStore store;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public AccountService(AccountStore store, PasswordEncoder passwordEncoder, Clock clock) {
        this.store = store;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    /**
     *Creates a new account and returns it.
     */
    
    public PlayerAccount register(String username, String password, String confirmPassword) {
    	String trimmedUsername = username == null ? "" : username.trim();

        if (!trimmedUsername.matches(USERNAME_PATTERN)) {
            throw new RegistrationException("Usernames are 3-20 letters, digits or _");
        }

        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new RegistrationException("Passwords need at least 8 characters");
        }

        if (!password.equals(confirmPassword)) {
            throw new RegistrationException("The two passwords don't match");
        }

        if (store.usernameTaken(trimmedUsername)) {
            throw new RegistrationException("That username is taken");
        }

        String hash = passwordEncoder.encode(password);
        PlayerAccount account = new PlayerAccount(trimmedUsername, hash, clock.instant());
        store.save(account);
        
        return account;
    }
}

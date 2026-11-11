// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * How Spring Security finds a player when someone logs in.
 *
 * At login, Spring Security calls loadUserByUsername with the typed username, gets back
 * the stored bcrypt hash, and checks the typed password against it with the PasswordEncoder.
 * This class never sees or compares the password itself.
 */
@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountStore store;

    public AccountUserDetailsService(AccountStore store) {
        this.store = store;
    }

    /** Look the player up and describe them to Spring Security. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	return store.findByUsername(username).map(account -> User.withUsername(account.getUsername()).password(account.getPasswordHash()).roles("PLAYER").build()).orElseThrow(() -> new UsernameNotFoundException("No player called " + username));
    }
}

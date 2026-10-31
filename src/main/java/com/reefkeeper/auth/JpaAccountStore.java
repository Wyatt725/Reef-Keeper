// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.auth;

import org.springframework.stereotype.Component;

import java.util.Optional;

/** The real AccountStore: passes each call on to the Spring Data repository (an adapter). */
@Component
public class JpaAccountStore implements AccountStore {

    private final PlayerAccountRepository repository;

    public JpaAccountStore(PlayerAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PlayerAccount> findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public boolean usernameTaken(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public PlayerAccount save(PlayerAccount account) {
        return repository.save(account);
    }
}

// Reef Keeper - CIS045-3 coursework.
// Source: generated with Claude (Anthropic AI), 29 Oct 2026. Test helper.
package com.reefkeeper.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** A fake AccountStore for tests: a plain list instead of the database. */
class InMemoryAccountStore implements AccountStore {

    final List<PlayerAccount> saved = new ArrayList<>();

    @Override
    public Optional<PlayerAccount> findByUsername(String username) {
        return saved.stream().filter(a -> a.getUsername().equals(username)).findFirst();
    }

    @Override
    public boolean usernameTaken(String username) {
        return saved.stream().anyMatch(a -> a.getUsername().equalsIgnoreCase(username));
    }

    @Override
    public PlayerAccount save(PlayerAccount account) {
        saved.add(account);
        return account;
    }
}

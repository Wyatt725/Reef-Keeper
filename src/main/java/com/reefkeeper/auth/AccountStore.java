// Reef Keeper - CIS045-3 coursework.
package com.reefkeeper.auth;

import java.util.Optional;

/**
 * Where accounts are kept. AccountService only knows this small interface:
 * the real app uses JpaAccountStore (the H2 database), the tests use an in-memory list.
 * Same idea as PuzzleSource and GameEventPublisher: depend on what you need, not on a library.
 */
public interface AccountStore {

    Optional<PlayerAccount> findByUsername(String username);

    /** True if this name is already used, ignoring upper/lower case ("Wyatt" = "wyatt"). */
    boolean usernameTaken(String username);

    PlayerAccount save(PlayerAccount account);
}

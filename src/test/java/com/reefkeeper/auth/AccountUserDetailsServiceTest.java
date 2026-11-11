// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountUserDetailsServiceTest {

    @Test
    void knownPlayerIsDescribedWithTheirHash() {
        InMemoryAccountStore store = new InMemoryAccountStore();
        store.save(new PlayerAccount("wyatt", "$2a$04$exampleHashForTests", Instant.now()));

        UserDetails user = new AccountUserDetailsService(store).loadUserByUsername("wyatt");

        assertEquals("wyatt", user.getUsername());
        assertEquals("$2a$04$exampleHashForTests", user.getPassword());
        assertTrue(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PLAYER")));
    }

    @Test
    void unknownPlayerIsRefused() {
        AccountUserDetailsService service = new AccountUserDetailsService(new InMemoryAccountStore());
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nobody"));
    }
}

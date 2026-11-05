// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Uses the real bcrypt encoder (strength 4 to keep the tests fast) and a fake store. */
class AccountServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-29T10:00:00Z");

    private InMemoryAccountStore store;
    private BCryptPasswordEncoder encoder;
    private AccountService service;

    @BeforeEach
    void setUp() {
        store = new InMemoryAccountStore();
        encoder = new BCryptPasswordEncoder(4);
        service = new AccountService(store, encoder, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void registerSavesTheAccount() {
        PlayerAccount account = service.register("wyatt", "coral-reef-1", "coral-reef-1");
        assertEquals("wyatt", account.getUsername());
        assertEquals(NOW, account.getCreatedAt());
        assertEquals(1, store.saved.size());
    }

    @Test
    void thePasswordIsStoredOnlyAsABcryptHash() {
        PlayerAccount account = service.register("wyatt", "coral-reef-1", "coral-reef-1");
        assertNotEquals("coral-reef-1", account.getPasswordHash());
        assertTrue(account.getPasswordHash().startsWith("$2"), "bcrypt hashes start with $2");
        assertTrue(encoder.matches("coral-reef-1", account.getPasswordHash()));
    }

    @Test
    void usernameIsTrimmed() {
        PlayerAccount account = service.register("  wyatt ", "coral-reef-1", "coral-reef-1");
        assertEquals("wyatt", account.getUsername());
    }

    @Test
    void takenUsernameIsRefusedIgnoringCase() {
        service.register("wyatt", "coral-reef-1", "coral-reef-1");
        RegistrationException e = assertThrows(RegistrationException.class,
                () -> service.register("Wyatt", "another-pass", "another-pass"));
        assertEquals("That username is taken", e.getMessage());
        assertEquals(1, store.saved.size());
    }

    @Test
    void shortPasswordIsRefused() {
        assertThrows(RegistrationException.class, () -> service.register("wyatt", "short", "short"));
        assertEquals(0, store.saved.size());
    }

    @Test
    void passwordsMustMatch() {
        RegistrationException e = assertThrows(RegistrationException.class,
                () -> service.register("wyatt", "coral-reef-1", "coral-reef-2"));
        assertEquals("The two passwords don't match", e.getMessage());
    }

    @Test
    void badUsernamesAreRefused() {
        assertThrows(RegistrationException.class, () -> service.register("ab", "coral-reef-1", "coral-reef-1"));
        assertThrows(RegistrationException.class, () -> service.register("wy att", "coral-reef-1", "coral-reef-1"));
        assertThrows(RegistrationException.class, () -> service.register(null, "coral-reef-1", "coral-reef-1"));
        assertEquals(0, store.saved.size());
    }
}

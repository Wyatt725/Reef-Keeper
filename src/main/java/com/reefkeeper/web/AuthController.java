// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 29 Oct 2026.
// Method bodies marked TODO are written by me.
package com.reefkeeper.web;

import com.reefkeeper.auth.AccountService;
import com.reefkeeper.auth.PlayerAccount;
import com.reefkeeper.auth.RegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * Account endpoints. Logging in and out is NOT here: Spring Security handles
 * POST /login and POST /logout itself (see SecurityConfig).
 *
 *   POST /api/register  body {"username": "...", "password": "...", "confirmPassword": "..."}
 *   GET  /api/me        -> {"username": "..."}  (who is logged in)
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AccountService accounts;

    public AuthController(AccountService accounts) {
        this.accounts = accounts;
    }

    /** JSON body of POST /api/register. */
    public record RegisterRequest(String username, String password, String confirmPassword) {
    }

    /**
     * STEP 4. Create an account.
     * Call accounts.register(...) with the three values from the request, then return
     * ResponseEntity.status(HttpStatus.CREATED).body(Map.of("username", account.getUsername())).
     * (201 Created is the HTTP status for "a new thing was made".)
     * A RegistrationException is turned into a 400 by the handler below, so don't catch it here.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        // TODO step 4
        throw new UnsupportedOperationException("TODO step 4: AuthController.register");
    }

    /** Who is logged in. Spring Security fills in the Principal from the session. Given. */
    @GetMapping("/me")
    public Map<String, String> me(Principal principal) {
        return Map.of("username", principal.getName());
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Map<String, String>> registrationFailed(RegistrationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
}
// Note: Eclipse marks the PlayerAccount import as "never used" (yellow) until you write step 4. That is fine.

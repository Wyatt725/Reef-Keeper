// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
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

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AccountService accounts;

    public AuthController(AccountService accounts) {
        this.accounts = accounts;
    }

    public record RegisterRequest(String username, String password, String confirmPassword) {
    }

    /**
     * Create an account.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
    	PlayerAccount account = accounts.register(request.username(), request.password(), request.confirmPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("username", account.getUsername()));
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

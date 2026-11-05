// Reef Keeper - CIS045-3 coursework.
// Assited by claude AI
package com.reefkeeper.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data writes the SQL for this interface by itself, from the method names:
 * findByUsername becomes "SELECT ... WHERE username = ?".
 */
public interface PlayerAccountRepository extends JpaRepository<PlayerAccount, Long> {

    Optional<PlayerAccount> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
}

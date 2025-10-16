package com.taskmanager.repository;

import com.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username to check
     * @return true if a user exists with the username
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email to check
     * @return true if a user exists with the email
     */
    boolean existsByEmail(String email);
}
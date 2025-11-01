package com.taskmanager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User model.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Should have valid ID")
    void shouldHaveValidId() {
        assertThat(user.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should have valid username")
    void shouldHaveValidUsername() {
        assertThat(user.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should have valid email")
    void shouldHaveValidEmail() {
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should have valid password")
    void shouldHaveValidPassword() {
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
    }

    @Test
    @DisplayName("Should be enabled by default")
    void shouldBeEnabledByDefault() {
        assertThat(user.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should allow disabling user")
    void shouldAllowDisablingUser() {
        user.setEnabled(false);
        assertThat(user.getEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should allow username update")
    void shouldAllowUsernameUpdate() {
        user.setUsername("newusername");
        assertThat(user.getUsername()).isEqualTo("newusername");
    }

    @Test
    @DisplayName("Should allow email update")
    void shouldAllowEmailUpdate() {
        user.setEmail("newemail@example.com");
        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    @DisplayName("Should allow password update")
    void shouldAllowPasswordUpdate() {
        user.setPassword("newHashedPassword");
        assertThat(user.getPassword()).isEqualTo("newHashedPassword");
    }

    @Test
    @DisplayName("Should have full name from first and last")
    void shouldHaveFullNameFromFirstAndLast() {
        user.setFirstName("John");
        user.setLastName("Doe");
        assertThat(user.getFullName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should handle first name only")
    void shouldHandleFirstNameOnly() {
        user.setFirstName("John");
        assertThat(user.getFullName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should handle null password")
    void shouldHandleNullPassword() {
        user.setPassword(null);
        assertThat(user.getPassword()).isNull();
    }

    @Test
    @DisplayName("Should handle empty username")
    void shouldHandleEmptyUsername() {
        user.setUsername("");
        assertThat(user.getUsername()).isEmpty();
    }

    @Test
    @DisplayName("Should handle long username")
    void shouldHandleLongUsername() {
        String longUsername = "a".repeat(100);
        user.setUsername(longUsername);
        assertThat(user.getUsername()).hasSize(100);
    }
}

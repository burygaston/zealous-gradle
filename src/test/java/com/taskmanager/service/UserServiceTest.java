package com.taskmanager.service;

import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void shouldLoadUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: unknown");
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findByEmail("test@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> users = userService.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUsername).contains("testuser", "user2");
    }

    @Test
    @DisplayName("Should create user with encoded password")
    void shouldCreateUserWithEncodedPassword() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("plainPassword");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User created = userService.createUser(newUser);

        assertThat(created).isNotNull();
        assertThat(created.getPassword()).isEqualTo("encodedPassword");
        assertThat(created.getEnabled()).isTrue();
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(newUser);
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate username")
    void shouldThrowExceptionForDuplicateUsername() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("Should throw exception when creating user with duplicate email")
    void shouldThrowExceptionForDuplicateEmail() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(newUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User updated = userService.updateUser(testUser);

        assertThat(updated).isNotNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
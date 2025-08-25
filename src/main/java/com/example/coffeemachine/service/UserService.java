package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import com.example.coffeemachine.repository.FacilityRepository;
import com.example.coffeemachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing users and authentication.
 * Handles user creation, updates, and role management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user with encrypted password.
     *
     * @param user the user to create
     * @param plainPassword the plain text password
     * @return the created user
     * @throws IllegalArgumentException if username already exists or validation fails
     */
    public User createUser(User user, String plainPassword) {
        log.info("Creating new user: {} with role {}", user.getUsername(), user.getRole());
        
        // Validate username uniqueness
        if (userRepository.findActiveByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }
        
        // Validate facility assignment for FACILITY users
        if (user.getRole() == UserRole.FACILITY) {
            if (user.getFacility() == null || user.getFacility().getId() == null) {
                throw new IllegalArgumentException("FACILITY users must be assigned to a facility");
            }
            
            // Verify facility exists
            Facility facility = facilityRepository.findActiveById(user.getFacility().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + user.getFacility().getId()));
            
            user.setFacility(facility);
        } else if (user.getRole() == UserRole.ADMIN) {
            // ADMIN users should not have facility assignment
            user.setFacility(null);
        }
        
        // Encrypt password
        user.setPasswordHash(passwordEncoder.encode(plainPassword));
        
        return userRepository.save(user);
    }

    /**
     * Finds a user by username (active only).
     *
     * @param username the username
     * @return the user or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findActiveByUsername(username);
    }

    /**
     * Finds a user by ID (active only).
     *
     * @param userId the user ID
     * @return the user or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findActiveById(userId);
    }

    /**
     * Finds all active users.
     *
     * @return list of all active users
     */
    @Transactional(readOnly = true)
    public List<User> findAllActive() {
        return userRepository.findAllActive();
    }

    /**
     * Finds all users with FACILITY role.
     *
     * @return list of facility users
     */
    @Transactional(readOnly = true)
    public List<User> findFacilityUsers() {
        return userRepository.findActiveByRole(UserRole.FACILITY);
    }

    /**
     * Finds all users with ADMIN role.
     *
     * @return list of admin users
     */
    @Transactional(readOnly = true)
    public List<User> findAdminUsers() {
        return userRepository.findActiveByRole(UserRole.ADMIN);
    }

    /**
     * Finds all users assigned to a specific facility.
     *
     * @param facilityId the facility ID
     * @return list of users in the facility
     */
    @Transactional(readOnly = true)
    public List<User> findByFacilityId(Long facilityId) {
        return userRepository.findActiveFacilityUsersByFacilityId(facilityId);
    }

    /**
     * Updates user information (excluding password).
     *
     * @param userId the user ID
     * @param updates the user with updated fields
     * @return updated user or empty if not found
     */
    public Optional<User> updateUser(Long userId, User updates) {
        log.info("Updating user {}", userId);
        
        return userRepository.findActiveById(userId)
                .map(existing -> {
                    // Update username if provided and unique
                    if (updates.getUsername() != null && !updates.getUsername().trim().isEmpty()) {
                        String newUsername = updates.getUsername().trim();
                        if (!newUsername.equals(existing.getUsername())) {
                            // Check username uniqueness
                            Optional<User> usernameConflict = userRepository.findActiveByUsername(newUsername);
                            if (usernameConflict.isPresent() && !usernameConflict.get().getId().equals(userId)) {
                                throw new IllegalArgumentException("Username '" + newUsername + "' already exists");
                            }
                            existing.setUsername(newUsername);
                        }
                    }
                    
                    // Update role and facility assignment
                    if (updates.getRole() != null) {
                        existing.setRole(updates.getRole());
                        
                        // Handle facility assignment based on role
                        if (updates.getRole() == UserRole.FACILITY) {
                            if (updates.getFacility() != null && updates.getFacility().getId() != null) {
                                Facility facility = facilityRepository.findActiveById(updates.getFacility().getId())
                                        .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + updates.getFacility().getId()));
                                existing.setFacility(facility);
                            } else if (existing.getFacility() == null) {
                                throw new IllegalArgumentException("FACILITY users must be assigned to a facility");
                            }
                        } else if (updates.getRole() == UserRole.ADMIN) {
                            existing.setFacility(null);
                        }
                    }
                    
                    return userRepository.save(existing);
                });
    }

    /**
     * Updates user password.
     *
     * @param userId the user ID
     * @param newPassword the new plain text password
     * @return true if password was updated, false if user not found
     */
    public boolean updatePassword(Long userId, String newPassword) {
        log.info("Updating password for user {}", userId);
        
        return userRepository.findActiveById(userId)
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Assigns a user to a facility (for FACILITY role users only).
     *
     * @param userId the user ID
     * @param facilityId the facility ID
     * @return updated user or empty if not found
     * @throws IllegalArgumentException if user is not FACILITY role or facility not found
     */
    public Optional<User> assignUserToFacility(Long userId, Long facilityId) {
        log.info("Assigning user {} to facility {}", userId, facilityId);
        
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        if (user.getRole() != UserRole.FACILITY) {
            throw new IllegalArgumentException("Only FACILITY users can be assigned to facilities");
        }
        
        Facility facility = facilityRepository.findActiveById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + facilityId));
        
        user.setFacility(facility);
        return Optional.of(userRepository.save(user));
    }

    /**
     * Removes a user from their facility assignment.
     *
     * @param userId the user ID
     * @return updated user or empty if not found
     */
    public Optional<User> removeUserFromFacility(Long userId) {
        log.info("Removing user {} from facility assignment", userId);
        
        return userRepository.findActiveById(userId)
                .map(user -> {
                    user.setFacility(null);
                    return userRepository.save(user);
                });
    }

    /**
     * Soft deletes a user.
     *
     * @param userId the user ID
     * @return true if deleted, false if not found
     */
    public boolean deleteUser(Long userId) {
        log.info("Soft deleting user {}", userId);
        return userRepository.softDeleteById(userId) > 0;
    }

    /**
     * Checks if a username is available.
     *
     * @param username the username to check
     * @return true if username is available
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return userRepository.findActiveByUsername(username).isEmpty();
    }

    /**
     * Validates user credentials.
     *
     * @param username the username
     * @param password the plain text password
     * @return the user if credentials are valid, empty otherwise
     */
    @Transactional(readOnly = true)
    public Optional<User> validateCredentials(String username, String password) {
        return userRepository.findActiveByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    /**
     * Checks if user exists and is active.
     *
     * @param userId the user ID
     * @return true if user exists and is active
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long userId) {
        return userRepository.findActiveById(userId).isPresent();
    }
}
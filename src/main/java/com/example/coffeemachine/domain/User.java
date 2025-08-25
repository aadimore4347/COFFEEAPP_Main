package com.example.coffeemachine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a user in the coffee machine monitoring system.
 * 
 * Users can be either FACILITY users (limited to their assigned facility)
 * or ADMIN users (full system access).
 */
@Entity
@Table(name = "user", 
       uniqueConstraints = @UniqueConstraint(columnNames = "username"),
       indexes = {
           @Index(name = "idx_user_username", columnList = "username"),
           @Index(name = "idx_user_facility", columnList = "facility_id"),
           @Index(name = "idx_user_role", columnList = "role")
       })
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"passwordHash"})
public class User extends BaseEntity {

    /**
     * Unique username for authentication.
     * Must be unique across all users.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * BCrypt hashed password for authentication.
     * Never store plain text passwords.
     */
    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    /**
     * User role determining access permissions.
     */
    @NotNull(message = "User role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    /**
     * Facility assignment for FACILITY users.
     * NULL for ADMIN users who have access to all facilities.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", 
                foreignKey = @ForeignKey(name = "fk_user_facility"))
    private Facility facility;

    /**
     * Constructor for creating a new user.
     */
    public User(String username, String passwordHash, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Constructor for creating a facility user with facility assignment.
     */
    public User(String username, String passwordHash, UserRole role, Facility facility) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.facility = facility;
    }

    /**
     * Check if this user is an administrator.
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Check if this user is a facility user.
     */
    public boolean isFacilityUser() {
        return role == UserRole.FACILITY;
    }

    /**
     * Check if this user has access to the specified facility.
     * 
     * @param facilityId the facility ID to check access for
     * @return true if user has access, false otherwise
     */
    public boolean hasAccessToFacility(Long facilityId) {
        if (isAdmin()) {
            return true; // Admins have access to all facilities
        }
        
        if (isFacilityUser() && facility != null) {
            return facility.getId().equals(facilityId);
        }
        
        return false;
    }

    /**
     * Assign this user to a facility.
     * Only applicable for FACILITY users.
     */
    public void assignToFacility(Facility facility) {
        if (this.role == UserRole.FACILITY) {
            this.facility = facility;
        }
    }

    /**
     * Remove facility assignment.
     * Only applicable for FACILITY users.
     */
    public void removeFromFacility() {
        if (this.role == UserRole.FACILITY) {
            this.facility = null;
        }
    }

    /**
     * Validation method to ensure business rules are followed.
     */
    @PrePersist
    @PreUpdate
    private void validateUser() {
        // FACILITY users should have a facility assignment
        if (role == UserRole.FACILITY && facility == null) {
            throw new IllegalStateException("FACILITY users must be assigned to a facility");
        }
        
        // ADMIN users should not have facility assignments
        if (role == UserRole.ADMIN && facility != null) {
            throw new IllegalStateException("ADMIN users should not be assigned to a specific facility");
        }
    }
}
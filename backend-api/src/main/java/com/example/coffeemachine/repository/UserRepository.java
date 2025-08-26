package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * Provides data access methods for user management including
 * authentication, role-based queries, and facility assignments.
 */
@Repository
public interface UserRepository extends BaseRepository<User> {

    /**
     * Find an active user by username for authentication.
     * 
     * @param username the username
     * @return Optional containing the user if found and active
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveByUsername(@Param("username") String username);

    /**
     * Find all active users by role.
     * 
     * @param role the user role
     * @return List of active users with the specified role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true ORDER BY u.username")
    List<User> findActiveByRole(@Param("role") UserRole role);

    /**
     * Find all active facility users assigned to a specific facility.
     * 
     * @param facilityId the facility ID
     * @return List of active facility users
     */
    @Query("SELECT u FROM User u WHERE u.facility.id = :facilityId AND u.role = 'FACILITY' AND u.isActive = true ORDER BY u.username")
    List<User> findActiveFacilityUsersByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find all active admin users.
     * 
     * @return List of active admin users
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN' AND u.isActive = true ORDER BY u.username")
    List<User> findActiveAdminUsers();

    /**
     * Find all active facility users (users assigned to any facility).
     * 
     * @return List of active facility users with their facility information
     */
    @Query("SELECT u FROM User u JOIN FETCH u.facility f WHERE u.role = 'FACILITY' AND u.isActive = true AND f.isActive = true ORDER BY f.name, u.username")
    List<User> findActiveFacilityUsersWithFacility();

    /**
     * Find facility users without facility assignment (should not exist in normal operation).
     * 
     * @return List of facility users without facility assignment
     */
    @Query("SELECT u FROM User u WHERE u.role = 'FACILITY' AND u.facility IS NULL AND u.isActive = true")
    List<User> findUnassignedFacilityUsers();

    /**
     * Check if a username already exists (for uniqueness validation).
     * 
     * @param username the username to check
     * @return true if the username exists in any active user
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.isActive = true")
    boolean existsActiveByUsername(@Param("username") String username);

    /**
     * Check if a username exists excluding a specific user ID (for updates).
     * 
     * @param username the username to check
     * @param excludeId the user ID to exclude from the check
     * @return true if the username exists in any other active user
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeId AND u.isActive = true")
    boolean existsActiveByUsernameExcludingId(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * Find users by partial username match (case-insensitive).
     * 
     * @param usernamePattern the username pattern to search for
     * @return List of matching active users
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :usernamePattern, '%')) AND u.isActive = true ORDER BY u.username")
    List<User> findActiveByUsernameContainingIgnoreCase(@Param("usernamePattern") String usernamePattern);

    /**
     * Count users by role.
     * 
     * @param role the user role
     * @return number of active users with the specified role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(@Param("role") UserRole role);

    /**
     * Find users assigned to facilities that have unresolved alerts.
     * 
     * @return List of facility users whose facilities have alert issues
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.facility f " +
           "JOIN f.coffeeMachines cm " +
           "JOIN cm.alerts a " +
           "WHERE u.role = 'FACILITY' " +
           "AND u.isActive = true " +
           "AND f.isActive = true " +
           "AND cm.isActive = true " +
           "AND a.isActive = true " +
           "AND a.resolved = false " +
           "ORDER BY f.name, u.username")
    List<User> findFacilityUsersWithUnresolvedAlerts();

    /**
     * Get user statistics by role.
     * 
     * @return List of objects containing [role, count] for each role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.role")
    List<Object[]> getUserStatisticsByRole();

    /**
     * Find all users with their facility names (for admin overview).
     * 
     * @return List of users with facility information
     */
    @Query("SELECT u, CASE WHEN u.facility IS NOT NULL THEN u.facility.name ELSE 'N/A' END as facilityName " +
           "FROM User u " +
           "WHERE u.isActive = true " +
           "ORDER BY u.role, u.username")
    List<Object[]> findAllActiveUsersWithFacilityNames();

    /**
     * Check if a user has access to a specific facility.
     * 
     * @param userId the user ID
     * @param facilityId the facility ID
     * @return true if the user has access to the facility
     */
    @Query("SELECT COUNT(u) > 0 FROM User u " +
           "WHERE u.id = :userId " +
           "AND u.isActive = true " +
           "AND (u.role = 'ADMIN' OR u.facility.id = :facilityId)")
    boolean hasAccessToFacility(@Param("userId") Long userId, @Param("facilityId") Long facilityId);
}
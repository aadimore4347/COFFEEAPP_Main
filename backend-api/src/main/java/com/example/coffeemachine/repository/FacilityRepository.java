package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.Facility;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Facility entity operations.
 * 
 * Provides data access methods for facility management including
 * facility search, machine counts, and user assignments.
 */
@Repository
public interface FacilityRepository extends BaseRepository<Facility> {

    /**
     * Find an active facility by name.
     * 
     * @param name the facility name
     * @return Optional containing the facility if found
     */
    @Query("SELECT f FROM Facility f WHERE f.name = :name AND f.isActive = true")
    Optional<Facility> findActiveByName(@Param("name") String name);

    /**
     * Find facilities by location containing the given text (case-insensitive).
     * 
     * @param location the location text to search for
     * @return List of matching active facilities
     */
    @Query("SELECT f FROM Facility f WHERE LOWER(f.location) LIKE LOWER(CONCAT('%', :location, '%')) AND f.isActive = true")
    List<Facility> findActiveByLocationContainingIgnoreCase(@Param("location") String location);

    /**
     * Find all facilities with their active machine count.
     * 
     * @return List of facilities with machine counts
     */
    @Query("SELECT f, COUNT(cm) as machineCount FROM Facility f " +
           "LEFT JOIN f.coffeeMachines cm ON cm.isActive = true " +
           "WHERE f.isActive = true " +
           "GROUP BY f " +
           "ORDER BY f.name")
    List<Object[]> findActiveFacilitiesWithMachineCount();

    /**
     * Find facilities that have machines with unresolved alerts.
     * 
     * @return List of facilities with alert issues
     */
    @Query("SELECT DISTINCT f FROM Facility f " +
           "JOIN f.coffeeMachines cm " +
           "JOIN cm.alerts a " +
           "WHERE f.isActive = true " +
           "AND cm.isActive = true " +
           "AND a.isActive = true " +
           "AND a.resolved = false " +
           "ORDER BY f.name")
    List<Facility> findActiveFacilitiesWithUnresolvedAlerts();

    /**
     * Find facilities with their operational machine count (status = ON).
     * 
     * @return List of facilities with operational machine counts
     */
    @Query("SELECT f, COUNT(cm) as operationalCount FROM Facility f " +
           "LEFT JOIN f.coffeeMachines cm ON cm.isActive = true AND cm.status = 'ON' " +
           "WHERE f.isActive = true " +
           "GROUP BY f " +
           "ORDER BY f.name")
    List<Object[]> findActiveFacilitiesWithOperationalMachineCount();

    /**
     * Find facilities by partial name match (case-insensitive).
     * 
     * @param namePattern the name pattern to search for
     * @return List of matching active facilities
     */
    @Query("SELECT f FROM Facility f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :namePattern, '%')) AND f.isActive = true ORDER BY f.name")
    List<Facility> findActiveByNameContainingIgnoreCase(@Param("namePattern") String namePattern);

    /**
     * Get facility statistics including machine counts and alert counts.
     * 
     * @param facilityId the facility ID
     * @return Facility statistics array [totalMachines, operationalMachines, unresolvedAlerts]
     */
    @Query("SELECT " +
           "COUNT(DISTINCT cm), " +
           "COUNT(DISTINCT CASE WHEN cm.status = 'ON' THEN cm END), " +
           "COUNT(DISTINCT CASE WHEN a.resolved = false THEN a END) " +
           "FROM Facility f " +
           "LEFT JOIN f.coffeeMachines cm ON cm.isActive = true " +
           "LEFT JOIN cm.alerts a ON a.isActive = true " +
           "WHERE f.id = :facilityId AND f.isActive = true")
    Object[] getFacilityStatistics(@Param("facilityId") Long facilityId);

    /**
     * Check if a facility name already exists (for uniqueness validation).
     * 
     * @param name the facility name to check
     * @return true if the name exists in any active facility
     */
    @Query("SELECT COUNT(f) > 0 FROM Facility f WHERE f.name = :name AND f.isActive = true")
    boolean existsActiveByName(@Param("name") String name);

    /**
     * Check if a facility name exists excluding a specific facility ID (for updates).
     * 
     * @param name the facility name to check
     * @param excludeId the facility ID to exclude from the check
     * @return true if the name exists in any other active facility
     */
    @Query("SELECT COUNT(f) > 0 FROM Facility f WHERE f.name = :name AND f.id != :excludeId AND f.isActive = true")
    boolean existsActiveByNameExcludingId(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * Find facilities with low supply machines (any supply below threshold).
     * 
     * @param threshold the supply threshold percentage
     * @return List of facilities with low supply machines
     */
    @Query("SELECT DISTINCT f FROM Facility f " +
           "JOIN f.coffeeMachines cm " +
           "WHERE f.isActive = true " +
           "AND cm.isActive = true " +
           "AND (cm.waterLevel < :threshold OR cm.milkLevel < :threshold OR cm.beansLevel < :threshold) " +
           "ORDER BY f.name")
    List<Facility> findActiveFacilitiesWithLowSupplyMachines(@Param("threshold") int threshold);

    /**
     * Get all facilities ordered by name for dropdown/selection lists.
     * 
     * @return List of active facilities ordered by name
     */
    @Query("SELECT f FROM Facility f WHERE f.isActive = true ORDER BY f.name")
    List<Facility> findAllActiveOrderByName();
}
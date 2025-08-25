package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.Alert;
import com.example.coffeemachine.domain.AlertType;
import com.example.coffeemachine.domain.Severity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Alert entity operations.
 * 
 * Provides data access methods for alert management including
 * alert monitoring, resolution tracking, and facility-based queries.
 */
@Repository
public interface AlertRepository extends BaseRepository<Alert> {

    /**
     * Find all active unresolved alerts ordered by severity and creation time.
     * 
     * @return List of active unresolved alerts
     */
    @Query("SELECT a FROM Alert a WHERE a.isActive = true AND a.resolved = false " +
           "ORDER BY " +
           "CASE a.severity " +
           "  WHEN 'CRITICAL' THEN 1 " +
           "  WHEN 'WARNING' THEN 2 " +
           "  WHEN 'INFO' THEN 3 " +
           "END, a.createdAt ASC")
    List<Alert> findActiveUnresolvedOrderedBySeverity();

    /**
     * Find active unresolved alerts for a specific machine.
     * 
     * @param machineId the machine ID
     * @return List of unresolved alerts for the machine
     */
    @Query("SELECT a FROM Alert a WHERE a.machine.id = :machineId AND a.isActive = true AND a.resolved = false ORDER BY a.createdAt ASC")
    List<Alert> findActiveUnresolvedByMachineId(@Param("machineId") Long machineId);

    /**
     * Find active unresolved alerts for all machines in a facility.
     * 
     * @param facilityId the facility ID
     * @return List of unresolved alerts for the facility
     */
    @Query("SELECT a FROM Alert a WHERE a.machine.facility.id = :facilityId AND a.isActive = true AND a.resolved = false " +
           "ORDER BY " +
           "CASE a.severity " +
           "  WHEN 'CRITICAL' THEN 1 " +
           "  WHEN 'WARNING' THEN 2 " +
           "  WHEN 'INFO' THEN 3 " +
           "END, a.createdAt ASC")
    List<Alert> findActiveUnresolvedByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find alerts by type for a specific machine.
     * 
     * @param machineId the machine ID
     * @param alertType the alert type
     * @return List of alerts of the specified type for the machine
     */
    @Query("SELECT a FROM Alert a WHERE a.machine.id = :machineId AND a.type = :alertType AND a.isActive = true ORDER BY a.createdAt DESC")
    List<Alert> findActiveByMachineIdAndType(@Param("machineId") Long machineId, @Param("alertType") AlertType alertType);

    /**
     * Find alerts by severity across all machines.
     * 
     * @param severity the alert severity
     * @return List of active alerts with the specified severity
     */
    @Query("SELECT a FROM Alert a WHERE a.severity = :severity AND a.isActive = true ORDER BY a.createdAt DESC")
    List<Alert> findActiveBySeverity(@Param("severity") Severity severity);

    /**
     * Find critical unresolved alerts that require immediate attention.
     * 
     * @return List of critical unresolved alerts
     */
    @Query("SELECT a FROM Alert a WHERE a.severity = 'CRITICAL' AND a.resolved = false AND a.isActive = true ORDER BY a.createdAt ASC")
    List<Alert> findActiveCriticalUnresolved();

    /**
     * Find critical unresolved alerts for a specific facility.
     * 
     * @param facilityId the facility ID
     * @return List of critical unresolved alerts for the facility
     */
    @Query("SELECT a FROM Alert a WHERE a.machine.facility.id = :facilityId AND a.severity = 'CRITICAL' AND a.resolved = false AND a.isActive = true ORDER BY a.createdAt ASC")
    List<Alert> findActiveCriticalUnresolvedByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find the most recent unresolved alert of a specific type for a machine.
     * Used for alert debouncing to prevent duplicate alerts.
     * 
     * @param machineId the machine ID
     * @param alertType the alert type
     * @return Optional containing the most recent unresolved alert
     */
    @Query("SELECT a FROM Alert a WHERE a.machine.id = :machineId AND a.type = :alertType AND a.resolved = false AND a.isActive = true ORDER BY a.createdAt DESC LIMIT 1")
    Optional<Alert> findMostRecentUnresolvedByMachineIdAndType(@Param("machineId") Long machineId, @Param("alertType") AlertType alertType);

    /**
     * Find alerts created within a specific time period.
     * 
     * @param startTime the start time
     * @param endTime the end time
     * @return List of alerts created within the time period
     */
    @Query("SELECT a FROM Alert a WHERE a.createdAt BETWEEN :startTime AND :endTime AND a.isActive = true ORDER BY a.createdAt DESC")
    List<Alert> findActiveByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * Count unresolved alerts by severity.
     * 
     * @return List of [severity, count] for unresolved alerts
     */
    @Query("SELECT a.severity, COUNT(a) FROM Alert a WHERE a.resolved = false AND a.isActive = true GROUP BY a.severity")
    List<Object[]> countUnresolvedBySeverity();

    /**
     * Count unresolved alerts by type.
     * 
     * @return List of [alertType, count] for unresolved alerts
     */
    @Query("SELECT a.type, COUNT(a) FROM Alert a WHERE a.resolved = false AND a.isActive = true GROUP BY a.type")
    List<Object[]> countUnresolvedByType();

    /**
     * Get alert statistics for a facility.
     * 
     * @param facilityId the facility ID
     * @return Alert statistics array [total, unresolved, critical, resolved]
     */
    @Query("SELECT " +
           "COUNT(a), " +
           "COUNT(CASE WHEN a.resolved = false THEN 1 END), " +
           "COUNT(CASE WHEN a.severity = 'CRITICAL' AND a.resolved = false THEN 1 END), " +
           "COUNT(CASE WHEN a.resolved = true THEN 1 END) " +
           "FROM Alert a " +
           "WHERE a.machine.facility.id = :facilityId AND a.isActive = true")
    Object[] getAlertStatisticsForFacility(@Param("facilityId") Long facilityId);

    /**
     * Get overall alert statistics across all facilities.
     * 
     * @return Overall alert statistics array [total, unresolved, critical, resolved]
     */
    @Query("SELECT " +
           "COUNT(a), " +
           "COUNT(CASE WHEN a.resolved = false THEN 1 END), " +
           "COUNT(CASE WHEN a.severity = 'CRITICAL' AND a.resolved = false THEN 1 END), " +
           "COUNT(CASE WHEN a.resolved = true THEN 1 END) " +
           "FROM Alert a " +
           "WHERE a.isActive = true")
    Object[] getOverallAlertStatistics();

    /**
     * Bulk resolve alerts of a specific type for a machine.
     * Used when an issue is fixed to resolve all related alerts.
     * 
     * @param machineId the machine ID
     * @param alertType the alert type to resolve
     * @return number of alerts resolved
     */
    @Modifying
    @Query("UPDATE Alert a SET a.resolved = true, a.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE a.machine.id = :machineId AND a.type = :alertType AND a.resolved = false AND a.isActive = true")
    int resolveAlertsByMachineIdAndType(@Param("machineId") Long machineId, @Param("alertType") AlertType alertType);

    /**
     * Resolve a specific alert by ID.
     * 
     * @param alertId the alert ID
     * @return number of alerts resolved (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE Alert a SET a.resolved = true, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :alertId AND a.isActive = true")
    int resolveAlertById(@Param("alertId") Long alertId);

    /**
     * Find old resolved alerts that can be archived (older than specified days).
     * 
     * @param cutoffTime the cutoff time for archiving
     * @return List of old resolved alerts
     */
    @Query("SELECT a FROM Alert a WHERE a.resolved = true AND a.updatedAt < :cutoffTime AND a.isActive = true ORDER BY a.updatedAt ASC")
    List<Alert> findOldResolvedAlerts(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find long-running unresolved alerts (older than specified hours).
     * 
     * @param cutoffTime the cutoff time for long-running alerts
     * @return List of long-running unresolved alerts
     */
    @Query("SELECT a FROM Alert a WHERE a.resolved = false AND a.createdAt < :cutoffTime AND a.isActive = true " +
           "ORDER BY a.createdAt ASC")
    List<Alert> findLongRunningUnresolvedAlerts(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Get daily alert counts for trend analysis.
     * 
     * @param facilityId the facility ID (null for all facilities)
     * @param startTime the start time for analysis
     * @param endTime the end time for analysis
     * @return List of [date, alertCount] for daily trends
     */
    @Query("SELECT DATE(a.createdAt), COUNT(a) " +
           "FROM Alert a " +
           "WHERE (:facilityId IS NULL OR a.machine.facility.id = :facilityId) " +
           "AND a.createdAt BETWEEN :startTime AND :endTime " +
           "AND a.isActive = true " +
           "GROUP BY DATE(a.createdAt) " +
           "ORDER BY DATE(a.createdAt)")
    List<Object[]> getDailyAlertCounts(@Param("facilityId") Long facilityId, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * Find machines with the highest alert frequency.
     * 
     * @param facilityId the facility ID (null for all facilities)
     * @param startTime the start time for analysis
     * @param endTime the end time for analysis
     * @return List of [machineId, alertCount] ordered by frequency
     */
    @Query("SELECT a.machine.id, COUNT(a) " +
           "FROM Alert a " +
           "WHERE (:facilityId IS NULL OR a.machine.facility.id = :facilityId) " +
           "AND a.createdAt BETWEEN :startTime AND :endTime " +
           "AND a.isActive = true " +
           "GROUP BY a.machine.id " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getMachinesByAlertFrequency(@Param("facilityId") Long facilityId, 
                                              @Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);
}
package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.BrewType;
import com.example.coffeemachine.domain.UsageHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for UsageHistory entity operations.
 * 
 * Provides data access methods for usage analytics including
 * consumption patterns, brew statistics, and facility-level reporting.
 */
@Repository
public interface UsageHistoryRepository extends BaseRepository<UsageHistory> {

    /**
     * Find usage history for a specific machine ordered by timestamp descending.
     * 
     * @param machineId the machine ID
     * @return List of usage records for the machine
     */
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machine.id = :machineId AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findActiveByMachineIdOrderByTimestampDesc(@Param("machineId") Long machineId);

    /**
     * Find recent usage history for a machine (within specified hours).
     * 
     * @param machineId the machine ID
     * @param hoursAgo number of hours to look back
     * @return List of recent usage records
     */
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machine.id = :machineId " +
           "AND uh.timestamp >= :cutoffTime AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findActiveByMachineIdSince(@Param("machineId") Long machineId, @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find usage history for all machines in a facility.
     * 
     * @param facilityId the facility ID
     * @return List of usage records for the facility
     */
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machine.facility.id = :facilityId AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findActiveByFacilityIdOrderByTimestampDesc(@Param("facilityId") Long facilityId);

    /**
     * Find recent usage history for a facility (within specified hours).
     * 
     * @param facilityId the facility ID
     * @param hoursAgo number of hours to look back
     * @return List of recent usage records for the facility
     */
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp >= :cutoffTime AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findActiveByFacilityIdSince(@Param("facilityId") Long facilityId, @Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find usage history by brew type for analytics.
     * 
     * @param brewType the brew type
     * @return List of usage records for the specified brew type
     */
    @Query("SELECT uh FROM UsageHistory uh WHERE uh.brewType = :brewType AND uh.isActive = true ORDER BY uh.timestamp DESC")
    List<UsageHistory> findActiveByBrewType(@Param("brewType") BrewType brewType);

    /**
     * Get usage statistics by brew type for a facility within a time period.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of [brewType, count, avgVolume] statistics
     */
    @Query("SELECT uh.brewType, COUNT(uh), AVG(uh.volumeMl), AVG(uh.tempAtBrew) " +
           "FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "GROUP BY uh.brewType " +
           "ORDER BY COUNT(uh) DESC")
    List<Object[]> getUsageStatisticsByBrewTypeForFacility(@Param("facilityId") Long facilityId, 
                                                          @Param("startTime") LocalDateTime startTime, 
                                                          @Param("endTime") LocalDateTime endTime);

    /**
     * Get overall usage statistics by brew type within a time period.
     * 
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of [brewType, count, avgVolume] statistics
     */
    @Query("SELECT uh.brewType, COUNT(uh), AVG(uh.volumeMl), AVG(uh.tempAtBrew) " +
           "FROM UsageHistory uh " +
           "WHERE uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "GROUP BY uh.brewType " +
           "ORDER BY COUNT(uh) DESC")
    List<Object[]> getOverallUsageStatisticsByBrewType(@Param("startTime") LocalDateTime startTime, 
                                                      @Param("endTime") LocalDateTime endTime);

    /**
     * Get hourly usage patterns for a facility.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of [hour, count] for usage patterns
     */
    @Query("SELECT HOUR(uh.timestamp), COUNT(uh) " +
           "FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "GROUP BY HOUR(uh.timestamp) " +
           "ORDER BY HOUR(uh.timestamp)")
    List<Object[]> getHourlyUsagePatternsForFacility(@Param("facilityId") Long facilityId, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * Get daily usage count for a machine within a time period.
     * 
     * @param machineId the machine ID
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of [date, count] for daily usage
     */
    @Query("SELECT DATE(uh.timestamp), COUNT(uh) " +
           "FROM UsageHistory uh " +
           "WHERE uh.machine.id = :machineId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "GROUP BY DATE(uh.timestamp) " +
           "ORDER BY DATE(uh.timestamp)")
    List<Object[]> getDailyUsageCountForMachine(@Param("machineId") Long machineId, 
                                               @Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * Find peak hours usage (7-9 AM and 1-3 PM) for analytics.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of peak hours usage records
     */
    @Query("SELECT uh FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "AND (HOUR(uh.timestamp) BETWEEN 7 AND 9 OR HOUR(uh.timestamp) BETWEEN 13 AND 15) " +
           "ORDER BY uh.timestamp DESC")
    List<UsageHistory> findPeakHoursUsageForFacility(@Param("facilityId") Long facilityId, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);

    /**
     * Get machine utilization statistics for a facility.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for the analysis
     * @param endTime the end time for the analysis
     * @return List of [machineId, usageCount] statistics
     */
    @Query("SELECT uh.machine.id, COUNT(uh) " +
           "FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true " +
           "GROUP BY uh.machine.id " +
           "ORDER BY COUNT(uh) DESC")
    List<Object[]> getMachineUtilizationForFacility(@Param("facilityId") Long facilityId, 
                                                    @Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime);

    /**
     * Count total beverages brewed for a machine within a time period.
     * 
     * @param machineId the machine ID
     * @param startTime the start time for counting
     * @param endTime the end time for counting
     * @return total number of beverages brewed
     */
    @Query("SELECT COUNT(uh) FROM UsageHistory uh " +
           "WHERE uh.machine.id = :machineId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true")
    long countUsageForMachine(@Param("machineId") Long machineId, 
                             @Param("startTime") LocalDateTime startTime, 
                             @Param("endTime") LocalDateTime endTime);

    /**
     * Count total beverages brewed for a facility within a time period.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for counting
     * @param endTime the end time for counting
     * @return total number of beverages brewed
     */
    @Query("SELECT COUNT(uh) FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.isActive = true")
    long countUsageForFacility(@Param("facilityId") Long facilityId, 
                              @Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);

    /**
     * Find usage with optimal brewing temperature for quality analysis.
     * 
     * @param brewType the brew type
     * @param minTemp minimum optimal temperature
     * @param maxTemp maximum optimal temperature
     * @return List of usage records with optimal temperature
     */
    @Query("SELECT uh FROM UsageHistory uh " +
           "WHERE uh.brewType = :brewType " +
           "AND uh.tempAtBrew BETWEEN :minTemp AND :maxTemp " +
           "AND uh.isActive = true " +
           "ORDER BY uh.timestamp DESC")
    List<UsageHistory> findOptimalTemperatureUsage(@Param("brewType") BrewType brewType, 
                                                   @Param("minTemp") double minTemp, 
                                                   @Param("maxTemp") double maxTemp);

    /**
     * Get average brewing temperature by brew type for quality monitoring.
     * 
     * @param facilityId the facility ID
     * @param startTime the start time for analysis
     * @param endTime the end time for analysis
     * @return List of [brewType, avgTemperature, count] statistics
     */
    @Query("SELECT uh.brewType, AVG(uh.tempAtBrew), COUNT(uh) " +
           "FROM UsageHistory uh " +
           "WHERE uh.machine.facility.id = :facilityId " +
           "AND uh.timestamp BETWEEN :startTime AND :endTime " +
           "AND uh.tempAtBrew IS NOT NULL " +
           "AND uh.isActive = true " +
           "GROUP BY uh.brewType " +
           "ORDER BY uh.brewType")
    List<Object[]> getAverageBrewingTemperatureByType(@Param("facilityId") Long facilityId, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);
}
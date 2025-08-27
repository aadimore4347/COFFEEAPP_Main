package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.MachineStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CoffeeMachine entity operations.
 * 
 * Provides data access methods for coffee machine management including
 * status monitoring, supply level tracking, and facility-based queries.
 */
@Repository
public interface CoffeeMachineRepository extends BaseRepository<CoffeeMachine> {

    /**
     * Find all active coffee machines in a specific facility.
     * 
     * @param facilityId the facility ID
     * @return List of active coffee machines in the facility
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.facility.id = :facilityId AND cm.isActive = true ORDER BY cm.id")
    List<CoffeeMachine> findActiveByFacilityId(@Param("facilityId") Long facilityId);

    /**
     * Find all active coffee machines by status.
     * 
     * @param status the machine status
     * @return List of active machines with the specified status
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.status = :status AND cm.isActive = true ORDER BY cm.facility.name, cm.id")
    List<CoffeeMachine> findActiveByStatus(@Param("status") MachineStatus status);

    /**
     * Find coffee machines in a facility by status.
     * 
     * @param facilityId the facility ID
     * @param status the machine status
     * @return List of machines in the facility with the specified status
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.facility.id = :facilityId AND cm.status = :status AND cm.isActive = true ORDER BY cm.id")
    List<CoffeeMachine> findActiveByFacilityIdAndStatus(@Param("facilityId") Long facilityId, @Param("status") MachineStatus status);

    /**
     * Find machines with low water level below threshold.
     * 
     * @param threshold the water level threshold percentage
     * @return List of machines with low water levels
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.waterLevel < :threshold AND cm.isActive = true ORDER BY cm.waterLevel ASC")
    List<CoffeeMachine> findActiveWithLowWaterLevel(@Param("threshold") int threshold);

    /**
     * Find machines with low milk level below threshold.
     * 
     * @param threshold the milk level threshold percentage
     * @return List of machines with low milk levels
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.milkLevel < :threshold AND cm.isActive = true ORDER BY cm.milkLevel ASC")
    List<CoffeeMachine> findActiveWithLowMilkLevel(@Param("threshold") int threshold);

    /**
     * Find machines with low beans level below threshold.
     * 
     * @param threshold the beans level threshold percentage
     * @return List of machines with low beans levels
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.beansLevel < :threshold AND cm.isActive = true ORDER BY cm.beansLevel ASC")
    List<CoffeeMachine> findActiveWithLowBeansLevel(@Param("threshold") int threshold);

    /**
     * Find machines with any supply level below threshold.
     * 
     * @param threshold the supply level threshold percentage
     * @return List of machines with low supply levels
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true AND " +
           "(cm.waterLevel < :threshold OR cm.milkLevel < :threshold OR cm.beansLevel < :threshold) " +
           "ORDER BY cm.facility.name, cm.id")
    List<CoffeeMachine> findActiveWithLowSupplyLevels(@Param("threshold") int threshold);

    /**
     * Find machines that need attention (ERROR status or low supplies).
     * 
     * @param supplyThreshold the supply level threshold percentage
     * @return List of machines needing attention
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true AND " +
           "(cm.status = 'ERROR' OR cm.waterLevel < :supplyThreshold OR cm.milkLevel < :supplyThreshold OR cm.beansLevel < :supplyThreshold) " +
           "ORDER BY " +
           "CASE WHEN cm.status = 'ERROR' THEN 1 ELSE 2 END, " +
           "cm.facility.name, cm.id")
    List<CoffeeMachine> findActiveMachinesNeedingAttention(@Param("supplyThreshold") int supplyThreshold);

    /**
     * Find machines with unresolved alerts.
     * 
     * @return List of machines with active unresolved alerts
     */
    @Query("SELECT DISTINCT cm FROM CoffeeMachine cm " +
           "JOIN cm.alerts a " +
           "WHERE cm.isActive = true " +
           "AND a.isActive = true " +
           "AND a.resolved = false " +
           "ORDER BY cm.facility.name, cm.id")
    List<CoffeeMachine> findActiveWithUnresolvedAlerts();

    /**
     * Find machines in a facility with unresolved alerts.
     * 
     * @param facilityId the facility ID
     * @return List of machines in the facility with unresolved alerts
     */
    @Query("SELECT DISTINCT cm FROM CoffeeMachine cm " +
           "JOIN cm.alerts a " +
           "WHERE cm.facility.id = :facilityId " +
           "AND cm.isActive = true " +
           "AND a.isActive = true " +
           "AND a.resolved = false " +
           "ORDER BY cm.id")
    List<CoffeeMachine> findActiveInFacilityWithUnresolvedAlerts(@Param("facilityId") Long facilityId);

    /**
     * Find machines that haven't been updated recently (potential connectivity issues).
     * 
     * @param cutoffTime the cutoff time for recent updates
     * @return List of machines not updated since the cutoff time
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.updatedAt < :cutoffTime AND cm.isActive = true ORDER BY cm.updatedAt ASC")
    List<CoffeeMachine> findActiveNotUpdatedSince(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Get machine statistics for a facility.
     * 
     * @param facilityId the facility ID
     * @return Machine statistics array [total, operational, error, offline]
     */
    @Query("SELECT " +
           "COUNT(cm), " +
           "COUNT(CASE WHEN cm.status = 'ON' THEN 1 END), " +
           "COUNT(CASE WHEN cm.status = 'ERROR' THEN 1 END), " +
           "COUNT(CASE WHEN cm.status = 'OFF' THEN 1 END) " +
           "FROM CoffeeMachine cm " +
           "WHERE cm.facility.id = :facilityId AND cm.isActive = true")
    Object[] getMachineStatisticsForFacility(@Param("facilityId") Long facilityId);

    /**
     * Get overall machine statistics across all facilities.
     * 
     * @return Overall machine statistics array [total, operational, error, offline]
     */
    @Query("SELECT " +
           "COUNT(cm), " +
           "COUNT(CASE WHEN cm.status = 'ON' THEN 1 END), " +
           "COUNT(CASE WHEN cm.status = 'ERROR' THEN 1 END), " +
           "COUNT(CASE WHEN cm.status = 'OFF' THEN 1 END) " +
           "FROM CoffeeMachine cm " +
           "WHERE cm.isActive = true")
    Object[] getOverallMachineStatistics();

    /**
     * Find machines with temperature readings outside normal range.
     * 
     * @param minTemp minimum acceptable temperature
     * @param maxTemp maximum acceptable temperature
     * @return List of machines with abnormal temperatures
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true " +
           "AND cm.temperature IS NOT NULL " +
           "AND (cm.temperature < :minTemp OR cm.temperature > :maxTemp) " +
           "ORDER BY cm.temperature")
    List<CoffeeMachine> findActiveWithAbnormalTemperature(@Param("minTemp") double minTemp, @Param("maxTemp") double maxTemp);

    /**
     * Find operational machines (status = ON) in a facility.
     * 
     * @param facilityId the facility ID
     * @return List of operational machines in the facility
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.facility.id = :facilityId AND cm.status = 'ON' AND cm.isActive = true ORDER BY cm.id")
    List<CoffeeMachine> findOperationalInFacility(@Param("facilityId") Long facilityId);

    /**
     * Find all operational machines across all facilities.
     * 
     * @return List of all operational machines
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.status = 'ON' AND cm.isActive = true ORDER BY cm.facility.name, cm.id")
    List<CoffeeMachine> findAllOperational();

    /**
     * Count machines by status in a facility.
     * 
     * @param facilityId the facility ID
     * @param status the machine status
     * @return count of machines with the specified status in the facility
     */
    @Query("SELECT COUNT(cm) FROM CoffeeMachine cm WHERE cm.facility.id = :facilityId AND cm.status = :status AND cm.isActive = true")
    long countByFacilityIdAndStatus(@Param("facilityId") Long facilityId, @Param("status") MachineStatus status);

    /**
     * Find machines with adequate supply levels (all supplies above threshold).
     * 
     * @param threshold the supply level threshold percentage
     * @return List of machines with adequate supplies
     */
    @Query("SELECT cm FROM CoffeeMachine cm WHERE cm.isActive = true " +
           "AND (cm.waterLevel IS NULL OR cm.waterLevel >= :threshold) " +
           "AND (cm.milkLevel IS NULL OR cm.milkLevel >= :threshold) " +
           "AND (cm.beansLevel IS NULL OR cm.beansLevel >= :threshold) " +
           "ORDER BY cm.facility.name, cm.id")
    List<CoffeeMachine> findActiveWithAdequateSupplies(@Param("threshold") int threshold);
}
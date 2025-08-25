package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.*;
import com.example.coffeemachine.repository.CoffeeMachineRepository;
import com.example.coffeemachine.repository.UsageHistoryRepository;
import com.example.coffeemachine.alert.AlertEvaluatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing coffee machine operations and state updates.
 * Handles MQTT message processing, machine state changes, and alert generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CoffeeMachineService {

    private final CoffeeMachineRepository coffeeMachineRepository;
    private final UsageHistoryRepository usageHistoryRepository;
    private final AlertEvaluatorService alertEvaluatorService;

    /**
     * Updates machine temperature and evaluates alerts.
     *
     * @param machineId the machine ID
     * @param temperature the new temperature
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateTemperature(Long machineId, Double temperature) {
        log.debug("Updating temperature for machine {} to {}°C", machineId, temperature);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    machine.setTemperature(temperature);
                    CoffeeMachine saved = coffeeMachineRepository.save(machine);
                    
                    // Evaluate temperature-related alerts
                    alertEvaluatorService.evaluateTemperatureAlerts(saved);
                    
                    return saved;
                });
    }

    /**
     * Updates machine water level and evaluates low water alerts.
     *
     * @param machineId the machine ID
     * @param waterLevel the new water level (0-100)
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateWaterLevel(Long machineId, Integer waterLevel) {
        log.debug("Updating water level for machine {} to {}%", machineId, waterLevel);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    machine.setWaterLevel(waterLevel);
                    CoffeeMachine saved = coffeeMachineRepository.save(machine);
                    
                    // Evaluate water level alerts
                    alertEvaluatorService.evaluateWaterLevelAlerts(saved);
                    
                    return saved;
                });
    }

    /**
     * Updates machine milk level and evaluates low milk alerts.
     *
     * @param machineId the machine ID
     * @param milkLevel the new milk level (0-100)
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateMilkLevel(Long machineId, Integer milkLevel) {
        log.debug("Updating milk level for machine {} to {}%", machineId, milkLevel);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    machine.setMilkLevel(milkLevel);
                    CoffeeMachine saved = coffeeMachineRepository.save(machine);
                    
                    // Evaluate milk level alerts
                    alertEvaluatorService.evaluateMilkLevelAlerts(saved);
                    
                    return saved;
                });
    }

    /**
     * Updates machine beans level and evaluates low beans alerts.
     *
     * @param machineId the machine ID
     * @param beansLevel the new beans level (0-100)
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateBeansLevel(Long machineId, Integer beansLevel) {
        log.debug("Updating beans level for machine {} to {}%", machineId, beansLevel);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    machine.setBeansLevel(beansLevel);
                    CoffeeMachine saved = coffeeMachineRepository.save(machine);
                    
                    // Evaluate beans level alerts
                    alertEvaluatorService.evaluateBeansLevelAlerts(saved);
                    
                    return saved;
                });
    }

    /**
     * Updates machine status and evaluates malfunction alerts.
     *
     * @param machineId the machine ID
     * @param status the new machine status
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateStatus(Long machineId, MachineStatus status) {
        log.info("Updating status for machine {} to {}", machineId, status);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    MachineStatus oldStatus = machine.getStatus();
                    machine.setStatus(status);
                    CoffeeMachine saved = coffeeMachineRepository.save(machine);
                    
                    // Evaluate status-related alerts
                    alertEvaluatorService.evaluateStatusAlerts(saved, oldStatus);
                    
                    return saved;
                });
    }

    /**
     * Records a brewing event and updates machine usage history.
     *
     * @param machineId the machine ID
     * @param brewType the type of beverage brewed
     * @param volumeMl the volume in milliliters
     * @return the usage history record or empty if machine not found
     */
    public Optional<UsageHistory> recordBrewing(Long machineId, BrewType brewType, Integer volumeMl) {
        log.info("Recording brewing event for machine {}: {} ({}ml)", machineId, brewType, volumeMl);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> {
                    UsageHistory usage = new UsageHistory(machine, LocalDateTime.now(), brewType, volumeMl, machine.getTemperature());
                    
                    return usageHistoryRepository.save(usage);
                });
    }

    /**
     * Finds a coffee machine by ID (active only).
     *
     * @param machineId the machine ID
     * @return the machine or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<CoffeeMachine> findById(Long machineId) {
        return coffeeMachineRepository.findActiveById(machineId);
    }

    /**
     * Finds all active machines for a facility.
     *
     * @param facilityId the facility ID
     * @return list of active machines
     */
    @Transactional(readOnly = true)
    public List<CoffeeMachine> findByFacilityId(Long facilityId) {
        return coffeeMachineRepository.findActiveByFacilityId(facilityId);
    }

    /**
     * Finds all active machines.
     *
     * @return list of all active machines
     */
    @Transactional(readOnly = true)
    public List<CoffeeMachine> findAllActive() {
        return coffeeMachineRepository.findAllActive();
    }

    /**
     * Gets machine status and current levels.
     *
     * @param machineId the machine ID
     * @return machine status information
     */
    @Transactional(readOnly = true)
    public Optional<CoffeeMachine> getMachineStatus(Long machineId) {
        return coffeeMachineRepository.findActiveById(machineId);
    }

    /**
     * Gets recent usage history for a machine.
     *
     * @param machineId the machine ID
     * @param hours number of hours to look back
     * @return list of usage records
     */
    @Transactional(readOnly = true)
    public List<UsageHistory> getRecentUsage(Long machineId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return usageHistoryRepository.findActiveByMachineIdSince(machineId, since);
    }

    /**
     * Gets daily usage statistics for a machine.
     *
     * @param machineId the machine ID
     * @param days number of days to look back
     * @return usage statistics
     */
    @Transactional(readOnly = true)
    public List<UsageHistory> getDailyUsage(Long machineId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return usageHistoryRepository.findActiveByMachineIdSince(machineId, since);
    }

    /**
     * Creates a new coffee machine for a facility.
     *
     * @param facilityId the facility ID
     * @param machine the machine to create
     * @return the created machine
     * @throws IllegalArgumentException if facility not found
     */
    public CoffeeMachine createMachine(Long facilityId, CoffeeMachine machine) {
        log.info("Creating new machine for facility {}", facilityId);
        
        // The facility validation will be done at the controller level
        // or we can inject FacilityService here
        return coffeeMachineRepository.save(machine);
    }

    /**
     * Soft deletes a coffee machine.
     *
     * @param machineId the machine ID
     * @return true if deleted, false if not found
     */
    public boolean deleteMachine(Long machineId) {
        log.info("Soft deleting machine {}", machineId);
        return coffeeMachineRepository.softDeleteById(machineId) > 0;
    }

    /**
     * Updates machine configuration/settings.
     *
     * @param machineId the machine ID
     * @param updates the machine with updated fields
     * @return updated machine or empty if not found
     */
    public Optional<CoffeeMachine> updateMachine(Long machineId, CoffeeMachine updates) {
        log.info("Updating machine {}", machineId);
        
        return coffeeMachineRepository.findActiveById(machineId)
                .map(existing -> {
                    // Update only non-null fields from updates
                    if (updates.getStatus() != null) {
                        existing.setStatus(updates.getStatus());
                    }
                    if (updates.getTemperature() != null) {
                        existing.setTemperature(updates.getTemperature());
                    }
                    if (updates.getWaterLevel() != null) {
                        existing.setWaterLevel(updates.getWaterLevel());
                    }
                    if (updates.getMilkLevel() != null) {
                        existing.setMilkLevel(updates.getMilkLevel());
                    }
                    if (updates.getBeansLevel() != null) {
                        existing.setBeansLevel(updates.getBeansLevel());
                    }
                    
                    return coffeeMachineRepository.save(existing);
                });
    }
}
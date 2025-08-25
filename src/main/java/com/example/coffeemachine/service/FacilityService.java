package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.domain.MachineStatus;
import com.example.coffeemachine.repository.CoffeeMachineRepository;
import com.example.coffeemachine.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing facilities and their coffee machines.
 * Provides operations for facility management and machine assignment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final CoffeeMachineRepository coffeeMachineRepository;

    /**
     * Creates a new facility.
     *
     * @param facility the facility to create
     * @return the created facility
     * @throws IllegalArgumentException if facility name already exists
     */
    public Facility createFacility(Facility facility) {
        log.info("Creating new facility: {}", facility.getName());
        
        // Check if facility name already exists
        if (facilityRepository.findActiveByName(facility.getName()).isPresent()) {
            throw new IllegalArgumentException("Facility with name '" + facility.getName() + "' already exists");
        }
        
        return facilityRepository.save(facility);
    }

    /**
     * Finds a facility by ID (active only).
     *
     * @param facilityId the facility ID
     * @return the facility or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Facility> findById(Long facilityId) {
        return facilityRepository.findActiveById(facilityId);
    }

    /**
     * Finds all active facilities.
     *
     * @return list of all active facilities
     */
    @Transactional(readOnly = true)
    public List<Facility> findAllActive() {
        return facilityRepository.findAllActive();
    }

    /**
     * Updates an existing facility.
     *
     * @param facilityId the facility ID
     * @param updates the facility with updated fields
     * @return updated facility or empty if not found
     */
    public Optional<Facility> updateFacility(Long facilityId, Facility updates) {
        log.info("Updating facility {}", facilityId);
        
        return facilityRepository.findActiveById(facilityId)
                .map(existing -> {
                    // Update only non-null fields
                    if (updates.getName() != null && !updates.getName().trim().isEmpty()) {
                        // Check if new name conflicts with existing facility
                        Optional<Facility> nameConflict = facilityRepository.findActiveByName(updates.getName());
                        if (nameConflict.isPresent() && !nameConflict.get().getId().equals(facilityId)) {
                            throw new IllegalArgumentException("Facility with name '" + updates.getName() + "' already exists");
                        }
                        existing.setName(updates.getName().trim());
                    }
                    
                    if (updates.getLocation() != null && !updates.getLocation().trim().isEmpty()) {
                        existing.setLocation(updates.getLocation().trim());
                    }
                    
                    return facilityRepository.save(existing);
                });
    }

    /**
     * Soft deletes a facility and all its machines.
     *
     * @param facilityId the facility ID
     * @return true if deleted, false if not found
     */
    public boolean deleteFacility(Long facilityId) {
        log.info("Soft deleting facility {} and all its machines", facilityId);
        
        Optional<Facility> facility = facilityRepository.findActiveById(facilityId);
        if (facility.isEmpty()) {
            return false;
        }
        
        // First, soft delete all machines in this facility
        List<CoffeeMachine> machines = coffeeMachineRepository.findActiveByFacilityId(facilityId);
        for (CoffeeMachine machine : machines) {
            coffeeMachineRepository.softDeleteById(machine.getId());
        }
        
        // Then soft delete the facility
        return facilityRepository.softDeleteById(facilityId) > 0;
    }

    /**
     * Adds a new coffee machine to a facility.
     *
     * @param facilityId the facility ID
     * @param machine the machine to add
     * @return the created machine
     * @throws IllegalArgumentException if facility not found
     */
    public CoffeeMachine addMachineToFacility(Long facilityId, CoffeeMachine machine) {
        log.info("Adding new machine to facility {}", facilityId);
        
        Facility facility = facilityRepository.findActiveById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + facilityId));
        
        // Set facility and default values
        machine.setFacility(facility);
        if (machine.getStatus() == null) {
            machine.setStatus(MachineStatus.OFF);
        }
        if (machine.getTemperature() == null) {
            machine.setTemperature(0.0);
        }
        if (machine.getWaterLevel() == null) {
            machine.setWaterLevel(100);
        }
        if (machine.getMilkLevel() == null) {
            machine.setMilkLevel(100);
        }
        if (machine.getBeansLevel() == null) {
            machine.setBeansLevel(100);
        }
        
        return coffeeMachineRepository.save(machine);
    }

    /**
     * Gets all active machines for a facility.
     *
     * @param facilityId the facility ID
     * @return list of machines in the facility
     */
    @Transactional(readOnly = true)
    public List<CoffeeMachine> getFacilityMachines(Long facilityId) {
        return coffeeMachineRepository.findActiveByFacilityId(facilityId);
    }

    /**
     * Gets facility statistics (number of machines, status counts, etc.).
     *
     * @param facilityId the facility ID
     * @return facility statistics or empty if facility not found
     */
    @Transactional(readOnly = true)
    public Optional<FacilityStatistics> getFacilityStatistics(Long facilityId) {
        return facilityRepository.findActiveById(facilityId)
                .map(facility -> {
                    List<CoffeeMachine> machines = coffeeMachineRepository.findActiveByFacilityId(facilityId);
                    
                    long totalMachines = machines.size();
                    long onlineMachines = machines.stream().filter(m -> m.getStatus() == MachineStatus.ON).count();
                    long offlineMachines = machines.stream().filter(m -> m.getStatus() == MachineStatus.OFF).count();
                    long errorMachines = machines.stream().filter(m -> m.getStatus() == MachineStatus.ERROR).count();
                    
                    return FacilityStatistics.builder()
                            .facilityId(facilityId)
                            .facilityName(facility.getName())
                            .totalMachines((int) totalMachines)
                            .onlineMachines((int) onlineMachines)
                            .offlineMachines((int) offlineMachines)
                            .errorMachines((int) errorMachines)
                            .build();
                });
    }

    /**
     * Checks if a facility exists and is active.
     *
     * @param facilityId the facility ID
     * @return true if facility exists and is active
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long facilityId) {
        return facilityRepository.findActiveById(facilityId).isPresent();
    }

    /**
     * Searches facilities by name (case-insensitive, partial match).
     *
     * @param name the name to search for
     * @return list of matching facilities
     */
    @Transactional(readOnly = true)
    public List<Facility> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return List.of();
        }
        return facilityRepository.findActiveByNameContainingIgnoreCase(name.trim());
    }

    /**
     * Gets facilities by location (case-insensitive, partial match).
     *
     * @param location the location to search for
     * @return list of facilities in the location
     */
    @Transactional(readOnly = true)
    public List<Facility> findByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            return List.of();
        }
        return facilityRepository.findActiveByLocationContainingIgnoreCase(location.trim());
    }

    /**
     * Get the facility ID that owns a specific machine.
     * Used for security checks to ensure users can only access their own facility's machines.
     *
     * @param machineId the machine ID
     * @return facility ID that owns the machine, or null if machine not found
     */
    @Transactional(readOnly = true)
    public Long getMachineOwnerFacilityId(Long machineId) {
        return coffeeMachineRepository.findActiveById(machineId)
                .map(machine -> machine.getFacility().getId())
                .orElse(null);
    }
}

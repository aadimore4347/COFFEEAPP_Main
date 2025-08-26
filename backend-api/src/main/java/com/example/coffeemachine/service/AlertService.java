package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.Alert;
import com.example.coffeemachine.domain.AlertType;
import com.example.coffeemachine.domain.Severity;
import com.example.coffeemachine.repository.AlertRepository;
import com.example.coffeemachine.alert.AlertEvaluatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing alerts and alert queries.
 * Provides methods for alert retrieval, filtering, and management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertEvaluatorService alertEvaluatorService;

    /**
     * Finds an alert by ID (active only).
     *
     * @param alertId the alert ID
     * @return the alert or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<Alert> findById(Long alertId) {
        return alertRepository.findActiveById(alertId);
    }

    /**
     * Gets all unresolved alerts for a specific machine.
     *
     * @param machineId the machine ID
     * @return list of unresolved alerts for the machine
     */
    @Transactional(readOnly = true)
    public List<Alert> getUnresolvedAlertsByMachineId(Long machineId) {
        return alertRepository.findActiveUnresolvedByMachineId(machineId);
    }

    /**
     * Gets all unresolved alerts for machines in a specific facility.
     *
     * @param facilityId the facility ID
     * @return list of unresolved alerts for machines in the facility
     */
    @Transactional(readOnly = true)
    public List<Alert> getUnresolvedAlertsByFacilityId(Long facilityId) {
        return alertRepository.findActiveUnresolvedByFacilityId(facilityId);
    }

    /**
     * Gets alerts by severity level.
     *
     * @param severity the alert severity
     * @return list of alerts with the specified severity
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsBySeverity(Severity severity) {
        return alertRepository.findActiveBySeverity(severity);
    }

    /**
     * Gets alerts by type for a specific machine.
     *
     * @param machineId the machine ID
     * @param alertType the alert type
     * @return list of alerts of the specified type
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsByMachineAndType(Long machineId, AlertType alertType) {
        return alertRepository.findActiveByMachineIdAndType(machineId, alertType);
    }

    /**
     * Gets recent alerts within the specified time period.
     *
     * @param hours number of hours to look back
     * @return list of recent alerts
     */
    @Transactional(readOnly = true)
    public List<Alert> getRecentAlerts(int hours) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(hours);
        return alertRepository.findActiveByCreatedAtBetween(startTime, endTime);
    }

    /**
     * Gets all critical unresolved alerts.
     *
     * @return list of critical unresolved alerts
     */
    @Transactional(readOnly = true)
    public List<Alert> getCriticalAlerts() {
        return alertRepository.findActiveCriticalUnresolved();
    }

    /**
     * Gets critical unresolved alerts for a specific facility.
     *
     * @param facilityId the facility ID
     * @return list of critical unresolved alerts for the facility
     */
    @Transactional(readOnly = true)
    public List<Alert> getCriticalAlertsByFacilityId(Long facilityId) {
        return alertRepository.findActiveCriticalUnresolvedByFacilityId(facilityId);
    }

    /**
     * Gets alert statistics for a facility.
     *
     * @param facilityId the facility ID
     * @return alert statistics
     */
    @Transactional(readOnly = true)
    public AlertStatistics getAlertStatistics(Long facilityId) {
        List<Alert> facilityAlerts = alertRepository.findActiveUnresolvedByFacilityId(facilityId);
        
        long totalAlerts = facilityAlerts.size();
        long unresolvedAlerts = facilityAlerts.stream().filter(a -> !a.getResolved()).count();
        long criticalAlerts = facilityAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.CRITICAL && !a.getResolved())
                .count();
        long warningAlerts = facilityAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.WARNING && !a.getResolved())
                .count();
        long infoAlerts = facilityAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.INFO && !a.getResolved())
                .count();

        return AlertStatistics.builder()
                .facilityId(facilityId)
                .totalAlerts((int) totalAlerts)
                .unresolvedAlerts((int) unresolvedAlerts)
                .criticalAlerts((int) criticalAlerts)
                .warningAlerts((int) warningAlerts)
                .infoAlerts((int) infoAlerts)
                .build();
    }

    /**
     * Gets overall alert statistics across all facilities.
     *
     * @return overall alert statistics
     */
    @Transactional(readOnly = true)
    public AlertStatistics getOverallAlertStatistics() {
        List<Alert> allAlerts = alertRepository.findAllActive();
        
        long totalAlerts = allAlerts.size();
        long unresolvedAlerts = allAlerts.stream().filter(a -> !a.getResolved()).count();
        long criticalAlerts = allAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.CRITICAL && !a.getResolved())
                .count();
        long warningAlerts = allAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.WARNING && !a.getResolved())
                .count();
        long infoAlerts = allAlerts.stream()
                .filter(a -> a.getSeverity() == Severity.INFO && !a.getResolved())
                .count();

        return AlertStatistics.builder()
                .facilityId(null) // Overall statistics
                .totalAlerts((int) totalAlerts)
                .unresolvedAlerts((int) unresolvedAlerts)
                .criticalAlerts((int) criticalAlerts)
                .warningAlerts((int) warningAlerts)
                .infoAlerts((int) infoAlerts)
                .build();
    }

    /**
     * Resolves an alert by ID.
     *
     * @param alertId the alert ID
     * @return true if alert was resolved, false if not found
     */
    @Transactional
    public boolean resolveAlert(Long alertId) {
        log.info("Resolving alert {}", alertId);
        return alertEvaluatorService.resolveAlert(alertId);
    }

    /**
     * Resolves all alerts of a specific type for a machine.
     *
     * @param machineId the machine ID
     * @param alertType the alert type
     * @return number of alerts resolved
     */
    @Transactional
    public int resolveAlertsByTypeAndMachine(Long machineId, AlertType alertType) {
        log.info("Resolving all {} alerts for machine {}", alertType, machineId);
        return alertRepository.resolveAlertsByMachineIdAndType(machineId, alertType);
    }

    /**
     * Gets the count of unresolved alerts by severity.
     *
     * @return alert counts by severity
     */
    @Transactional(readOnly = true)
    public java.util.Map<Severity, Long> getUnresolvedAlertCountsBySeverity() {
        List<Object[]> results = alertRepository.countUnresolvedBySeverity();
        
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Severity) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Gets the count of unresolved alerts by type.
     *
     * @return alert counts by type
     */
    @Transactional(readOnly = true)
    public java.util.Map<AlertType, Long> getUnresolvedAlertCountsByType() {
        List<Object[]> results = alertRepository.countUnresolvedByType();
        
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (AlertType) row[0],
                        row -> (Long) row[1]
                ));
    }

    /**
     * Soft deletes an alert.
     *
     * @param alertId the alert ID
     * @return true if deleted, false if not found
     */
    @Transactional
    public boolean deleteAlert(Long alertId) {
        log.info("Soft deleting alert {}", alertId);
        return alertRepository.softDeleteById(alertId) > 0;
    }

    /**
     * Checks if there are any critical unresolved alerts.
     *
     * @return true if there are critical alerts
     */
    @Transactional(readOnly = true)
    public boolean hasCriticalAlerts() {
        return !alertRepository.findActiveCriticalUnresolved().isEmpty();
    }

    /**
     * Checks if there are any critical unresolved alerts for a facility.
     *
     * @param facilityId the facility ID
     * @return true if there are critical alerts for the facility
     */
    @Transactional(readOnly = true)
    public boolean hasCriticalAlertsForFacility(Long facilityId) {
        return !alertRepository.findActiveCriticalUnresolvedByFacilityId(facilityId).isEmpty();
    }

    /**
     * Gets all unresolved alerts ordered by severity.
     *
     * @return list of unresolved alerts ordered by severity
     */
    @Transactional(readOnly = true)
    public List<Alert> getAllUnresolvedAlerts() {
        return alertRepository.findActiveUnresolvedOrderedBySeverity();
    }
}
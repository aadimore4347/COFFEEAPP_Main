package com.example.coffeemachine.service;

import com.example.coffeemachine.domain.Alert;
import com.example.coffeemachine.domain.AlertType;
import com.example.coffeemachine.domain.Severity;
import com.example.coffeemachine.repository.AlertRepository;
import com.example.coffeemachine.alert.AlertEvaluatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Gets all active alerts for a specific machine.
     *
     * @param machineId the machine ID
     * @return list of active alerts for the machine
     */
    @Transactional(readOnly = true)
    public List<Alert> getActiveAlertsByMachineId(Long machineId) {
        return alertRepository.findActiveUnresolvedByMachineId(machineId);
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
     * Gets all alerts for machines in a specific facility.
     *
     * @param facilityId the facility ID
     * @return list of alerts for machines in the facility
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsByFacilityId(Long facilityId) {
        return alertRepository.findActiveUnresolvedByFacilityId(facilityId);
    }

    /**
     * Gets all unresolved alerts for machines in a specific facility.
     *
     * @param facilityId the facility ID
     * @return list of unresolved alerts for machines in the facility
     */
    @Transactional(readOnly = true)
    public List<Alert> getUnresolvedAlertsByFacilityId(Long facilityId) {
        return alertRepository.findUnresolvedByFacilityId(facilityId);
    }

    /**
     * Gets alerts by severity level.
     *
     * @param severity the alert severity
     * @return list of alerts with the specified severity
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsBySeverity(Severity severity) {
        return alertRepository.findBySeverityAndIsActiveTrue(severity);
    }

    /**
     * Gets alerts by type.
     *
     * @param alertType the alert type
     * @return list of alerts of the specified type
     */
    @Transactional(readOnly = true)
    public List<Alert> getAlertsByType(AlertType alertType) {
        return alertRepository.findByTypeAndIsActiveTrue(alertType);
    }

    /**
     * Gets recent alerts within the specified time period.
     *
     * @param hours number of hours to look back
     * @return list of recent alerts
     */
    @Transactional(readOnly = true)
    public List<Alert> getRecentAlerts(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return alertRepository.findAlertsCreatedAfter(since);
    }

    /**
     * Gets paginated alerts with optional filtering.
     *
     * @param machineId optional machine ID filter
     * @param severity optional severity filter
     * @param resolved optional resolved status filter
     * @param pageable pagination information
     * @return page of alerts
     */
    @Transactional(readOnly = true)
    public Page<Alert> getAlertsPage(Long machineId, Severity severity, Boolean resolved, Pageable pageable) {
        if (machineId != null && severity != null && resolved != null) {
            return alertRepository.findByMachineIdAndSeverityAndResolvedAndIsActiveTrue(
                    machineId, severity, resolved, pageable);
        } else if (machineId != null && severity != null) {
            return alertRepository.findByMachineIdAndSeverityAndIsActiveTrue(
                    machineId, severity, pageable);
        } else if (machineId != null && resolved != null) {
            return alertRepository.findByMachineIdAndResolvedAndIsActiveTrue(
                    machineId, resolved, pageable);
        } else if (severity != null && resolved != null) {
            return alertRepository.findBySeverityAndResolvedAndIsActiveTrue(
                    severity, resolved, pageable);
        } else if (machineId != null) {
            return alertRepository.findByMachineIdAndIsActiveTrue(machineId, pageable);
        } else if (severity != null) {
            return alertRepository.findBySeverityAndIsActiveTrue(severity, pageable);
        } else if (resolved != null) {
            return alertRepository.findByResolvedAndIsActiveTrue(resolved, pageable);
        } else {
            return alertRepository.findByIsActiveTrue(pageable);
        }
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
        List<Alert> unresolvedAlerts = alertRepository.findByResolvedAndIsActiveTrue(false);
        
        return unresolvedAlerts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Alert::getSeverity,
                        java.util.stream.Collectors.counting()
                ));
    }

    /**
     * Gets the count of unresolved alerts by type.
     *
     * @return alert counts by type
     */
    @Transactional(readOnly = true)
    public java.util.Map<AlertType, Long> getUnresolvedAlertCountsByType() {
        List<Alert> unresolvedAlerts = alertRepository.findByResolvedAndIsActiveTrue(false);
        
        return unresolvedAlerts.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Alert::getType,
                        java.util.stream.Collectors.counting()
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
        return !alertRepository.findBySeverityAndResolvedAndIsActiveTrue(Severity.CRITICAL, false).isEmpty();
    }

    /**
     * Checks if there are any critical unresolved alerts for a facility.
     *
     * @param facilityId the facility ID
     * @return true if there are critical alerts for the facility
     */
    @Transactional(readOnly = true)
    public boolean hasCriticalAlertsForFacility(Long facilityId) {
        return alertRepository.findUnresolvedByFacilityId(facilityId).stream()
                .anyMatch(alert -> alert.getSeverity() == Severity.CRITICAL);
    }
}
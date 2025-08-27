package com.example.coffeemachine.alert;

import com.example.coffeemachine.domain.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending alert notifications.
 * Currently implements logging-based notifications but can be extended 
 * to support email, SMS, Slack, or other notification channels.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertNotificationService {

    /**
     * Sends a notification for the given alert.
     * This method is async to avoid blocking the main thread.
     *
     * @param alert the alert to send notification for
     */
    @Async
    public void sendAlertNotification(Alert alert) {
        try {
            // For now, we'll log the alert. In production, this would integrate with:
            // - Email service (SendGrid, AWS SES)
            // - SMS service (Twilio, AWS SNS)
            // - Slack/Teams webhooks
            // - Push notification service
            // - Dashboard websockets for real-time updates
            
            String logLevel = switch (alert.getSeverity()) {
                case CRITICAL -> "ERROR";
                case WARNING -> "WARN";
                case INFO -> "INFO";
            };
            
            String notificationMessage = String.format(
                "[%s] Alert #%d - Machine %d (%s): %s",
                alert.getSeverity(),
                alert.getId(),
                alert.getMachine().getId(),
                alert.getType(),
                alert.getMessage()
            );
            
            switch (logLevel) {
                case "ERROR" -> log.error("🚨 CRITICAL ALERT: {}", notificationMessage);
                case "WARN" -> log.warn("⚠️ WARNING ALERT: {}", notificationMessage);
                default -> log.info("ℹ️ INFO ALERT: {}", notificationMessage);
            }
            
            // TODO: Future implementations:
            // - Send email to facility managers
            // - Send SMS for critical alerts
            // - Post to Slack/Teams channels
            // - Send push notifications to mobile apps
            // - Update real-time dashboard via WebSocket
            
        } catch (Exception e) {
            log.error("Failed to send alert notification for alert {}: {}", alert.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Sends a notification when an alert is resolved.
     *
     * @param alert the resolved alert
     */
    @Async
    public void sendAlertResolvedNotification(Alert alert) {
        try {
            String notificationMessage = String.format(
                "✅ RESOLVED - Alert #%d - Machine %d (%s): %s",
                alert.getId(),
                alert.getMachine().getId(),
                alert.getType(),
                alert.getMessage()
            );
            
            log.info("ALERT RESOLVED: {}", notificationMessage);
            
            // TODO: Send resolution notifications via configured channels
            
        } catch (Exception e) {
            log.error("Failed to send alert resolved notification for alert {}: {}", 
                    alert.getId(), e.getMessage(), e);
        }
    }
    
    /**
     * Sends a batch notification for multiple alerts.
     * Useful for daily/weekly alert summaries.
     *
     * @param alerts the alerts to include in the summary
     */
    @Async
    public void sendAlertSummary(java.util.List<Alert> alerts) {
        if (alerts.isEmpty()) {
            return;
        }
        
        try {
            long criticalCount = alerts.stream().filter(a -> a.getSeverity() == com.example.coffeemachine.domain.Severity.CRITICAL).count();
            long warningCount = alerts.stream().filter(a -> a.getSeverity() == com.example.coffeemachine.domain.Severity.WARNING).count();
            long infoCount = alerts.stream().filter(a -> a.getSeverity() == com.example.coffeemachine.domain.Severity.INFO).count();
            
            String summary = String.format(
                "📊 Alert Summary - Total: %d | Critical: %d | Warning: %d | Info: %d",
                alerts.size(), criticalCount, warningCount, infoCount
            );
            
            log.info("ALERT SUMMARY: {}", summary);
            
            // TODO: Send detailed alert summaries via email or dashboard
            
        } catch (Exception e) {
            log.error("Failed to send alert summary: {}", e.getMessage(), e);
        }
    }
}
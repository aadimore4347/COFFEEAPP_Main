package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.mqtt.dto.MachineStatusUpdate;
import com.example.coffeemachine.mqtt.dto.MachineLevelsUpdate;
import com.example.coffeemachine.mqtt.dto.MachineUsageEvent;
import com.example.coffeemachine.mqtt.dto.MachineAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttMessageHandler {

    private final WebClient webClient;
    private final MqttPayloadParser payloadParser;

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    public void handleTemperatureUpdate(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for temperature update: {}", machineId);
                return;
            }
            
            BigDecimal temperature = payloadParser.parseTemperature(payload);
            if (temperature != null) {
                MachineStatusUpdate update = MachineStatusUpdate.builder()
                    .machineId(Long.valueOf(machineId))
                    .temperature(temperature)
                    .build();
                
                updateMachineStatus(update);
            } else {
                log.warn("Failed to parse temperature from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for temperature update: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling temperature update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleWaterLevelUpdate(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for water level update: {}", machineId);
                return;
            }
            
            Integer waterLevel = payloadParser.parseLevel(payload);
            if (waterLevel != null) {
                MachineLevelsUpdate update = MachineLevelsUpdate.builder()
                    .machineId(Long.valueOf(machineId))
                    .waterLevel(waterLevel)
                    .build();
                
                updateMachineLevels(update);
            } else {
                log.warn("Failed to parse water level from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for water level update: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling water level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleMilkLevelUpdate(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for milk level update: {}", machineId);
                return;
            }
            
            Integer milkLevel = payloadParser.parseLevel(payload);
            if (milkLevel != null) {
                MachineLevelsUpdate update = MachineLevelsUpdate.builder()
                    .machineId(Long.valueOf(machineId))
                    .milkLevel(milkLevel)
                    .build();
                
                updateMachineLevels(update);
            } else {
                log.warn("Failed to parse milk level from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for milk level update: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling milk level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleBeansLevelUpdate(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for beans level update: {}", machineId);
                return;
            }
            
            Integer beansLevel = payloadParser.parseLevel(payload);
            if (beansLevel != null) {
                MachineLevelsUpdate update = MachineLevelsUpdate.builder()
                    .machineId(Long.valueOf(machineId))
                    .beansLevel(beansLevel)
                    .build();
                
                updateMachineLevels(update);
            } else {
                log.warn("Failed to parse beans level from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for beans level update: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling beans level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleStatusUpdate(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for status update: {}", machineId);
                return;
            }
            
            String status = payloadParser.parseStatus(payload);
            if (status != null) {
                MachineStatusUpdate update = MachineStatusUpdate.builder()
                    .machineId(Long.valueOf(machineId))
                    .status(status)
                    .build();
                
                updateMachineStatus(update);
            } else {
                log.warn("Failed to parse status from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for status update: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling status update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleUsageEvent(String machineId, String payload) {
        try {
            if (machineId == null || machineId.trim().isEmpty()) {
                log.warn("Invalid machine ID for usage event: {}", machineId);
                return;
            }
            
            MqttPayloadParser.UsageEvent usageEvent = payloadParser.parseUsageEvent(payload);
            if (usageEvent != null) {
                MachineUsageEvent event = MachineUsageEvent.builder()
                    .machineId(Long.valueOf(machineId))
                    .brewType(usageEvent.brewType())
                    .volumeMl(usageEvent.volumeMl())
                    .tempAtBrew(usageEvent.tempAtBrew())
                    .build();
                
                recordMachineUsage(event);
            } else {
                log.warn("Failed to parse usage event from payload: {}", payload);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid machine ID format for usage event: {}", machineId);
        } catch (Exception e) {
            log.error("Error handling usage event for machine {}: {}", machineId, e.getMessage());
        }
    }

    private void updateMachineStatus(MachineStatusUpdate update) {
        try {
            webClient.post()
                .uri(backendApiBaseUrl + "/api/machine/{id}/status", update.getMachineId())
                .bodyValue(update)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(result -> log.info("Successfully updated status for machine {}", update.getMachineId()))
                .doOnError(error -> log.error("Failed to update status for machine {}: {}", update.getMachineId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Error updating machine status for machine {}: {}", update.getMachineId(), error.getMessage());
                    return Mono.empty();
                })
                .subscribe();
        } catch (Exception e) {
            log.error("Exception while updating machine status for machine {}: {}", update.getMachineId(), e.getMessage());
        }
    }

    private void updateMachineLevels(MachineLevelsUpdate update) {
        try {
            webClient.post()
                .uri(backendApiBaseUrl + "/api/machine/{id}/levels", update.getMachineId())
                .bodyValue(update)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(result -> log.info("Successfully updated levels for machine {}", update.getMachineId()))
                .doOnError(error -> log.error("Failed to update levels for machine {}: {}", update.getMachineId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Error updating machine levels for machine {}: {}", update.getMachineId(), error.getMessage());
                    return Mono.empty();
                })
                .subscribe();
        } catch (Exception e) {
            log.error("Exception while updating machine levels for machine {}: {}", update.getMachineId(), e.getMessage());
        }
    }

    private void recordMachineUsage(MachineUsageEvent event) {
        try {
            webClient.post()
                .uri(backendApiBaseUrl + "/api/machine/{id}/history", event.getMachineId())
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(result -> log.info("Successfully recorded usage for machine {}", event.getMachineId()))
                .doOnError(error -> log.error("Failed to record usage for machine {}: {}", event.getMachineId(), error.getMessage()))
                .onErrorResume(error -> {
                    log.error("Error recording machine usage for machine {}: {}", event.getMachineId(), error.getMessage());
                    return Mono.empty();
                })
                .subscribe();
        } catch (Exception e) {
            log.error("Exception while recording machine usage for machine {}: {}", event.getMachineId(), e.getMessage());
        }
    }
}
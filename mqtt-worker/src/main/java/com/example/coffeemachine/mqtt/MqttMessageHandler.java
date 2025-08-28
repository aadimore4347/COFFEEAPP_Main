package com.example.coffeemachine.mqtt;

import com.example.coffeemachine.mqtt.dto.MachineStatusUpdate;
import com.example.coffeemachine.mqtt.dto.MachineLevelsUpdate;
import com.example.coffeemachine.mqtt.dto.MachineUsageEvent;
import com.example.coffeemachine.mqtt.dto.MachineAlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class MqttMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MqttMessageHandler.class);

    private final WebClient webClient;
    private final MqttPayloadParser payloadParser;

    public MqttMessageHandler(WebClient webClient, MqttPayloadParser payloadParser) {
        this.webClient = webClient;
        this.payloadParser = payloadParser;
    }

    @Value("${backend.api.base-url}")
    private String backendApiBaseUrl;

    public void handleTemperatureUpdate(String machineId, String payload) {
        try {
            BigDecimal temperature = payloadParser.parseTemperature(payload);
            if (temperature != null) {
                MachineStatusUpdate update = new MachineStatusUpdate();
                update.setMachineId(Long.valueOf(machineId));
                update.setTemperature(temperature);
                
                updateMachineStatus(update);
            }
        } catch (Exception e) {
            log.error("Error handling temperature update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleWaterLevelUpdate(String machineId, String payload) {
        try {
            Integer waterLevel = payloadParser.parseLevel(payload);
            if (waterLevel != null) {
                MachineLevelsUpdate update = new MachineLevelsUpdate();
                update.setMachineId(Long.valueOf(machineId));
                update.setWaterLevel(waterLevel);
                
                updateMachineLevels(update);
            }
        } catch (Exception e) {
            log.error("Error handling water level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleMilkLevelUpdate(String machineId, String payload) {
        try {
            Integer milkLevel = payloadParser.parseLevel(payload);
            if (milkLevel != null) {
                MachineLevelsUpdate update = new MachineLevelsUpdate();
                update.setMachineId(Long.valueOf(machineId));
                update.setMilkLevel(milkLevel);
                
                updateMachineLevels(update);
            }
        } catch (Exception e) {
            log.error("Error handling milk level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleBeansLevelUpdate(String machineId, String payload) {
        try {
            Integer beansLevel = payloadParser.parseLevel(payload);
            if (beansLevel != null) {
                MachineLevelsUpdate update = new MachineLevelsUpdate();
                update.setMachineId(Long.valueOf(machineId));
                update.setBeansLevel(beansLevel);
                
                updateMachineLevels(update);
            }
        } catch (Exception e) {
            log.error("Error handling beans level update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleStatusUpdate(String machineId, String payload) {
        try {
            String status = payloadParser.parseStatus(payload);
            if (status != null) {
                MachineStatusUpdate update = new MachineStatusUpdate();
                update.setMachineId(Long.valueOf(machineId));
                update.setStatus(status);
                
                updateMachineStatus(update);
            }
        } catch (Exception e) {
            log.error("Error handling status update for machine {}: {}", machineId, e.getMessage());
        }
    }

    public void handleUsageEvent(String machineId, String payload) {
        try {
            MqttPayloadParser.UsageEvent usageEvent = payloadParser.parseUsageEvent(payload);
            if (usageEvent != null) {
                MachineUsageEvent event = new MachineUsageEvent();
                event.setMachineId(Long.valueOf(machineId));
                event.setBrewType(usageEvent.brewType());
                event.setVolumeMl(usageEvent.volumeMl());
                event.setTempAtBrew(usageEvent.tempAtBrew());
                
                recordMachineUsage(event);
            }
        } catch (Exception e) {
            log.error("Error handling usage event for machine {}: {}", machineId, e.getMessage());
        }
    }

    private void updateMachineStatus(MachineStatusUpdate update) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/machine/{id}/status", update.getMachineId())
            .bodyValue(update)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(result -> log.info("Successfully updated status for machine {}", update.getMachineId()))
            .doOnError(error -> log.error("Failed to update status for machine {}: {}", update.getMachineId(), error.getMessage()))
            .subscribe();
    }

    private void updateMachineLevels(MachineLevelsUpdate update) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/machine/{id}/levels", update.getMachineId())
            .bodyValue(update)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(result -> log.info("Successfully updated levels for machine {}", update.getMachineId()))
            .doOnError(error -> log.error("Failed to update levels for machine {}: {}", update.getMachineId(), error.getMessage()))
            .subscribe();
    }

    private void recordMachineUsage(MachineUsageEvent event) {
        webClient.post()
            .uri(backendApiBaseUrl + "/api/machine/{id}/history", event.getMachineId())
            .bodyValue(event)
            .retrieve()
            .bodyToMono(Void.class)
            .doOnSuccess(result -> log.info("Successfully recorded usage for machine {}", event.getMachineId()))
            .doOnError(error -> log.error("Failed to record usage for machine {}: {}", event.getMachineId(), error.getMessage()))
            .subscribe();
    }
}
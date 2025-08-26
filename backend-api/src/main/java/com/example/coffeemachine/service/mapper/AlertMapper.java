package com.example.coffeemachine.service.mapper;

import com.example.coffeemachine.domain.Alert;
import com.example.coffeemachine.service.dto.AlertDto;
import com.example.coffeemachine.service.dto.AlertStatisticsDto;
import com.example.coffeemachine.service.AlertStatistics;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Alert} and its DTO {@link AlertDto}.
 */
@Mapper(componentModel = "spring")
public interface AlertMapper {

    /**
     * Map Alert entity to AlertDto.
     *
     * @param alert the Alert entity
     * @return the AlertDto
     */
    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "machineFacilityName", source = "machine.facility.name")
    @Mapping(target = "threshold", source = "thresholdValue")
    AlertDto toDto(Alert alert);

    /**
     * Map AlertStatistics to AlertStatisticsDto.
     *
     * @param alertStatistics the AlertStatistics
     * @return the AlertStatisticsDto
     */
    AlertStatisticsDto toDto(AlertStatistics alertStatistics);

    /**
     * Map a list of Alert entities to a list of AlertDtos.
     *
     * @param alerts the list of Alert entities
     * @return the list of AlertDtos
     */
    java.util.List<AlertDto> toDto(java.util.List<Alert> alerts);

    /**
     * Map a list of AlertStatistics to a list of AlertStatisticsDtos.
     *
     * @param alertStatisticsList the list of AlertStatistics
     * @return the list of AlertStatisticsDtos
     */
    java.util.List<AlertStatisticsDto> statisticsToDto(java.util.List<AlertStatistics> alertStatisticsList);
}
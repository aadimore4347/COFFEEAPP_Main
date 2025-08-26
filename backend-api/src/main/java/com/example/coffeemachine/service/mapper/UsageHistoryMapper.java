package com.example.coffeemachine.service.mapper;

import com.example.coffeemachine.domain.UsageHistory;
import com.example.coffeemachine.service.dto.UsageHistoryDto;
import com.example.coffeemachine.service.dto.UsageStatisticsDto;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UsageHistory} and its DTO {@link UsageHistoryDto}.
 */
@Mapper(componentModel = "spring")
public interface UsageHistoryMapper {

    /**
     * Map UsageHistory entity to UsageHistoryDto.
     *
     * @param usageHistory the UsageHistory entity
     * @return the UsageHistoryDto
     */
    @Mapping(target = "machineId", source = "machine.id")
    UsageHistoryDto toDto(UsageHistory usageHistory);

    /**
     * Map a list of UsageHistory entities to a list of UsageHistoryDtos.
     *
     * @param usageHistories the list of UsageHistory entities
     * @return the list of UsageHistoryDtos
     */
    java.util.List<UsageHistoryDto> toDto(java.util.List<UsageHistory> usageHistories);
}
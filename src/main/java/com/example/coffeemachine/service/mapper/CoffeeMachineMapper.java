package com.example.coffeemachine.service.mapper;

import com.example.coffeemachine.domain.CoffeeMachine;
import com.example.coffeemachine.service.dto.CoffeeMachineDto;
import com.example.coffeemachine.service.dto.CreateMachineRequest;
import com.example.coffeemachine.service.dto.UpdateMachineRequest;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CoffeeMachine} and its DTO {@link CoffeeMachineDto}.
 */
@Mapper(componentModel = "spring", uses = {UsageHistoryMapper.class, AlertMapper.class})
public interface CoffeeMachineMapper {

    /**
     * Map CoffeeMachine entity to CoffeeMachineDto.
     *
     * @param coffeeMachine the CoffeeMachine entity
     * @return the CoffeeMachineDto
     */
    @Mapping(target = "facilityId", source = "facility.id")
    @Mapping(target = "facilityName", source = "facility.name")
    @Mapping(target = "recentUsage", ignore = true) // Mapped separately when needed
    @Mapping(target = "activeAlerts", ignore = true) // Mapped separately when needed
    CoffeeMachineDto toDto(CoffeeMachine coffeeMachine);



    /**
     * Map CreateMachineRequest to CoffeeMachine entity.
     *
     * @param createMachineRequest the create request
     * @return the CoffeeMachine entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "usageHistory", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    CoffeeMachine toEntity(CreateMachineRequest createMachineRequest);

    /**
     * Map UpdateMachineRequest to CoffeeMachine entity.
     *
     * @param updateMachineRequest the update request
     * @return the CoffeeMachine entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "usageHistory", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    CoffeeMachine toEntity(UpdateMachineRequest updateMachineRequest);

    /**
     * Partially update a CoffeeMachine entity from UpdateMachineRequest.
     * Only updates non-null fields from the request.
     *
     * @param updateMachineRequest the update request
     * @param coffeeMachine the existing CoffeeMachine entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "usageHistory", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void partialUpdate(UpdateMachineRequest updateMachineRequest, @MappingTarget CoffeeMachine coffeeMachine);

    /**
     * Map a list of CoffeeMachine entities to a list of CoffeeMachineDtos.
     *
     * @param coffeeMachines the list of CoffeeMachine entities
     * @return the list of CoffeeMachineDtos
     */
    java.util.List<CoffeeMachineDto> toDto(java.util.List<CoffeeMachine> coffeeMachines);
}
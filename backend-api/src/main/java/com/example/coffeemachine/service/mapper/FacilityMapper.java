package com.example.coffeemachine.service.mapper;

import com.example.coffeemachine.domain.Facility;
import com.example.coffeemachine.service.dto.FacilityDto;
import com.example.coffeemachine.service.dto.CreateFacilityRequest;
import com.example.coffeemachine.service.dto.UpdateFacilityRequest;
import com.example.coffeemachine.service.dto.FacilityStatisticsDto;
import com.example.coffeemachine.service.FacilityStatistics;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Facility} and its DTO {@link FacilityDto}.
 */
@Mapper(componentModel = "spring", uses = {CoffeeMachineMapper.class})
public interface FacilityMapper {

    /**
     * Map Facility entity to FacilityDto.
     *
     * @param facility the Facility entity
     * @return the FacilityDto
     */
    @Mapping(target = "coffeeMachines", ignore = true) // Mapped separately when needed
    FacilityDto toDto(Facility facility);



    /**
     * Map CreateFacilityRequest to Facility entity.
     *
     * @param createFacilityRequest the create request
     * @return the Facility entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coffeeMachines", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Facility toEntity(CreateFacilityRequest createFacilityRequest);

    /**
     * Map UpdateFacilityRequest to Facility entity.
     *
     * @param updateFacilityRequest the update request
     * @return the Facility entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coffeeMachines", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Facility toEntity(UpdateFacilityRequest updateFacilityRequest);

    /**
     * Partially update a Facility entity from UpdateFacilityRequest.
     * Only updates non-null fields from the request.
     *
     * @param updateFacilityRequest the update request
     * @param facility the existing Facility entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "coffeeMachines", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void partialUpdate(UpdateFacilityRequest updateFacilityRequest, @MappingTarget Facility facility);

    /**
     * Map FacilityStatistics to FacilityStatisticsDto.
     *
     * @param facilityStatistics the FacilityStatistics
     * @return the FacilityStatisticsDto
     */
    FacilityStatisticsDto toDto(FacilityStatistics facilityStatistics);

    /**
     * Map a list of Facility entities to a list of FacilityDtos.
     *
     * @param facilities the list of Facility entities
     * @return the list of FacilityDtos
     */
    java.util.List<FacilityDto> toDto(java.util.List<Facility> facilities);

    /**
     * Map a list of FacilityStatistics to a list of FacilityStatisticsDtos.
     *
     * @param facilityStatisticsList the list of FacilityStatistics
     * @return the list of FacilityStatisticsDtos
     */
    java.util.List<FacilityStatisticsDto> statisticsToDto(java.util.List<FacilityStatistics> facilityStatisticsList);
}
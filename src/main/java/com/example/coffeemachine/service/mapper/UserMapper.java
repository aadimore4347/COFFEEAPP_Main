package com.example.coffeemachine.service.mapper;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.service.dto.UserDto;
import com.example.coffeemachine.service.dto.CreateUserRequest;
import com.example.coffeemachine.service.dto.UpdateUserRequest;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDto}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Map User entity to UserDto.
     * Excludes sensitive data like password hash.
     *
     * @param user the User entity
     * @return the UserDto
     */
    @Mapping(target = "facility", source = "facility")
    UserDto toDto(User user);
    
    /**
     * Map Facility to simple FacilityDto for user context.
     *
     * @param facility the Facility entity
     * @return the FacilityDto
     */
    @Mapping(target = "coffeeMachines", ignore = true)
    com.example.coffeemachine.service.dto.FacilityDto facilityToDto(com.example.coffeemachine.domain.Facility facility);

    /**
     * Map CreateUserRequest to User entity.
     * Note: Password must be set separately after encryption.
     *
     * @param createUserRequest the create request
     * @return the User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    User toEntity(CreateUserRequest createUserRequest);

    /**
     * Map UpdateUserRequest to User entity for partial updates.
     *
     * @param updateUserRequest the update request
     * @return the User entity with updated fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    User toEntity(UpdateUserRequest updateUserRequest);

    /**
     * Partially update a User entity from UpdateUserRequest.
     * Only updates non-null fields from the request.
     *
     * @param updateUserRequest the update request
     * @param user the existing User entity to update
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "facility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void partialUpdate(UpdateUserRequest updateUserRequest, @MappingTarget User user);

    /**
     * Map a list of User entities to a list of UserDtos.
     *
     * @param users the list of User entities
     * @return the list of UserDtos
     */
    java.util.List<UserDto> toDto(java.util.List<User> users);
}
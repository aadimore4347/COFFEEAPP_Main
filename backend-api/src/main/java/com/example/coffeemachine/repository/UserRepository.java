package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true ORDER BY u.username")
    List<User> findActiveByRole(@Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.facility.id = :facilityId AND u.role = 'FACILITY' AND u.isActive = true ORDER BY u.username")
    List<User> findActiveFacilityUsersByFacilityId(@Param("facilityId") Long facilityId);
}
package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.User;
import com.example.coffeemachine.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("select u from User u where u.role = :role")
    List<User> findByRole(UserRole role);
}
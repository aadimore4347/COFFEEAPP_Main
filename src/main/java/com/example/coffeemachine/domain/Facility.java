package com.example.coffeemachine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a facility (building/location) that contains coffee machines.
 * 
 * Each facility can have multiple coffee machines and multiple facility users.
 * Provides location-based organization for the coffee machine monitoring system.
 */
@Entity
@Table(name = "facility", 
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"coffeeMachines", "users"})
public class Facility extends BaseEntity {

    /**
     * Unique name identifier for the facility.
     * Must be unique across all facilities.
     */
    @NotBlank(message = "Facility name is required")
    @Size(min = 2, max = 100, message = "Facility name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Physical location/address of the facility.
     */
    @NotBlank(message = "Facility location is required")
    @Size(min = 5, max = 255, message = "Facility location must be between 5 and 255 characters")
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    /**
     * Coffee machines located at this facility.
     * Bi-directional relationship with CoffeeMachine.
     */
    @OneToMany(mappedBy = "facility", 
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY,
               orphanRemoval = true)
    private List<CoffeeMachine> coffeeMachines = new ArrayList<>();

    /**
     * Users assigned to this facility.
     * Bi-directional relationship with User.
     */
    @OneToMany(mappedBy = "facility", 
               cascade = CascadeType.ALL, 
               fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    /**
     * Constructor for creating a new facility.
     */
    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Add a coffee machine to this facility.
     * Maintains bi-directional relationship.
     */
    public void addCoffeeMachine(CoffeeMachine machine) {
        if (machine != null) {
            coffeeMachines.add(machine);
            machine.setFacility(this);
        }
    }

    /**
     * Remove a coffee machine from this facility.
     * Maintains bi-directional relationship.
     */
    public void removeCoffeeMachine(CoffeeMachine machine) {
        if (machine != null) {
            coffeeMachines.remove(machine);
            machine.setFacility(null);
        }
    }

    /**
     * Add a user to this facility.
     * Maintains bi-directional relationship.
     */
    public void addUser(User user) {
        if (user != null && user.getRole() == UserRole.FACILITY) {
            users.add(user);
            user.setFacility(this);
        }
    }

    /**
     * Remove a user from this facility.
     * Maintains bi-directional relationship.
     */
    public void removeUser(User user) {
        if (user != null) {
            users.remove(user);
            user.setFacility(null);
        }
    }

    /**
     * Get count of active coffee machines in this facility.
     */
    public long getActiveMachineCount() {
        return coffeeMachines.stream()
                .filter(BaseEntity::isActive)
                .count();
    }

    /**
     * Get count of operational (ON status) machines in this facility.
     */
    public long getOperationalMachineCount() {
        return coffeeMachines.stream()
                .filter(machine -> machine.isActive() && machine.getStatus() == MachineStatus.ON)
                .count();
    }
}
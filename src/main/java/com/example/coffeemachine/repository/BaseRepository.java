package com.example.coffeemachine.repository;

import com.example.coffeemachine.domain.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface providing common operations for entities extending BaseEntity.
 * 
 * Provides soft-delete functionality and common query patterns for all entities.
 * The @NoRepositoryBean annotation prevents Spring from creating an implementation of this interface.
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    /**
     * Find all active entities (where isActive = true).
     * 
     * @return List of active entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = true")
    List<T> findAllActive();

    /**
     * Find an active entity by ID.
     * 
     * @param id the entity ID
     * @return Optional containing the entity if found and active
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.isActive = true")
    Optional<T> findActiveById(@Param("id") Long id);

    /**
     * Check if an active entity exists by ID.
     * 
     * @param id the entity ID
     * @return true if an active entity with the given ID exists
     */
    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.isActive = true")
    boolean existsActiveById(@Param("id") Long id);

    /**
     * Count all active entities.
     * 
     * @return number of active entities
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.isActive = true")
    long countActive();

    /**
     * Soft delete an entity by ID (set isActive = false).
     * 
     * @param id the entity ID to soft delete
     * @return number of entities updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = false, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    int softDeleteById(@Param("id") Long id);

    /**
     * Restore a soft-deleted entity by ID (set isActive = true).
     * 
     * @param id the entity ID to restore
     * @return number of entities updated (should be 1 if successful)
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.isActive = true, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
    int restoreById(@Param("id") Long id);

    /**
     * Find all soft-deleted entities (where isActive = false).
     * 
     * @return List of soft-deleted entities
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.isActive = false")
    List<T> findAllDeleted();

    /**
     * Permanently delete all soft-deleted entities.
     * This is a hard delete and should be used with caution.
     * 
     * @return number of entities permanently deleted
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.isActive = false")
    int permanentlyDeleteAllSoftDeleted();
}
package com.backend.backendpreu.courses.Repository;

import com.backend.backendpreu.courses.model.SchoolTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link SchoolTerm} entities.
 * Provides methods for database operations on school terms.
 */
@Repository
public interface SchoolTermRepository extends JpaRepository<SchoolTerm, Long> {

    /**
     * Finds all active school terms.
     * Returns a list to handle multiple active terms if they exist.
     *
     * @return A list of active {@link SchoolTerm} entities.
     */
    List<SchoolTerm> findByActiveTrue();

    /**
     * Finds a school term by its name.
     *
     * @param name The name of the school term (e.g., "2025-1").
     * @return An {@link Optional} containing the found {@link SchoolTerm}, or empty if not found.
     */
    Optional<SchoolTerm> findByName(String name);

    /**
     * Checks if a school term with the given name already exists.
     * Used for quick duplicate validation before saving.
     *
     * @param name The name to check for existence.
     * @return true if a school term with the name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Retrieves all school terms, ordered chronologically by start date in descending order (newest first).
     *
     * @return A list of all {@link SchoolTerm} entities, sorted by start date.
     */
    List<SchoolTerm> findAllByOrderByStartDateDesc();

    /**
     * Checks if a school term with the given name exists, excluding the term with the specified ID.
     * Useful for validating uniqueness during updates.
     *
     * @param name The name to check for existence.
     * @param id The ID of the school term to exclude from the search.
     * @return true if another school term with the name exists, false otherwise.
     */
    boolean existsByNameAndIdNot(String name, Long id);
}

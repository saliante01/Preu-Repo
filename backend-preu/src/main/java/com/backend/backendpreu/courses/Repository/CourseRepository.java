package com.backend.backendpreu.courses.Repository;

import com.backend.backendpreu.courses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Course} entities.
 * Provides methods for database operations on courses.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its unique code.
     *
     * @param code The code of the course to find.
     * @return An {@link Optional} containing the found {@link Course}, or empty if not found.
     */
    Optional<Course> findByCode(String code);

    /**
     * Finds a list of courses whose names contain the given string, case-insensitive.
     *
     * @param name The substring to search for in course names.
     * @return A list of {@link Course} entities matching the name.
     */
    List<Course> findByNameContainingIgnoreCase(String name);

    /**
     * Checks if a course with the given code already exists.
     *
     * @param code The code to check for existence.
     * @return true if a course with the code exists, false otherwise.
     */
    boolean existsByCode(String code);

    /**
     * Checks if a course with the given name already exists.
     *
     * @param name The name to check for existence.
     * @return true if a course with the name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Checks if a course with the given code exists, excluding the course with the specified ID.
     * Useful for validating uniqueness during updates.
     *
     * @param code The code to check for existence.
     * @param id The ID of the course to exclude from the search.
     * @return true if another course with the code exists, false otherwise.
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * Checks if a course with the given name exists, excluding the course with the specified ID.
     * Useful for validating uniqueness during updates.
     *
     * @param name The name to check for existence.
     * @param id The ID of the course to exclude from the search.
     * @return true if another course with the name exists, false otherwise.
     */
    boolean existsByNameAndIdNot(String name, Long id);
}

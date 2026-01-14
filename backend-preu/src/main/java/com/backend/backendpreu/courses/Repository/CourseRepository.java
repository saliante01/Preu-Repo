package com.backend.backendpreu.courses.Repository;

import com.backend.backendpreu.courses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);
    List<Course> findByNameContainingIgnoreCase(String name);
    boolean existsByCode(String code);

}
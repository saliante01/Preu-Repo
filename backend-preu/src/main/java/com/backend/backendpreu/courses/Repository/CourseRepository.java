package com.backend.backendpreu.courses.Repository;

import com.backend.backendpreu.courses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Buscar por código interno (ej: "MAT-101")
    Optional<Course> findByCode(String code);

    // Búsqueda por nombre (ej: buscar "Matem" -> trae Matemáticas)
    // IgnoreCase hace que no importen mayúsculas/minúsculas
    List<Course> findByNameContainingIgnoreCase(String name);

}
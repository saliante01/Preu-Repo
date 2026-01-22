package com.backend.backendpreu.courses.controller;

import com.backend.backendpreu.courses.model.SchoolTerm;
import com.backend.backendpreu.courses.Repository.SchoolTermRepository;
import com.backend.backendpreu.courses.dto.SchoolTermRequestDTO;
import com.backend.backendpreu.courses.service.SchoolTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/terms")
@RequiredArgsConstructor
public class SchoolTermController {

    private final SchoolTermService schoolTermService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SchoolTerm>> getAllTerms() {
        return ResponseEntity.ok(schoolTermService.getAllTerms());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolTerm> createTerm(
            @RequestBody SchoolTermRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(schoolTermService.createTerm(request, authentication.getName()));
    }
    // Modificar Semestre
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolTerm> updateTerm(
            @PathVariable Long id,
            @RequestBody SchoolTermRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(schoolTermService.updateTerm(id, request, authentication.getName()));
    }

    // Eliminar Semestre
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTerm(
            @PathVariable Long id,
            Authentication authentication
    ) {
        schoolTermService.deleteTerm(id, authentication.getName());
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}

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

/**
 * REST controller for managing school terms.
 * Provides endpoints for administrative operations on academic semesters.
 */
@RestController
@RequestMapping("/api/admin/terms")
@RequiredArgsConstructor
public class SchoolTermController {

    private final SchoolTermService schoolTermService;

    /**
     * Retrieves all school terms, ordered by start date in descending order.
     * Accessible only by users with 'ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of {@link SchoolTerm} objects.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SchoolTerm>> getAllTerms() {
        return ResponseEntity.ok(schoolTermService.getAllTerms());
    }

    /**
     * Creates a new school term.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param request The {@link SchoolTermRequestDTO} containing the details of the new school term.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} containing the newly created {@link SchoolTerm}.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolTerm> createTerm(
            @RequestBody SchoolTermRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(schoolTermService.createTerm(request, authentication.getName()));
    }

    /**
     * Updates an existing school term identified by its ID.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param id The ID of the school term to update.
     * @param request The {@link SchoolTermRequestDTO} containing the updated details.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} containing the updated {@link SchoolTerm}.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchoolTerm> updateTerm(
            @PathVariable Long id,
            @RequestBody SchoolTermRequestDTO request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(schoolTermService.updateTerm(id, request, authentication.getName()));
    }

    /**
     * Deletes a school term identified by its ID.
     * Accessible only by users with 'ADMIN' role.
     *
     * @param id The ID of the school term to delete.
     * @param authentication The Spring Security {@link Authentication} object of the current user.
     * @return A {@link ResponseEntity} with no content if the deletion is successful.
     */
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

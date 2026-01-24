package com.backend.backendpreu.courses.service;
import com.backend.backendpreu.audit.service.AuditLogService;
import com.backend.backendpreu.courses.model.SchoolTerm;
import com.backend.backendpreu.courses.dto.SchoolTermRequestDTO;
import com.backend.backendpreu.courses.Repository.SchoolTermRepository;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service class for managing school terms.
 * Provides business logic for CRUD operations on SchoolTerm entities,
 * including validations, and audit logging.
 */
@Service
@RequiredArgsConstructor
public class SchoolTermService {

    private final SchoolTermRepository schoolTermRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all school terms, ordered by start date in descending order.
     *
     * @return A list of all {@link SchoolTerm} objects.
     */
    public List<SchoolTerm> getAllTerms() {
        return schoolTermRepository.findAllByOrderByStartDateDesc();
    }

    /**
     * Creates a new school term based on the provided request DTO.
     * Performs validations for unique name and valid dates, and logs the action.
     *
     * @param request The {@link SchoolTermRequestDTO} containing the details of the new school term.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @return The newly created {@link SchoolTerm} object.
     * @throws ResponseStatusException if a term with the same name already exists, or if the end date is before the start date.
     */
    @Transactional
    public SchoolTerm createTerm(SchoolTermRequestDTO request, String adminEmail) {

        if (schoolTermRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A school term with name: " + request.getName() + " already exists.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date.");
        }

        SchoolTerm term = new SchoolTerm();
        term.setName(request.getName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());
        term.setActive(true);

        SchoolTerm savedTerm = schoolTermRepository.save(term);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        auditLogService.log(
                admin,
                "CREATE_TERM",
                "SCHOOL_TERM",
                savedTerm.getId(),
                "Created school term: " + savedTerm.getName()
        );

        return savedTerm;
    }

    /**
     * Updates an existing school term identified by its ID.
     * Performs validations for unique name (if changed) and valid dates, and logs the action.
     *
     * @param id The ID of the school term to update.
     * @param request The {@link SchoolTermRequestDTO} containing the updated details.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @return The updated {@link SchoolTerm} object.
     * @throws ResponseStatusException if the school term is not found, or if another term with the updated name already exists, or if dates are invalid.
     */
    @Transactional
    public SchoolTerm updateTerm(Long id, SchoolTermRequestDTO request, String adminEmail) {
        SchoolTerm term = schoolTermRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School term not found"));

        if (schoolTermRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Another school term with name: " + request.getName() + " already exists.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date.");
        }

        term.setName(request.getName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());

        SchoolTerm updatedTerm = schoolTermRepository.save(term);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        auditLogService.log(admin, "UPDATE_TERM", "SCHOOL_TERM", id, "Updated school term: " + term.getName());

        return updatedTerm;
    }

    /**
     * Deletes a school term identified by its ID.
     * Catches and handles {@link org.springframework.dao.DataIntegrityViolationException}
     * if the term has associated courses or history (due to foreign key constraints).
     * Logs the deletion action.
     *
     * @param id The ID of the school term to delete.
     * @param adminEmail The email of the administrator performing the action for auditing.
     * @throws ResponseStatusException if the school term is not found, or if it cannot be deleted due to associated data.
     */
    @Transactional
    public void deleteTerm(Long id, String adminEmail) {
        SchoolTerm term = schoolTermRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School term not found"));

        try {
            schoolTermRepository.delete(term);

            // Audit
            User admin = userRepository.findByEmail(adminEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
            auditLogService.log(admin, "DELETE_TERM", "SCHOOL_TERM", id, "Deleted school term " + term.getName());

        } catch (Exception e) {
            // Catch foreign key constraint violation (Referential Integrity)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete school term because it has associated courses or history.");
        }
    }
}

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

@Service
@RequiredArgsConstructor
public class SchoolTermService {

    private final SchoolTermRepository schoolTermRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public List<SchoolTerm> getAllTerms() {
        return schoolTermRepository.findAllByOrderByStartDateDesc();
    }

    @Transactional
    public SchoolTerm createTerm(SchoolTermRequestDTO request, String adminEmail) {

        if (schoolTermRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un semestre con el nombre: " + request.getName());
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la de inicio.");
        }

        SchoolTerm term = new SchoolTerm();
        term.setName(request.getName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());
        term.setActive(true);

        SchoolTerm savedTerm = schoolTermRepository.save(term);

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin no encontrado"));

        auditLogService.log(
                admin,
                "CREATE_TERM",
                "SCHOOL_TERM",
                savedTerm.getId(),
                "Creó semestre: " + savedTerm.getName()
        );

        return savedTerm;
    }

    @Transactional
    public SchoolTerm updateTerm(Long id, SchoolTermRequestDTO request, String adminEmail) {
        SchoolTerm term = schoolTermRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semestre no encontrado"));

        if (schoolTermRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe otro semestre con el nombre: " + request.getName());
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fecha fin no puede ser anterior a inicio");
        }

        term.setName(request.getName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());

        SchoolTerm updatedTerm = schoolTermRepository.save(term);

        User admin = userRepository.findByEmail(adminEmail).orElseThrow();
        auditLogService.log(admin, "UPDATE_TERM", "SCHOOL_TERM", id, "Actualizó semestre: " + term.getName());

        return updatedTerm;
    }

    @Transactional
    public void deleteTerm(Long id, String adminEmail) {
        SchoolTerm term = schoolTermRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semestre no encontrado"));

        try {
            schoolTermRepository.delete(term);

            // Auditoría
            User admin = userRepository.findByEmail(adminEmail).orElseThrow();
            auditLogService.log(admin, "DELETE_TERM", "SCHOOL_TERM", id, "Eliminó el semestre " + term.getName());

        } catch (Exception e) {
            // Capturamos el error de llave foránea (Integridad Referencial)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede eliminar el semestre porque ya tiene cursos o historial asociado.");
        }
    }
}

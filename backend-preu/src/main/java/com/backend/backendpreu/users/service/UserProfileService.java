package com.backend.backendpreu.users.service;

import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.users.dto.AcademicHistoryDTO;
import com.backend.backendpreu.users.dto.UserProfileDTO;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final CourseParticipationRepository participationRepository;

    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long userId) {
        // 1. Buscar al usuario (Lanza error si no existe)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Buscar su historial usando la Query optimizada del Repository
        // Nota: Asume que arreglamos el repositorio en el paso anterior
        var historyEntities = participationRepository.findFullHistoryByUserId(userId);

        // 3. Convertir Entidades -> DTOs (Mapping)
        List<AcademicHistoryDTO> historyDtoList = historyEntities.stream()
                .map(p -> AcademicHistoryDTO.builder()
                        .courseName(p.getAcademicPeriod().getCourse().getName())
                        .termName(p.getAcademicPeriod().getSchoolTerm().getName()) // Ojo aquí con la relación nueva
                        .role(p.getRole().name())
                        .status(p.getStatus().name())
                        .startDate(p.getAcademicPeriod().getSchoolTerm().getStartDate())
                        .build())
                .collect(Collectors.toList());

        // 4. Armar el perfil final
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFirstName() + " " + user.getLastName()) // Ajusta según tus campos de User
                .academicHistory(historyDtoList)
                .build();
    }
}

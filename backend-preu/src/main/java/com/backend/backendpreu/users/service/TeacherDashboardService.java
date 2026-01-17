package com.backend.backendpreu.users.service;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.ClassSchedule;
import com.backend.backendpreu.users.dto.AvailableTeacherDTO;
import com.backend.backendpreu.users.dto.TeacherDashboardDTO;
import com.backend.backendpreu.users.model.Role;
import com.backend.backendpreu.users.model.User;
import com.backend.backendpreu.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private final UserRepository userRepository;
    private final CourseParticipationRepository participationRepository;

    @Transactional(readOnly = true)
    public List<TeacherDashboardDTO> getTeachersDashboard() {
        // 1. Traer todos los usuarios que sean PROFESSOR
        List<User> teachers = userRepository.findByRole(Role.PROFESSOR); // Asegúrate de tener este método en UserRepository

        return teachers.stream().map(this::buildTeacherStats).collect(Collectors.toList());
    }

    private TeacherDashboardDTO buildTeacherStats(User teacher) {
        // 2. Buscar cursos ACTIVOS donde este usuario sea MAIN_PROFESSOR
        // (Nota: Debes crear este método en tu ParticipationRepository si no existe, o usar uno genérico)
        List<CourseParticipation> activeParticipations = participationRepository
                .findAllByUserIdAndRoleAndStatus(teacher.getId(), CourseRole.MAIN_PROFESSOR, ParticipationStatus.ACTIVE);

        long totalHours = 0;
        List<TeacherDashboardDTO.AssignedCourseInfo> courseInfos = new ArrayList<>();

        for (CourseParticipation p : activeParticipations) {
            AcademicPeriod period = p.getAcademicPeriod();
            ClassSchedule schedule = period.getSchedule();

            // Calculamos info del horario
            String scheduleStr = "Sin Horario";
            long duration = 0;

            if (schedule != null) {
                scheduleStr = schedule.getDayOfWeek() + " " + schedule.getStartTime() + " - " + schedule.getEndTime();
                duration = schedule.getDurationInHours();
            }

            totalHours += duration;

            courseInfos.add(TeacherDashboardDTO.AssignedCourseInfo.builder()
                    .courseName(period.getCourse().getName())
                    .term(period.getSchoolTerm().getName())
                    .schedule(scheduleStr)
                    .build());
        }

        // Determinar estado de carga (Lógica simple)
        String status = "LIBRE";
        if (totalHours > 0) status = "NORMAL";
        if (totalHours > 40) status = "SOBRECARGA";

        return TeacherDashboardDTO.builder()
                .teacherId(teacher.getId())
                .fullName(teacher.getFirstName() + " " + teacher.getLastName())
                .email(teacher.getEmail())
                .subjects(teacher.getSubjects()) // Las materias que sabe
                .activeCourses(courseInfos)
                .totalWeeklyHours(totalHours)
                .workloadStatus(status)
                .build();
    }

    public List<AvailableTeacherDTO> findAvailableTeachers(String subject, int maxHours) {
        // 1. Traer todos los profes
        List<User> allTeachers = userRepository.findByRole(Role.PROFESSOR);
        List<AvailableTeacherDTO> result = new ArrayList<>();

        for (User teacher : allTeachers) {
            // Filtro 1: ¿Sabe la materia?
            if (teacher.getSubjects() == null || !teacher.getSubjects().contains(subject)) {
                continue;
            }

            // Filtro 2: Calcular carga actual (Reusamos lógica interna o simplificamos)
            List<CourseParticipation> active = participationRepository
                    .findAllByUserIdAndRoleAndStatus(teacher.getId(), CourseRole.MAIN_PROFESSOR, ParticipationStatus.ACTIVE);

            long hours = 0;
            for(CourseParticipation p : active) {
                if(p.getAcademicPeriod().getSchedule() != null) {
                    hours += p.getAcademicPeriod().getSchedule().getDurationInHours();
                }
            }

            // Filtro 3: ¿Tiene espacio?
            if (hours < maxHours) {
                result.add(AvailableTeacherDTO.builder()
                        .id(teacher.getId())
                        .fullName(teacher.getFirstName() + " " + teacher.getLastName())
                        .currentHours(hours)
                        .status(hours > 35 ? "ALERTA" : "LIBRE")
                        .build());
            }
        }
        return result;
    }
    // ... dentro de TeacherDashboardService

    public TeacherDashboardDTO getTeacherStats(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));

        // Opcional: Validar que sea realmente un profesor
        if (teacher.getRole() != Role.PROFESSOR) {
            // Puedes decidir si lanzar error o devolverlo igual
            // throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no es profesor");
        }

        return buildTeacherStats(teacher);
    }
}
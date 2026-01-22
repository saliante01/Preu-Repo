package com.backend.backendpreu.users.service;

import com.backend.backendpreu.academicPaticipation.model.CourseParticipation;
import com.backend.backendpreu.academicPaticipation.model.CourseRole;
import com.backend.backendpreu.academicPaticipation.model.ParticipationStatus;
import com.backend.backendpreu.academicPaticipation.repository.CourseParticipationRepository;
import com.backend.backendpreu.academicPeriod.model.AcademicPeriod;
import com.backend.backendpreu.academicPeriod.model.ClassSchedule;
import com.backend.backendpreu.courses.model.Subject;
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
        List<User> teachers = userRepository.findByRole(Role.PROFESSOR);
        return teachers.stream().map(this::buildTeacherStats).collect(Collectors.toList());
    }

    private TeacherDashboardDTO buildTeacherStats(User teacher) {
        List<CourseParticipation> activeParticipations = participationRepository
                .findAllByUserIdAndRolesInAndStatus(
                        teacher.getId(),
                        List.of(CourseRole.MAIN_PROFESSOR, CourseRole.SUBSTITUTE_PROFESSOR),
                        ParticipationStatus.ACTIVE
                );

        // CAMBIO 1: Usamos double para decimales (1.5 horas)
        double totalHours = 0.0;
        List<TeacherDashboardDTO.AssignedCourseInfo> courseInfos = new ArrayList<>();

        for (CourseParticipation p : activeParticipations) {
            AcademicPeriod period = p.getAcademicPeriod();
            ClassSchedule schedule = period.getSchedule();

            String scheduleStr = "Sin Horario";
            // CAMBIO 2: Variable local double
            double duration = 0.0;

            if (schedule != null) {
                scheduleStr = schedule.getDayOfWeek() + " " + schedule.getStartTime() + " - " + schedule.getEndTime();
                // CAMBIO 3: Se asume que getDurationInHours devuelve double
                duration = schedule.getDurationInHours();
            }

            totalHours += duration;

            courseInfos.add(TeacherDashboardDTO.AssignedCourseInfo.builder()
                    .courseName(period.getCourse().getName())
                    .term(period.getSchoolTerm().getName())
                    .schedule(scheduleStr)
                    .build());
        }

        String status = "LIBRE";
        if (totalHours > 0) status = "NORMAL";
        if (totalHours > 40) status = "SOBRECARGA";

        return TeacherDashboardDTO.builder()
                .teacherId(teacher.getId())
                .fullName(teacher.getFirstName() + " " + teacher.getLastName())
                .email(teacher.getEmail())
                .subjects(teacher.getSubjects())
                .activeCourses(courseInfos)
                .totalWeeklyHours(totalHours) // El DTO debe aceptar Double
                .workloadStatus(status)
                .build();
    }

    // CAMBIO 4: El parámetro maxHours lo dejamos en int o double según prefieras, pero la lógica interna es double
    public List<AvailableTeacherDTO> findAvailableTeachers(Subject subject, int maxHours) {
        List<User> allTeachers = userRepository.findByRole(Role.PROFESSOR);
        List<AvailableTeacherDTO> result = new ArrayList<>();

        for (User teacher : allTeachers) {
            if (teacher.getSubjects() == null || !teacher.getSubjects().contains(subject)) {
                continue;
            }

            List<CourseParticipation> active = participationRepository
                    .findAllByUserIdAndRolesInAndStatus(
                            teacher.getId(),
                            List.of(CourseRole.MAIN_PROFESSOR, CourseRole.SUBSTITUTE_PROFESSOR),
                            ParticipationStatus.ACTIVE
                    );

            // CAMBIO 5: Cálculo con decimales
            double hours = 0.0;
            for(CourseParticipation p : active) {
                if(p.getAcademicPeriod().getSchedule() != null) {
                    hours += p.getAcademicPeriod().getSchedule().getDurationInHours();
                }
            }

            if (hours < maxHours) {
                result.add(AvailableTeacherDTO.builder()
                        .id(teacher.getId())
                        .fullName(teacher.getFirstName() + " " + teacher.getLastName())
                        .currentHours(hours) // El DTO debe aceptar Double
                        .status(hours > 35 ? "ALERTA" : "LIBRE")
                        .build());
            }
        }
        return result;
    }

    public TeacherDashboardDTO getTeacherStats(Long teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado"));

        if (teacher.getRole() != Role.PROFESSOR) {
            // Validación opcional
        }

        return buildTeacherStats(teacher);
    }
}
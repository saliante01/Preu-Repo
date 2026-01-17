package com.backend.backendpreu.users.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class TeacherDashboardDTO {
    private Long teacherId;
    private String fullName;
    private String email;
    private Set<String> subjects; //Que sabe enseñas el profesor
    private List<AssignedCourseInfo> activeCourses;
    private Long totalWeeklyHours; //Carga horaria total
    private String workloadStatus; //"Libre,Normal,sonbrecarga

    @Data
    @Builder
    public static class AssignedCourseInfo {
        private String courseName;
        private String schedule;
        private String term;
    }
}

This is a template for folder structure

src/
└── main/
└── java/
└── com.backend.backendpreu/
├── BackendPreuApplication.java
│
├── config/
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   └── JwtConfig.java
│   ├── database/
│   │   └── DatabaseConfig.java
│   └── AppConfig.java
├──academicperiod/
├── model/
│   ├── AcademicPeriod.java
│   └── AcademicPeriodStatus.java
│
├── repository/
│   └── AcademicPeriodRepository.java
│
├── service/
│   └── AcademicPeriodService.java
│
└── controller/
└── AcademicPeriodController.java
│
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── service/
│   │   └── AuthService.java
│   ├── model/
│   │   ├── LoginRequest.java
│   │   └── AuthResponse.java
│   └── security/
│       └── JwtAuthFilter.java
│
├── users/
│   ├── controller/
│   │   └── UserController.java
│   ├── service/
│   │   └── UserService.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── model/
│   │   ├── User.java
│   │   └── UserDTO.java
│   └── mapper/
│       └── UserMapper.java
│
├── courses/
│   ├── controller/
│   │   └── CourseController.java
│   ├── service/
│   │   └── CourseService.java
│   ├── repository/
│   │   ├── CourseRepository.java
│   │   ├── AcademicPeriodRepository.java
│   │   └── CourseParticipationRepository.java
│   ├── model/
│   │   ├── Course.java
│   │   ├── AcademicPeriod.java
│   │   └── CourseParticipation.java
│   └── dto/
│       └── CourseDTO.java
│
├── meetings/
│   ├── controller/
│   │   └── MeetingController.java
│   ├── service/
│   │   └── MeetingService.java
│   ├── repository/
│   │   ├── MeetingRepository.java
│   │   └── AttendanceRepository.java
│   ├── model/
│   │   ├── Meeting.java
│   │   └── Attendance.java
│   └── dto/
│       └── MeetingDTO.java
│
├── contents/
│   ├── controller/
│   │   └── ContentController.java
│   ├── service/
│   │   └── ContentService.java
│   ├── repository/
│   │   └── ContentRepository.java
│   ├── model/
│   │   └── Content.java
│   └── dto/
│       └── ContentDTO.java
│
├── evaluations/
│   ├── controller/
│   │   └── EvaluationController.java
│   ├── service/
│   │   └── EvaluationService.java
│   ├── repository/
│   │   ├── EvaluationRepository.java
│   │   └── GradeRepository.java
│   ├── model/
│   │   ├── Evaluation.java
│   │   └── Grade.java
│   └── dto/
│       └── EvaluationDTO.java
│
├── payments/
│   ├── controller/
│   │   └── PaymentController.java
│   ├── service/
│   │   └── PaymentService.java
│   ├── repository/
│   │   └── PaymentRepository.java
│   ├── model/
│   │   └── Payment.java
│   └── dto/
│       └── PaymentDTO.java
│
├── notifications/
│   ├── controller/
│   │   └── NotificationController.java
│   ├── service/
│   │   └── NotificationService.java
│   ├── repository/
│   │   ├── NotificationRepository.java
│   │   ├── NotificationTargetRepository.java
│   │   └── NotificationDeliveryRepository.java
│   ├── model/
│   │   ├── Notification.java
│   │   ├── NotificationTarget.java
│   │   └── NotificationDelivery.java
│   └── dto/
│       └── NotificationDTO.java
│
├── audit/
│   ├── service/
│   │   └── AuditService.java
│   ├── repository/
│   │   └── AuditLogRepository.java
│   └── model/
│       └── AuditLog.java
│
├── common/
│   ├── enums/
│   ├── exceptions/
│   ├── dto/
│   └── utils/
│
└── database/
├── migrations/
└── seeders/

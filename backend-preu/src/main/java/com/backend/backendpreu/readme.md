Estructura de carpetas recomendadas

src/
└── main/
└── java/
└── com.backend.backendpreu/
├── BackendPreuApplication.java
├── config/
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   └── RoleConfig.java
│   ├── database/
│   │   └── DatabaseConfig.java
│   └── AppConfig.java
│
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── AuthDTO.java
│   └── AuthFilter.java
│
├── users/
│   ├── UserController.java
│   ├── UserService.java
│   ├── UserRepository.java
│   ├── User.java
│   └── UserDTO.java
│
├── courses/
│   ├── CourseController.java
│   ├── CourseService.java
│   ├── CourseRepository.java
│   ├── Course.java
│   ├── AcademicPeriod.java
│   ├── CourseParticipation.java
│   └── CourseDTO.java
│
├── meetings/
│   ├── MeetingController.java
│   ├── MeetingService.java
│   ├── MeetingRepository.java
│   ├── Meeting.java
│   ├── Attendance.java
│   └── MeetingDTO.java
│
├── contents/
│   ├── ContentController.java
│   ├── ContentService.java
│   ├── ContentRepository.java
│   ├── Content.java
│   └── ContentDTO.java
│
├── evaluations/
│   ├── EvaluationController.java
│   ├── EvaluationService.java
│   ├── EvaluationRepository.java
│   ├── Evaluation.java
│   ├── Grade.java
│   └── EvaluationDTO.java
│
├── payments/
│   ├── PaymentController.java
│   ├── PaymentService.java
│   ├── PaymentRepository.java
│   ├── Payment.java
│   └── PaymentDTO.java
│
├── notifications/
│   ├── NotificationController.java
│   ├── NotificationService.java
│   ├── NotificationRepository.java
│   ├── Notification.java
│   ├── NotificationTarget.java
│   ├── NotificationDelivery.java
│   └── NotificationDTO.java
│
├── audit/
│   ├── AuditLog.java
│   ├── AuditRepository.java
│   └── AuditService.java
│
├── common/
│   ├── enums/
│   │   ├── Role.java
│   │   ├── UserStatus.java
│   │   ├── PaymentStatus.java
│   │   └── NotificationType.java
│   │
│   ├── exceptions/
│   ├── utils/
│   └── dto/
│
└── database/
├── migrations/
└── seeders/

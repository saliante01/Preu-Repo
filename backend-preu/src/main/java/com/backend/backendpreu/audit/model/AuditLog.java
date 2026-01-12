package com.backend.backendpreu.audit.model;

import com.backend.backendpreu.users.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // EL ACTOR: ¿Quién hizo la acción? (Ej. El Admin)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // LA ACCIÓN: "CREATE", "UPDATE_STATUS", "DELETE"
    @Column(nullable = false)
    private String action;

    // LA ENTIDAD AFECTADA: "USER", "COURSE"
    @Column(nullable = false)
    private String entityName;

    // EL ID DE LA ENTIDAD: 45
    @Column(nullable = false)
    private Long entityId;

    // --- NUEVO CAMPO ---
    // DETALLES: "Creó al usuario pepe@gmail.com con rol USER"
    @Column(length = 1000) // Damos espacio suficiente
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
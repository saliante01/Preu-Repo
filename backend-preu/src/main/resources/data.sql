-- =================================================================================
-- 1. USUARIOS (Admin, 2 Profesores, 3 Alumnos)
-- =================================================================================

-- ID 1: Admin
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (1, 'admin@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'ADMIN', true, CURRENT_TIMESTAMP, 'Admin', 'Sistema');

-- ID 2: Profe Juan (Ciencias Exactas)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (2, 'profe.juan@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'PROFESSOR', true, CURRENT_TIMESTAMP, 'Juan', 'Pérez');

-- ID 3: Profe Ana (Humanidades)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (3, 'profe.ana@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'PROFESSOR', true, CURRENT_TIMESTAMP, 'Ana', 'Gómez');

-- ID 4, 5, 6: Alumnos (Igual que antes)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name) VALUES
                                                                                                  (4, 'alumno@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Pedrito', 'Alumno'),
                                                                                                  (5, 'laura@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Laura', 'Estudiosa'),
                                                                                                  (6, 'carlos@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', false, CURRENT_TIMESTAMP, 'Carlos', 'Inactivo');


-- =================================================================================
-- 1.1 MATERIAS DE LOS PROFESORES (NUEVO)
-- =================================================================================
-- Profe Juan sabe Matemáticas y Física
INSERT INTO user_subjects (user_id, subject_name) VALUES (2, 'Matemáticas');
INSERT INTO user_subjects (user_id, subject_name) VALUES (2, 'Física');
INSERT INTO user_subjects (user_id, subject_name) VALUES (2, 'Ciencias');

-- Profe Ana sabe Lenguaje e Historia
INSERT INTO user_subjects (user_id, subject_name) VALUES (3, 'Lenguaje');
INSERT INTO user_subjects (user_id, subject_name) VALUES (3, 'Historia');


-- =================================================================================
-- 2. SEMESTRES y 3. CATÁLOGO (Igual que antes)
-- =================================================================================
INSERT INTO school_terms (id, name, start_date, end_date, active) VALUES
                                                                      (1, 'Invierno 2025', '2025-03-01', '2025-07-31', false),
                                                                      (2, 'Admisión 2026', '2026-03-01', '2026-11-30', true),
                                                                      (3, 'Verano Intensivo 2027', '2027-01-05', '2027-02-28', true);

INSERT INTO courses (id, name, code, description, created_at) VALUES
                                                                  (1, 'Matemáticas M1', 'MAT-PAES-1', 'Matemática común obligatoria', NOW()),
                                                                  (2, 'Competencia Lectora', 'LEN-PAES', 'Estrategias de comprensión lectora', NOW()),
                                                                  (3, 'Matemáticas M2', 'MAT-PAES-2', 'Matemática avanzada electiva', NOW()),
                                                                  (4, 'Historia y Cs. Sociales', 'HIS-PAES', 'Historia de Chile y el Mundo', NOW()),
                                                                  (5, 'Ciencias - Biología', 'CIE-BIO', 'Módulo electivo de Biología', NOW()),
                                                                  (6, 'Ciencias - Física', 'CIE-FIS', 'Módulo electivo de Física', NOW());


-- =================================================================================
-- 4. HORARIOS (ClassSchedule) - (NUEVO)
-- Creamos los bloques horarios primero
-- =================================================================================

-- ID 1: Lunes 10:00 - 11:30
INSERT INTO class_schedules (id, day_of_week, start_time, end_time) VALUES (1, 'MONDAY', '10:00:00', '11:30:00');
-- ID 2: Martes 14:00 - 15:30
INSERT INTO class_schedules (id, day_of_week, start_time, end_time) VALUES (2, 'TUESDAY', '14:00:00', '15:30:00');
-- ID 3: Miércoles 08:30 - 10:00
INSERT INTO class_schedules (id, day_of_week, start_time, end_time) VALUES (3, 'WEDNESDAY', '08:30:00', '10:00:00');
-- ID 4: Jueves 16:00 - 18:00
INSERT INTO class_schedules (id, day_of_week, start_time, end_time) VALUES (4, 'THURSDAY', '16:00:00', '18:00:00');


-- =================================================================================
-- 5. APERTURA DE CLASES (AcademicPeriod)
-- Ahora enlazamos el schedule_id
-- =================================================================================

-- Cursos Pasados (Sin horario, schedule_id NULL está bien o ponemos uno dummy)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (1, 1, 1, '2025-03-05', '2025-07-30', 'CLOSED', NOW(), 30, NULL);
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (2, 2, 1, '2025-03-05', '2025-07-30', 'CLOSED', NOW(), 30, NULL);

-- Cursos Actuales (Con horario asignado)
-- M1 Actual -> Lunes (ID 1)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (3, 1, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW(), 40, 1);

-- Lenguaje Actual -> Martes (ID 2)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (4, 2, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW(), 40, 2);

-- Historia Actual -> Miércoles (ID 3)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (5, 4, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW(), 35, 3);

-- Física Actual -> Jueves (ID 4)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (6, 6, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW(), 20, 4);

-- Futuro
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at, max_capacity, schedule_id)
VALUES (7, 1, 3, '2027-01-05', '2027-02-28', 'ACTIVE', NOW(), 50, NULL);


-- =================================================================================
-- 6. INSCRIPCIONES (CourseParticipation) - (Igual que antes)
-- =================================================================================
-- Profes
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (2, 1, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), (2, 3, 'MAIN_PROFESSOR', 'ACTIVE', NOW()),
                                                                                              (2, 6, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), (2, 7, 'MAIN_PROFESSOR', 'ACTIVE', NOW()),
                                                                                              (3, 2, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), (3, 4, 'MAIN_PROFESSOR', 'ACTIVE', NOW()),
                                                                                              (3, 5, 'MAIN_PROFESSOR', 'ACTIVE', NOW());

-- Alumnos
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (4, 1, 'STUDENT', 'ACTIVE', '2025-03-05 10:00:00'), (4, 2, 'STUDENT', 'ACTIVE', '2025-03-05 10:00:00'),
                                                                                              (4, 3, 'STUDENT', 'ACTIVE', NOW()), (4, 4, 'STUDENT', 'ACTIVE', NOW()),
                                                                                              (5, 3, 'STUDENT', 'ACTIVE', NOW()), (5, 5, 'STUDENT', 'ACTIVE', NOW()),
                                                                                              (5, 6, 'STUDENT', 'ACTIVE', NOW()), (1, 5, 'STUDENT', 'ACTIVE', NOW());


-- =================================================================================
-- 7. REINICIO DE CONTADORES
-- =================================================================================
ALTER TABLE school_terms ALTER COLUMN id RESTART WITH 100;
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE courses ALTER COLUMN id RESTART WITH 100;
ALTER TABLE class_schedules ALTER COLUMN id RESTART WITH 100; -- NUEVO
ALTER TABLE academic_periods ALTER COLUMN id RESTART WITH 100;
ALTER TABLE course_participation ALTER COLUMN id RESTART WITH 100;
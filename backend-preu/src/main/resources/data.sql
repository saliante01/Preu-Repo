-- =================================================================================
-- 1. USUARIOS (Admin, 2 Profesores, 3 Alumnos)
-- Password para todos: 'password'
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

-- ID 4: Alumno Pedrito (El clásico)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (4, 'alumno@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Pedrito', 'Alumno');

-- ID 5: Alumna Laura (Nueva)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (5, 'laura@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Laura', 'Estudiosa');

-- ID 6: Alumno Carlos (Inactivo/Desertor)
INSERT INTO users (id, email, password_hash, role, active, created_at, first_name, last_name)
VALUES (6, 'carlos@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', false, CURRENT_TIMESTAMP, 'Carlos', 'Inactivo');


-- =================================================================================
-- 2. SEMESTRES (SchoolTerm) - Pasado, Presente y Futuro
-- =================================================================================

-- ID 1: Semestre Pasado (Cerrado)
INSERT INTO school_terms (id, name, start_date, end_date, active)
VALUES (1, 'Invierno 2025', '2025-03-01', '2025-07-31', false);

-- ID 2: Semestre Actual (Abierto)
INSERT INTO school_terms (id, name, start_date, end_date, active)
VALUES (2, 'Admisión 2026', '2026-03-01', '2026-11-30', true);

-- ID 3: Semestre Futuro (Planificación)
INSERT INTO school_terms (id, name, start_date, end_date, active)
VALUES (3, 'Verano Intensivo 2027', '2027-01-05', '2027-02-28', true);


-- =================================================================================
-- 3. CATÁLOGO DE CURSOS (Course)
-- =================================================================================

INSERT INTO courses (id, name, code, description, created_at) VALUES
                                                                  (1, 'Matemáticas M1', 'MAT-PAES-1', 'Matemática común obligatoria', NOW()),
                                                                  (2, 'Competencia Lectora', 'LEN-PAES', 'Estrategias de comprensión lectora', NOW()),
                                                                  (3, 'Matemáticas M2', 'MAT-PAES-2', 'Matemática avanzada electiva', NOW()),
                                                                  (4, 'Historia y Cs. Sociales', 'HIS-PAES', 'Historia de Chile y el Mundo', NOW()),
                                                                  (5, 'Ciencias - Biología', 'CIE-BIO', 'Módulo electivo de Biología', NOW()),
                                                                  (6, 'Ciencias - Física', 'CIE-FIS', 'Módulo electivo de Física', NOW());


-- =================================================================================
-- 4. APERTURA DE CLASES (AcademicPeriod)
-- =================================================================================

-- --- DEL PASADO (Semestre ID 1: Invierno 2025) ---
-- ID 1: M1 Pasado (Cerrado)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (1, 1, 1, '2025-03-05', '2025-07-30', 'CLOSED', NOW());

-- ID 2: Lenguaje Pasado (Cerrado)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (2, 2, 1, '2025-03-05', '2025-07-30', 'CLOSED', NOW());


-- --- DEL PRESENTE (Semestre ID 2: Admisión 2026) ---
-- ID 3: M1 Actual (Activo)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (3, 1, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW());

-- ID 4: Lenguaje Actual (Activo)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (4, 2, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW());

-- ID 5: Historia Actual (Activo)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (5, 4, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW());

-- ID 6: Física Actual (Activo)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (6, 6, 2, '2026-03-05', '2026-11-20', 'ACTIVE', NOW());


-- --- DEL FUTURO (Semestre ID 3: Verano 2027) ---
-- ID 7: M1 Intensivo Futuro (Planificado)
INSERT INTO academic_periods (id, course_id, term_id, start_date, end_date, status, created_at)
VALUES (7, 1, 3, '2027-01-05', '2027-02-28', 'ACTIVE', NOW());


-- =================================================================================
-- 5. INSCRIPCIONES (CourseParticipation)
-- =================================================================================

-- --- PROFESORES ---
-- Profe Juan (ID 2) dicta todas las Matemáticas y Física
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (2, 1, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), -- M1 Pasado
                                                                                              (2, 3, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), -- M1 Actual
                                                                                              (2, 6, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), -- Física Actual
                                                                                              (2, 7, 'MAIN_PROFESSOR', 'ACTIVE', NOW()); -- M1 Futuro

-- Profe Ana (ID 3) dicta Lenguaje e Historia
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (3, 2, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), -- Lenguaje Pasado
                                                                                              (3, 4, 'MAIN_PROFESSOR', 'ACTIVE', NOW()), -- Lenguaje Actual
                                                                                              (3, 5, 'MAIN_PROFESSOR', 'ACTIVE', NOW()); -- Historia Actual


-- --- ALUMNOS ---

-- ALUMNO PEDRITO (ID 4) - El alumno antiguo
-- Hizo cursos el año pasado
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (4, 1, 'STUDENT', 'ACTIVE', '2025-03-05 10:00:00'), -- M1 Pasado (Terminado)
                                                                                              (4, 2, 'STUDENT', 'ACTIVE', '2025-03-05 10:00:00'); -- Lenguaje Pasado (Terminado)

-- Y ahora está tomando cursos actuales
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (4, 3, 'STUDENT', 'ACTIVE', NOW()), -- M1 Actual
                                                                                              (4, 4, 'STUDENT', 'ACTIVE', NOW()); -- Lenguaje Actual

-- ALUMNA LAURA (ID 5) - Alumna Nueva
-- Solo tiene cursos actuales
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
                                                                                              (5, 3, 'STUDENT', 'ACTIVE', NOW()), -- M1 Actual
                                                                                              (5, 5, 'STUDENT', 'ACTIVE', NOW()), -- Historia Actual
                                                                                              (5, 6, 'STUDENT', 'ACTIVE', NOW()); -- Física Actual

-- ADMIN (ID 1) - Curiosidad
-- Se mete a mirar Historia
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at) VALUES
    (1, 5, 'STUDENT', 'ACTIVE', NOW());

-- ... tus inserts anteriores ...

-- ---------------------------------------------------------
-- REINICIAR CONTADORES (Para H2 Database)
-- Ajustamos la secuencia para que empiece después del último ID que usaste (ej: 10)
-- ---------------------------------------------------------
-- ---------------------------------------------------------
-- REINICIAR CONTADORES (CORREGIDO)
-- Usamos 100 para asegurarnos de no chocar con los datos insertados
-- ---------------------------------------------------------
ALTER TABLE school_terms ALTER COLUMN id RESTART WITH 100;
ALTER TABLE users ALTER COLUMN id RESTART WITH 100;
ALTER TABLE courses ALTER COLUMN id RESTART WITH 100;
ALTER TABLE academic_periods ALTER COLUMN id RESTART WITH 100;
ALTER TABLE course_participation ALTER COLUMN id RESTART WITH 100;
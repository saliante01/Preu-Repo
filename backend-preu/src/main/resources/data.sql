INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ( 'admin@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'ADMIN', true, CURRENT_TIMESTAMP, 'Admin', 'Sistema');
-- Usuario PROFESOR //password
INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ('profe@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'PROFESSOR', true, CURRENT_TIMESTAMP, 'Juan', 'Profesor');

-- Usuario ALUMNO (ESTUDIANTE)
INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ('alumno@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Pedrito', 'Alumno');

-- ---------------------------------------------------------
-- 1. CREAR EL SEMESTRE (SchoolTerm)
-- ---------------------------------------------------------
INSERT INTO school_terms (name, start_date, end_date, active)
VALUES ('Admisión 2026', '2026-03-01', '2026-11-30', true);
-- Asumimos que este Semestre tendrá ID = 1

-- ---------------------------------------------------------
-- 2. CREAR CURSOS BASE (Course)
-- ---------------------------------------------------------
INSERT INTO courses (name, code, description, created_at) VALUES
                                                              ('Matemáticas M1', 'MAT-PAES-1', 'Entrenamiento intensivo M1', NOW()), -- ID 1
                                                              ('Competencia Lectora', 'LEN-PAES', 'Estrategias de comprensión', NOW()); -- ID 2

-- ---------------------------------------------------------
-- 3. ABRIR LAS CLASES (AcademicPeriod)
-- ---------------------------------------------------------
-- Clase de Matemáticas en el Semestre 2026
INSERT INTO academic_periods (course_id, term_id, start_date, end_date, status, created_at)
VALUES (1, 1, '2026-03-05', '2026-11-20', 'ACTIVE', NOW()); -- ID 1

-- Clase de Lenguaje en el Semestre 2026
INSERT INTO academic_periods (course_id, term_id, start_date, end_date, status, created_at)
VALUES (2, 1, '2026-03-05', '2026-11-20', 'ACTIVE', NOW()); -- ID 2

-- ---------------------------------------------------------
-- 4. MATRICULAR (CourseParticipation) - ¡Aquí está la magia!
-- ---------------------------------------------------------

-- A. El PROFE (ID 2) será el profesor de Matemáticas (Clase ID 1)
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at)
VALUES (2, 1, 'MAIN_PROFESSOR', 'ACTIVE', NOW());

-- B. El ALUMNO (ID 3) tomará Matemáticas (Clase ID 1)
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at)
VALUES (3, 1, 'STUDENT', 'ACTIVE', NOW());

-- C. El ALUMNO (ID 3) TAMBIÉN tomará Lenguaje (Clase ID 2)
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at)
VALUES (3, 2, 'STUDENT', 'ACTIVE', NOW());

-- D. (Para tu prueba) El ADMIN (ID 1) tomará Lenguaje solo para probar que ves datos
INSERT INTO course_participation (user_id, academic_period_id, role, status, enrolled_at)
VALUES (1, 2, 'STUDENT', 'ACTIVE', NOW());
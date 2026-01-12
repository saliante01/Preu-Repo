INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ( 'admin@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'ADMIN', true, CURRENT_TIMESTAMP, 'Admin', 'Sistema');
-- Usuario PROFESOR //password
INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ('profe@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'PROFESSOR', true, CURRENT_TIMESTAMP, 'Juan', 'Profesor');

-- Usuario ALUMNO (ESTUDIANTE)
INSERT INTO users (email, password_hash, role, active, created_at, first_name, last_name)
VALUES ('alumno@preu.cl', '$2a$12$A/G3/2lOyD646SpaYc1we.C.WytRrENQoHXQSJbaPk1qYFqZ/Yu0q', 'STUDENT', true, CURRENT_TIMESTAMP, 'Pedrito', 'Alumno');
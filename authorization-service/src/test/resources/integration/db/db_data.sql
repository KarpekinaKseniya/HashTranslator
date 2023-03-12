INSERT INTO roles(id, name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles(id, name)
VALUES (2, 'ROLE_USER');

ALTER SEQUENCE users_seq RESTART WITH 1;
-- ROLE: user PASSWORD: password
INSERT INTO users(id, firstname, lastname, email, password)
VALUES (1, 'John', 'Test', 'test@gmail.ru', '$2a$10$r.ctB43XcviDPD0trfxdUeo7vag/N2JqnHGtXm7/sBmXjn567MEEm');
-- ROLE: admin PASSWORD: adminpassword
INSERT INTO users(id, firstname, lastname, email, password)
VALUES (2, 'Mark', 'Black', 'black34@gmail.eu', '$2a$10$gQhskCJoBMGgjApbYrtqQ.87DdnOBF1dTobfQVyxNMWtejkCugQF6');
-- ROLE: user PASSWORD: @.7^veVJwQshCV
INSERT INTO users(id, firstname, lastname, email, password)
VALUES (3, 'Carla', 'Smith', 'smith12@mail.com', '$2a$10$OYW9hLg0lJjdnUQ5THHLE.TaI6cMVqeSTuva.CUb7uVXl2GtPEttO');
ALTER SEQUENCE users_seq RESTART WITH 4;

INSERT INTO user_roles(user_id, role_id)
VALUES (1, 2);
INSERT INTO user_roles(user_id, role_id)
VALUES (3, 2);
INSERT INTO user_roles(user_id, role_id)
VALUES (2, 1);
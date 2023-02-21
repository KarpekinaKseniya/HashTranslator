INSERT INTO roles(id, name)
VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles(id, name)
VALUES (2, 'ROLE_USER');

-- ROLE: user PASSWORD: @.7^veVJwQshCV
INSERT INTO users(firstname, lastname, email, password)
VALUES ('John', 'Test', 'test@gmail.ru', '$2a$10$OYW9hLg0lJjdnUQ5THHLE.TaI6cMVqeSTuva.CUb7uVXl2GtPEttO');
-- ROLE: admin PASSWORD: adminpassword
INSERT INTO users(firstname, lastname, email, password)
VALUES ('Mark', 'Black', 'black34@gmail.eu', '$2a$10$gQhskCJoBMGgjApbYrtqQ.87DdnOBF1dTobfQVyxNMWtejkCugQF6');
-- ROLE: user PASSWORD: password
INSERT INTO users(firstname, lastname, email, password)
VALUES ('Carla', 'Smith', 'smith12@mail.com', '$2a$10$r.ctB43XcviDPD0trfxdUeo7vag/N2JqnHGtXm7/sBmXjn567MEEm');
-- ROLE: admin PASSWORD: sh~Av9CSa+U62axg
INSERT INTO users(firstname, lastname, email, password)
VALUES ('Adam', 'Yang', 'evil6@gmail.eu', '$2a$10$1Qaqa0sy.VZwtZXZR7Eg7.EbJKTs075VqwhjVO94qQvnl/PtTajaG');
-- ROLE: user PASSWORD: -4aCc<(}-$h79,BZ
INSERT INTO users(firstname, lastname, email, password)
VALUES ('Liza', 'Brown', 'bor.work11@mail.com', '$2a$10$pSZk4Q/.rPgeKT8z4HWKP..uCUvRYGXIhvXxB94j55mBy8I08.9Ce');

INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'test@gmail.ru'), 2);
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'evil6@gmail.eu'), 1);
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'smith12@mail.com'), 2);
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'bor.work11@mail.com'), 2);
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'black34@gmail.eu'), 1);
CREATE TABLE IF NOT EXISTS roles
(
    id   BIGSERIAL   NOT NULL
        CONSTRAINT roles_pk PRIMARY KEY,
    name VARCHAR(15) NOT NULL
);

CREATE UNIQUE INDEX roles_id_uindex on roles (id);
CREATE UNIQUE INDEX roles_name_uindex on roles (name);

CREATE TABLE IF NOT EXISTS users
(
    id        BIGSERIAL    NOT NULL
        CONSTRAINT users_pk PRIMARY KEY,
    firstname VARCHAR(25),
    lastname  VARCHAR(25),
    email     VARCHAR(255) NOT NULL,
    password  VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX users_id_uindex on users (id);
CREATE UNIQUE INDEX users_email_uindex on users (email);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id BIGINT NOT NULL
        CONSTRAINT user_roles_user_id_fk REFERENCES users,
    role_id BIGINT NOT NULL
        CONSTRAINT user_roles_roles_id_fk REFERENCES roles
);
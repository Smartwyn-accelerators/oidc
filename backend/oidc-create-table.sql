
-- =============================
-- Create table: "user"
-- =============================
CREATE TABLE "user" (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL,
    email_address VARCHAR(256) NOT NULL,
    first_name VARCHAR(32),
    last_name VARCHAR(32)
);

-- =============================
-- Create table: "role"
-- =============================
CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL
);

-- =============================
-- Create table: "permission"
-- =============================
CREATE TABLE permission (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL
);

-- =============================
-- Create table: "userrole"
-- =============================
CREATE TABLE userrole (
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT fktbick5dbrpnos6ll2175dt5qr FOREIGN KEY (user_id)
        REFERENCES "user"(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk5j6gggggggggfrtttt FOREIGN KEY (role_id)
        REFERENCES role(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- =============================
-- Create table: "userpermission"
-- =============================
CREATE TABLE userpermission (
    permission_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    revoked BOOLEAN,
    PRIMARY KEY (permission_id, user_id),
    CONSTRAINT fks5wddn2j2872axd91k4heuvoe FOREIGN KEY (user_id)
        REFERENCES "user"(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk5j6kxx2g0pxrd8ht2ss9e0uoe FOREIGN KEY (permission_id)
        REFERENCES permission(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- =============================
-- Create table: "rolepermission"
-- =============================
CREATE TABLE rolepermission (
    permission_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    PRIMARY KEY (permission_id, role_id),
    CONSTRAINT fk14kirk7t76s89r7er6c6ircbe FOREIGN KEY (permission_id)
        REFERENCES permission(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkfa75v4h0djvvrq0pqrh0x9n3m FOREIGN KEY (role_id)
        REFERENCES role(id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255)        NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    role     VARCHAR(50)         NOT NULL,
    phone    VARCHAR(20)
);

CREATE TABLE blocks
(
    id                   BIGSERIAL PRIMARY KEY,
    identifier           VARCHAR(100) UNIQUE NOT NULL,
    total_floors         INTEGER             NOT NULL,
    apartments_per_floor INTEGER             NOT NULL
);

CREATE TABLE units
(
    id         BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(100) NOT NULL,
    block_id   BIGINT       NOT NULL,
    FOREIGN KEY (block_id) REFERENCES blocks (id)
);

CREATE TABLE user_units
(
    user_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, unit_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (unit_id) REFERENCES units (id)
);

CREATE TABLE issue_types
(
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL UNIQUE,
    sla_hours INTEGER      NOT NULL
);

CREATE TABLE issue_statuses
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL UNIQUE,
    is_default BOOLEAN      NOT NULL DEFAULT FALSE,
    is_final   BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE issues
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT         NOT NULL,
    unit_id     BIGINT       NOT NULL,
    type_id     BIGINT       NOT NULL,
    status_id   BIGINT       NOT NULL,
    created_by  BIGINT       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units (id),
    FOREIGN KEY (type_id) REFERENCES issue_types (id),
    FOREIGN KEY (status_id) REFERENCES issue_statuses (id),
    FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE attachments
(
    id       BIGSERIAL PRIMARY KEY,
    issue_id BIGINT       NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    FOREIGN KEY (issue_id) REFERENCES issues (id)
);

CREATE TABLE comments
(
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT      NOT NULL,
    issue_id   BIGINT    NOT NULL,
    author_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);
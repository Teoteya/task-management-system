-- Таблица пользователей
CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    password   VARCHAR(80) NOT NULL,
    email      VARCHAR(50) UNIQUE,
    role       VARCHAR(30) NOT NULL
);

-- Таблица ролей
CREATE TABLE roles (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE
);

-- Связующая таблица для пользователей и ролей
CREATE TABLE users_roles (
    user_id     BIGINT NOT NULL,
    role_id     BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Таблица задач
CREATE TABLE tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description TEXT,
    status      VARCHAR(20) NOT NULL,
    priority    VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    author_id   BIGINT NOT NULL,
    assignee_id BIGINT,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (assignee_id) REFERENCES users (id) ON DELETE SET NULL
);

-- Таблица комментариев
CREATE TABLE comments (
    id         BIGSERIAL PRIMARY KEY,
    content    TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    task_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
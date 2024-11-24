-- Добавляем роли
INSERT INTO roles (name)
VALUES
    ('ROLE_USER'),
    ('ROLE_ADMIN');

-- Добавляем пользователей
INSERT INTO users (password, email, role)
VALUES
    ('$2a$12$N14/uJb.6z98xXRouBn2UeZtobh4lN7EvWj39j8Oa.NtI.u8M3Jk.', 'user@gmail.com', 'ROLE_USER'),    -- password: 12345
    ('$2a$12$L8XNbxgE1QDX.E3CrpHLIuU8gwc1QmsfnbHjQOuPlaV6cwBYBja4G', 'admin@gmail.com', 'ROLE_ADMIN');  -- password: 123456

-- Связываем пользователей с ролями
INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),    -- user: ROLE_USER
    (2, 2);    -- admin: ROLE_ADMIN

-- Добавляем задачи
INSERT INTO tasks (title, description, status, priority, author_id, assignee_id)
VALUES
    ('Task 1', 'Description of task 1', 'Pending', 'High', 2, 1),
    ('Task 2', 'Description of task 2', 'In Progress', 'Medium', 2, 1);

-- Добавляем комментарии
INSERT INTO comments (content, task_id, user_id)
VALUES
    ('This is a comment for Task 1', 1, 1),
    ('Another comment for Task 1', 1, 2),
    ('Comment for Task 2', 2, 1);
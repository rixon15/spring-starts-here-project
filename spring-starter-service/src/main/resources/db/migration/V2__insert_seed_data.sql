-- 1. Create standard Roles
-- (Assuming your app logic checks for ROLE_USER or ROLE_ADMIN)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');

-- 2. Create the User
-- The password hash below corresponds to the plaintext: "password"
INSERT INTO users (id, username, email, password, created_at, updated_at)
VALUES (1,
        'demo_user',
        'demo@example.com',
        '$2a$10$eAccJf9WUj/MCD.K8s5VIube.ogjG1.wMpt2.3eLg7TqQ7.6rT3rO',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- 3. Assign Role to User
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);

-- 4. Create Project 1: "Website Redesign"
INSERT INTO projects (id, name, description, owner_id, created_at, updated_at)
VALUES (1,
        'Website Redesign',
        'Overhaul the company landing page with React',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- 5. Create Tasks for Project 1
INSERT INTO tasks (id, project_id, assignee_id, title, status, due_date, created_at, updated_at)
VALUES
    (1, 1, 1, 'Design Figma Mockups', 'DONE', CURRENT_TIMESTAMP - INTERVAL '1 DAY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 1, 'Implement Frontend Component', 'IN_PROGRESS', CURRENT_TIMESTAMP + INTERVAL '3 DAYS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. Create Project 2: "Mobile App"
INSERT INTO projects (id, name, description, owner_id, created_at, updated_at)
VALUES (2,
        'Mobile App',
        'MVP for the new iOS tracking application',
        1,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- 7. Create Tasks for Project 2
INSERT INTO tasks (id, project_id, assignee_id, title, status, due_date, created_at, updated_at)
VALUES
    (3, 2, 1, 'Setup React Native Environment', 'TODO', CURRENT_TIMESTAMP + INTERVAL '7 DAYS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 2, 1, 'Configure Firebase Auth', 'TODO', CURRENT_TIMESTAMP + INTERVAL '10 DAYS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. CRITICAL: Reset the identity sequences
-- Since we manually inserted IDs (1, 2, etc.), we must tell the database
-- to start counting from the next available number (e.g., 3 or 5)
-- to avoid "Unique index or primary key violation" errors later.

SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('projects_id_seq', (SELECT MAX(id) FROM projects));
SELECT setval('tasks_id_seq', (SELECT MAX(id) FROM tasks));
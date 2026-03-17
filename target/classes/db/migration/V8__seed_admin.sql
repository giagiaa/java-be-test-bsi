-- V8: Seed Admin User (password: admin123)
INSERT INTO users (id, username, email, password, enabled) 
VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@concert.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-0000-0000-000000000001', id FROM roles WHERE name = 'ROLE_ADMIN';

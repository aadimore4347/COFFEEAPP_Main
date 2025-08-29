-- H2-compatible Development Data
-- This script populates the database with test data.
-- It should be run after schema-dev.sql.

-- Password for all users is 'password' (bcrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6)

INSERT INTO facility (id, name, location, is_active, created_at, updated_at) VALUES
(1, 'Downtown Office', '123 Main St, Downtown Financial District', true, '2024-01-15 08:00:00', '2024-01-15 08:00:00'),
(2, 'Tech Campus', '456 Innovation Blvd, Silicon Valley Tech District', true, '2024-01-20 09:30:00', '2024-01-20 09:30:00'),
(3, 'Branch Office', '789 Corporate Ave, Business Park West', true, '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(4, 'Research Center', '321 Science Dr, University District', true, '2024-02-10 11:00:00', '2024-02-10 11:00:00'),
(5, 'Remote Hub', '555 Coworking Plaza, Suburban Center', true, '2024-02-15 14:30:00', '2024-02-15 14:30:00');

INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(1, 'downtown_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-01-15 08:30:00', '2024-01-15 08:30:00'),
(2, 'tech_supervisor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 2, true, '2024-01-20 10:00:00', '2024-01-20 10:00:00'),
(3, 'branch_coordinator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 3, true, '2024-02-01 10:45:00', '2024-02-01 10:45:00'),
(4, 'research_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 4, true, '2024-02-10 11:30:00', '2024-02-10 11:30:00'),
(5, 'hub_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 5, true, '2024-02-15 15:00:00', '2024-02-15 15:00:00'),
(6, 'facilities_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-02-20 09:00:00', '2024-02-20 09:00:00'),
(7, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(8, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(9, 'system_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-12 09:00:00', '2024-01-12 09:00:00');

INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(1, 1, 'ON', 92.5, 85, 75, 90, true, '2024-01-15 08:45:00', '2024-01-15 08:50:00'),
(2, 1, 'ON', 91.0, 18, 15, 65, true, '2024-01-15 09:00:00', '2024-01-15 09:03:00'),
(3, 2, 'OFF', 25.0, 95, 85, 80, true, '2024-01-20 10:30:00', '2024-01-20 10:45:00'),
(4, 2, 'ERROR', 88.0, 12, 8, 25, true, '2024-01-20 10:45:00', '2024-01-20 10:47:00'),
(5, 2, 'ON', 93.2, 70, 60, 55, true, '2024-01-20 11:00:00', '2024-01-20 11:01:00'),
(6, 2, 'ON', 90.8, 45, 52, 78, true, '2024-01-20 11:15:00', '2024-01-20 11:08:00'),
(7, 3, 'ON', 91.5, 65, 45, 72, true, '2024-02-01 11:00:00', '2024-02-01 11:10:00'),
(8, 3, 'ON', 92.8, 89, 82, 88, true, '2024-02-01 11:30:00', '2024-02-01 11:36:00'),
(9, 4, 'ON', 93.0, 78, 68, 82, true, '2024-02-10 12:00:00', '2024-02-10 12:04:00'),
(10, 4, 'ERROR', 85.5, 25, 15, 35, true, '2024-02-10 12:15:00', '2024-02-10 12:07:00'),
(11, 4, 'ON', 91.8, 92, 88, 95, true, '2024-02-10 12:30:00', '2024-02-10 12:28:00'),
(12, 5, 'ON', 92.2, 55, 38, 62, true, '2024-02-15 15:30:00', '2024-02-15 15:18:00'),
(13, 5, 'OFF', 22.0, 88, 92, 85, true, '2024-02-15 16:00:00', '2024-02-15 15:40:00');

INSERT INTO usage_history (id, machine_id, timestamp, brew_type, volume_ml, temp_at_brew, is_active, created_at, updated_at) VALUES
(1, 1, '2024-03-10 08:00:00', 'ESPRESSO', 30, 92.5, true, '2024-03-10 08:00:00', '2024-03-10 08:00:00'),
(2, 1, '2024-03-10 08:15:00', 'CAPPUCCINO', 150, 91.8, true, '2024-03-10 08:15:00', '2024-03-10 08:15:00'),
(3, 2, '2024-03-10 08:45:00', 'LATTE', 200, 91.2, true, '2024-03-10 08:45:00', '2024-03-10 08:45:00'),
(4, 2, '2024-03-10 08:30:00', 'AMERICANO', 250, 91.0, true, '2024-03-10 08:30:00', '2024-03-10 08:30:00'),
(5, 5, '2024-03-10 09:00:00', 'ESPRESSO', 30, 93.0, true, '2024-03-10 09:00:00', '2024-03-10 09:00:00'),
(6, 5, '2024-03-10 09:10:00', 'ESPRESSO', 30, 93.2, true, '2024-03-10 09:10:00', '2024-03-10 09:10:00'),
(7, 6, '2024-03-10 09:30:00', 'CAPPUCCINO', 150, 90.8, true, '2024-03-10 09:30:00', '2024-03-10 09:30:00'),
(8, 6, '2024-03-10 09:45:00', 'LATTE', 200, 91.0, true, '2024-03-10 09:45:00', '2024-03-10 09:45:00'),
(9, 7, '2024-03-10 12:00:00', 'AMERICANO', 250, 91.5, true, '2024-03-10 12:00:00', '2024-03-10 12:00:00'),
(10, 8, '2024-03-10 12:20:00', 'FLAT_WHITE', 160, 92.8, true, '2024-03-10 12:20:00', '2024-03-10 12:20:00'),
(11, 9, '2024-03-10 13:00:00', 'ESPRESSO', 30, 93.0, true, '2024-03-10 13:00:00', '2024-03-10 13:00:00'),
(12, 11, '2024-03-10 13:30:00', 'MOCHA', 200, 91.8, true, '2024-03-10 13:30:00', '2024-03-10 13:30:00'),
(13, 12, '2024-03-10 11:00:00', 'CAPPUCCINO', 150, 92.2, true, '2024-03-10 11:00:00', '2024-03-10 11:00:00'),
(14, 1, '2024-03-09 10:00:00', 'ESPRESSO', 30, 92.0, true, '2024-03-09 10:00:00', '2024-03-09 10:00:00'),
(15, 1, '2024-03-08 10:00:00', 'CAPPUCCINO', 150, 91.5, true, '2024-03-08 10:00:00', '2024-03-08 10:00:00'),
(16, 5, '2024-03-09 11:00:00', 'LATTE', 200, 93.1, true, '2024-03-09 11:00:00', '2024-03-09 11:00:00'),
(17, 5, '2024-03-08 11:00:00', 'AMERICANO', 250, 92.8, true, '2024-03-08 11:00:00', '2024-03-08 11:00:00'),
(18, 9, DATEADD('DAY', -3, NOW()), 'MACCHIATO', 40, 92.5, true, DATEADD('DAY', -3, NOW()), DATEADD('DAY', -3, NOW())),
(19, 2, DATEADD('DAY', -3, NOW()), 'DOUBLE_ESPRESSO', 60, 91.8, true, DATEADD('DAY', -3, NOW()), DATEADD('DAY', -3, NOW())),
(20, 6, DATEADD('DAY', -4, NOW()), 'FLAT_WHITE', 160, 90.9, true, DATEADD('DAY', -4, NOW()), DATEADD('DAY', -4, NOW())),
(21, 7, DATEADD('DAY', -5, NOW()), 'MOCHA', 200, 91.7, true, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -5, NOW())),
(22, 8, DATEADD('DAY', -6, NOW()), 'ESPRESSO', 30, 92.9, true, DATEADD('DAY', -6, NOW()), DATEADD('DAY', -6, NOW())),
(23, 12, DATEADD('DAY', -7, NOW()), 'CAPPUCCINO', 150, 92.1, true, DATEADD('DAY', -7, NOW()), DATEADD('DAY', -7, NOW()));

INSERT INTO alert (id, machine_id, type, severity, message, threshold_value, resolved, is_active, created_at, updated_at) VALUES
(1, 4, 'MALFUNCTION', 'CRITICAL', 'Machine has encountered a critical error and requires immediate maintenance', NULL, false, true, DATEADD('HOUR', -2, NOW()), DATEADD('HOUR', -2, NOW())),
(2, 10, 'MALFUNCTION', 'CRITICAL', 'Machine status shows ERROR - system malfunction detected', NULL, false, true, DATEADD('HOUR', -1, NOW()), DATEADD('HOUR', -1, NOW())),
(3, 4, 'LOW_WATER', 'WARNING', 'Water level has dropped below 20% threshold', 12, false, true, DATEADD('HOUR', -3, NOW()), DATEADD('HOUR', -3, NOW())),
(4, 4, 'LOW_MILK', 'WARNING', 'Milk level has dropped below 20% threshold', 8, false, true, DATEADD('HOUR', -3, NOW()), DATEADD('HOUR', -3, NOW())),
(5, 4, 'LOW_BEANS', 'CRITICAL', 'Coffee beans level critically low - immediate refill required', 25, false, true, DATEADD('MINUTE', 30, DATEADD('HOUR', -2, NOW())), DATEADD('MINUTE', 30, DATEADD('HOUR', -2, NOW()))),
(6, 2, 'LOW_WATER', 'WARNING', 'Water level has dropped below 20% threshold', 18, false, true, DATEADD('HOUR', -4, NOW()), DATEADD('HOUR', -4, NOW())),
(7, 2, 'LOW_MILK', 'CRITICAL', 'Milk level critically low at 15%', 15, false, true, DATEADD('MINUTE', 15, DATEADD('HOUR', -3, NOW())), DATEADD('MINUTE', 15, DATEADD('HOUR', -3, NOW()))),
(8, 10, 'LOW_WATER', 'WARNING', 'Water level has dropped below 30% threshold', 25, false, true, DATEADD('MINUTE', 30, DATEADD('HOUR', -1, NOW())), DATEADD('MINUTE', 30, DATEADD('HOUR', -1, NOW()))),
(9, 10, 'LOW_MILK', 'CRITICAL', 'Milk supply critically low at 15%', 15, false, true, DATEADD('MINUTE', 15, DATEADD('HOUR', -1, NOW())), DATEADD('MINUTE', 15, DATEADD('HOUR', -1, NOW()))),
(10, 1, 'LOW_BEANS', 'WARNING', 'Coffee beans level below threshold - resolved after refill', 55, true, true, DATEADD('DAY', -2, NOW()), DATEADD('DAY', -1, NOW())),
(11, 5, 'LOW_WATER', 'INFO', 'Water level notification - resolved', 70, true, true, DATEADD('DAY', -3, NOW()), DATEADD('DAY', -2, NOW())),
(12, 7, 'LOW_MILK', 'WARNING', 'Milk level below threshold - refilled', 45, true, true, DATEADD('DAY', -5, NOW()), DATEADD('DAY', -4, NOW())),
(13, 12, 'LOW_MILK', 'WARNING', 'Milk level was below threshold but has been refilled', 38, true, true, DATEADD('DAY', -1, NOW()), DATEADD('HOUR', -12, NOW())),
(14, 9, 'LOW_WATER', 'INFO', 'Water level notification - maintenance completed', 78, true, true, DATEADD('HOUR', -6, NOW()), DATEADD('HOUR', -4, NOW()));

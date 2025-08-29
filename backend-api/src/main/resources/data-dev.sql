-- Development seed data for Coffee Machine Monitoring System
-- This file is loaded only in the 'dev' profile
-- Password: 'password' (bcrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6)

-- Cleanup for idempotent seeding (delete children first due to FKs)
DELETE FROM alert;
DELETE FROM usage_history;
DELETE FROM coffee_machine;
DELETE FROM user;
DELETE FROM facility;

-- ======================================================================================
-- FACILITIES
-- ======================================================================================
INSERT INTO facility (id, name, location, is_active, created_at, updated_at) VALUES
(1, 'Downtown Office', '123 Main St, Downtown Financial District', true, '2024-01-15 08:00:00', '2024-01-15 08:00:00'),
(2, 'Tech Campus', '456 Innovation Blvd, Silicon Valley Tech District', true, '2024-01-20 09:30:00', '2024-01-20 09:30:00'),
(3, 'Branch Office', '789 Corporate Ave, Business Park West', true, '2024-02-01 10:15:00', '2024-02-01 10:15:00'),
(4, 'Research Center', '321 Science Dr, University District', true, '2024-02-10 11:00:00', '2024-02-10 11:00:00'),
(5, 'Remote Hub', '555 Coworking Plaza, Suburban Center', true, '2024-02-15 14:30:00', '2024-02-15 14:30:00');

-- ======================================================================================
-- USERS (All with password: 'password')
-- ======================================================================================

-- FACILITY Users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(1, 'downtown_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-01-15 08:30:00', '2024-01-15 08:30:00'),
(2, 'tech_supervisor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 2, true, '2024-01-20 10:00:00', '2024-01-20 10:00:00'),
(3, 'branch_coordinator', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 3, true, '2024-02-01 10:45:00', '2024-02-01 10:45:00'),
(4, 'research_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 4, true, '2024-02-10 11:30:00', '2024-02-10 11:30:00'),
(5, 'hub_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 5, true, '2024-02-15 15:00:00', '2024-02-15 15:00:00'),
(6, 'facilities_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'FACILITY', 1, true, '2024-02-20 09:00:00', '2024-02-20 09:00:00');

-- ADMIN Users
INSERT INTO user (id, username, password_hash, role, facility_id, is_active, created_at, updated_at) VALUES
(7, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(8, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-10 08:00:00', '2024-01-10 08:00:00'),
(9, 'system_admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKcnw.tilAYKBcQMZ5m0s1T6', 'ADMIN', NULL, true, '2024-01-12 09:00:00', '2024-01-12 09:00:00');

-- ======================================================================================
-- COFFEE MACHINES (Diverse scenarios for testing)
-- ======================================================================================

-- Downtown Office Machines (2 machines)
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(1, 1, 'ON', 92.5, 85, 75, 90, true, '2024-01-15 08:45:00', '2024-01-15 08:50:00'),
(2, 1, 'ON', 91.0, 18, 15, 65, true, '2024-01-15 09:00:00', '2024-01-15 09:03:00');

-- Tech Campus Machines (4 machines - high volume location)
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(3, 2, 'OFF', 25.0, 95, 85, 80, true, '2024-01-20 10:30:00', '2024-01-20 10:45:00'),
(4, 2, 'ERROR', 88.0, 12, 8, 25, true, '2024-01-20 10:45:00', '2024-01-20 10:47:00'),
(5, 2, 'ON', 93.2, 70, 60, 55, true, '2024-01-20 11:00:00', '2024-01-20 11:01:00'),
(6, 2, 'ON', 90.8, 45, 52, 78, true, '2024-01-20 11:15:00', '2024-01-20 11:08:00');

-- Branch Office Machines (2 machines)
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(7, 3, 'ON', 91.5, 65, 45, 72, true, '2024-02-01 11:00:00', '2024-02-01 11:10:00'),
(8, 3, 'ON', 92.8, 89, 82, 88, true, '2024-02-01 11:30:00', '2024-02-01 11:36:00');

-- Research Center Machines (3 machines)
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(9, 4, 'ON', 93.0, 78, 68, 82, true, '2024-02-10 12:00:00', '2024-02-10 12:04:00'),
(10, 4, 'ERROR', 85.5, 25, 15, 35, true, '2024-02-10 12:15:00', '2024-02-10 12:07:00'),
(11, 4, 'ON', 91.8, 92, 88, 95, true, '2024-02-10 12:30:00', '2024-02-10 12:28:00');

-- Remote Hub Machines (2 machines)
INSERT INTO coffee_machine (id, facility_id, status, temperature, water_level, milk_level, beans_level, is_active, created_at, updated_at) VALUES
(12, 5, 'ON', 92.2, 55, 38, 62, true, '2024-02-15 15:30:00', '2024-02-15 15:18:00'),
(13, 5, 'OFF', 22.0, 88, 92, 85, true, '2024-02-15 16:00:00', '2024-02-15 15:40:00');

-- ======================================================================================
-- USAGE HISTORY (Realistic usage patterns)
-- ======================================================================================

-- Recent usage (last 24 hours) - Peak hours simulation
-- Note: Using MySQL/H2 compatible syntax for development. Production should use Flyway for schema management.
INSERT INTO usage_history (id, machine_id, timestamp, brew_type, volume_ml, temp_at_brew, is_active, created_at, updated_at) VALUES
-- Morning rush (sample times)
(1, 1, '2024-03-10 08:00:00', 'ESPRESSO', 30, 92.5, true, '2024-03-10 08:00:00', '2024-03-10 08:00:00'),
(2, 1, '2024-03-10 08:15:00', 'CAPPUCCINO', 150, 91.8, true, '2024-03-10 08:15:00', '2024-03-10 08:15:00'),
(3, 2, '2024-03-10 08:45:00', 'LATTE', 200, 91.2, true, '2024-03-10 08:45:00', '2024-03-10 08:45:00'),
(4, 2, '2024-03-10 08:30:00', 'AMERICANO', 250, 91.0, true, '2024-03-10 08:30:00', '2024-03-10 08:30:00'),

-- Tech Campus - High volume
(5, 5, '2024-03-10 09:00:00', 'ESPRESSO', 30, 93.0, true, '2024-03-10 09:00:00', '2024-03-10 09:00:00'),
(6, 5, '2024-03-10 09:10:00', 'ESPRESSO', 30, 93.2, true, '2024-03-10 09:10:00', '2024-03-10 09:10:00'),
(7, 6, '2024-03-10 09:30:00', 'CAPPUCCINO', 150, 90.8, true, '2024-03-10 09:30:00', '2024-03-10 09:30:00'),
(8, 6, '2024-03-10 09:45:00', 'LATTE', 200, 91.0, true, '2024-03-10 09:45:00', '2024-03-10 09:45:00'),

-- Lunch time (12-2 PM)
(9, 7, '2024-03-10 12:00:00', 'AMERICANO', 250, 91.5, true, '2024-03-10 12:00:00', '2024-03-10 12:00:00'),
(10, 8, '2024-03-10 12:20:00', 'FLAT_WHITE', 160, 92.8, true, '2024-03-10 12:20:00', '2024-03-10 12:20:00'),

-- Research Center - Steady usage
(11, 9, '2024-03-10 13:00:00', 'ESPRESSO', 30, 93.0, true, '2024-03-10 13:00:00', '2024-03-10 13:00:00'),
(12, 11, '2024-03-10 13:30:00', 'MOCHA', 200, 91.8, true, '2024-03-10 13:30:00', '2024-03-10 13:30:00'),

-- Remote Hub
(13, 12, '2024-03-10 11:00:00', 'CAPPUCCINO', 150, 92.2, true, '2024-03-10 11:00:00', '2024-03-10 11:00:00'),

-- Historical usage (last week) for analytics
(14, 1, '2024-03-09 10:00:00', 'ESPRESSO', 30, 92.0, true, '2024-03-09 10:00:00', '2024-03-09 10:00:00'),
(15, 1, '2024-03-08 10:00:00', 'CAPPUCCINO', 150, 91.5, true, '2024-03-08 10:00:00', '2024-03-08 10:00:00'),
(16, 5, '2024-03-09 11:00:00', 'LATTE', 200, 93.1, true, '2024-03-09 11:00:00', '2024-03-09 11:00:00'),
(17, 5, '2024-03-08 11:00:00', 'AMERICANO', 250, 92.8, true, '2024-03-08 11:00:00', '2024-03-08 11:00:00'),
(18, 9, NOW() - INTERVAL 3 DAY, 'MACCHIATO', 40, 92.5, true, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),

-- More historical data for trends
(19, 2, NOW() - INTERVAL 3 DAY, 'DOUBLE_ESPRESSO', 60, 91.8, true, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
(20, 6, NOW() - INTERVAL 4 DAY, 'FLAT_WHITE', 160, 90.9, true, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY),
(21, 7, NOW() - INTERVAL 5 DAY, 'MOCHA', 200, 91.7, true, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY),
(22, 8, NOW() - INTERVAL 6 DAY, 'ESPRESSO', 30, 92.9, true, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY),
(23, 12, NOW() - INTERVAL 7 DAY, 'CAPPUCCINO', 150, 92.1, true, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY);

-- ======================================================================================
-- ALERTS (Various scenarios for testing)
-- ======================================================================================

-- Critical unresolved alerts
INSERT INTO alert (id, machine_id, type, severity, message, threshold_value, resolved, is_active, created_at, updated_at) VALUES
(1, 4, 'MALFUNCTION', 'CRITICAL', 'Machine has encountered a critical error and requires immediate maintenance', NULL, false, true, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR),
(2, 10, 'MALFUNCTION', 'CRITICAL', 'Machine status shows ERROR - system malfunction detected', NULL, false, true, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),

-- Warning level supply alerts
(3, 4, 'LOW_WATER', 'WARNING', 'Water level has dropped below 20% threshold', 12, false, true, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
(4, 4, 'LOW_MILK', 'WARNING', 'Milk level has dropped below 20% threshold', 8, false, true, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
(5, 4, 'LOW_BEANS', 'CRITICAL', 'Coffee beans level critically low - immediate refill required', 25, false, true, NOW() - INTERVAL 2 HOUR + INTERVAL 30 MINUTE, NOW() - INTERVAL 2 HOUR + INTERVAL 30 MINUTE),

(6, 2, 'LOW_WATER', 'WARNING', 'Water level has dropped below 20% threshold', 18, false, true, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 4 HOUR),
(7, 2, 'LOW_MILK', 'CRITICAL', 'Milk level critically low at 15%', 15, false, true, NOW() - INTERVAL 3 HOUR + INTERVAL 15 MINUTE, NOW() - INTERVAL 3 HOUR + INTERVAL 15 MINUTE),

(8, 10, 'LOW_WATER', 'WARNING', 'Water level has dropped below 30% threshold', 25, false, true, NOW() - INTERVAL 1 HOUR + INTERVAL 30 MINUTE, NOW() - INTERVAL 1 HOUR + INTERVAL 30 MINUTE),
(9, 10, 'LOW_MILK', 'CRITICAL', 'Milk supply critically low at 15%', 15, false, true, NOW() - INTERVAL 1 HOUR + INTERVAL 15 MINUTE, NOW() - INTERVAL 1 HOUR + INTERVAL 15 MINUTE),

-- Some resolved alerts (for history)
(10, 1, 'LOW_BEANS', 'WARNING', 'Coffee beans level below threshold - resolved after refill', 55, true, true, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 1 DAY),
(11, 5, 'LOW_WATER', 'INFO', 'Water level notification - resolved', 70, true, true, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 2 DAY),
(12, 7, 'LOW_MILK', 'WARNING', 'Milk level below threshold - refilled', 45, true, true, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 4 DAY),

-- Recent resolved alerts
(13, 12, 'LOW_MILK', 'WARNING', 'Milk level was below threshold but has been refilled', 38, true, true, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 12 HOUR),
(14, 9, 'LOW_WATER', 'INFO', 'Water level notification - maintenance completed', 78, true, true, NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 4 HOUR);

-- ======================================================================================
-- SUMMARY OF TEST DATA
-- ======================================================================================
/*
This comprehensive seed data provides:

FACILITIES (5):
- Downtown Office (2 machines) - Mixed status, some low supplies
- Tech Campus (4 machines) - High volume, includes error machine
- Branch Office (2 machines) - Normal operations
- Research Center (3 machines) - One error machine with alerts
- Remote Hub (2 machines) - Mixed status

USERS (9):
- 6 Facility users (one facility has 2 users for testing)
- 3 Admin users
- All passwords are 'password'

MACHINES (13):
- 8 operational (ON status)
- 2 offline (OFF status) 
- 3 in error state (ERROR status)
- Various supply levels for testing thresholds
- Recent update timestamps for connectivity testing

USAGE HISTORY (23 records):
- Recent usage for current day analytics
- Historical data for trend analysis
- Peak hours simulation (morning/lunch)
- Various brew types for preference analytics
- Realistic volumes and temperatures

ALERTS (14):
- 9 unresolved alerts (2 critical, 6 warning, 1 info)
- 5 resolved alerts for history
- Mix of malfunction and supply alerts
- Different severities for testing prioritization
- Recent and historical alerts for trend analysis

This data enables comprehensive testing of:
- Role-based access control
- Real-time monitoring dashboards
- Alert management and escalation
- Usage analytics and reporting
- Supply level monitoring
- Machine status tracking
- Historical trend analysis
*/
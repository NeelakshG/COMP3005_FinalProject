
INSERT INTO Member (first_name, last_name, password, email, phone, gender) VALUES
('Aditya', 'Gupta', 'pass123', 'aditya@example.com', '111-222-3333', 'Male'),
('Sarah', 'Lee', 'pass123', 'sarah@example.com', '222-333-4444', 'Female'),
('Michael', 'Smith', 'pass123', 'mike@example.com', '333-444-5555', 'Male'),
('Emily', 'Wong', 'pass123', 'emily@example.com', '444-555-6666', 'Female');


INSERT INTO Trainer (name, email, specialization) VALUES
('John Trainer', 'john.trainer@example.com', 'Strength'),
('Lisa Coach', 'lisa.coach@example.com', 'Cardio'),
('Marcus PT', 'marcus.pt@example.com', 'Boxing');


INSERT INTO AdministrativeStaff (name, email) VALUES
('Admin One', 'admin1@example.com'),
('Admin Two', 'admin2@example.com');


INSERT INTO Room (name, capacity) VALUES
('Studio A', 20),
('Studio B', 25),
('Boxing Room', 15);




INSERT INTO FitnessGoal (member_id, target_weight, start_date, end_date, status, goal_type) VALUES
(1, 70.0, '2025-01-01', '2025-03-01', 'active', 'Weight Loss'),
(2, 55.0, '2025-01-15', '2025-04-15', 'active', 'Toning'),
(3, 85.0, '2025-02-01', '2025-05-01', 'completed', 'Muscle Gain');


INSERT INTO HealthMetrics (member_id, weight, heartrate, body_fat_percentage) VALUES
(1, 75.0, 72, 18.5),
(2, 60.0, 80, 22.0),
(3, 90.0, 78, 25.0),
(4, 68.0, 70, 19.5);


INSERT INTO ClassRegistration (member_id, class_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 4);


INSERT INTO traineravailability (trainer_id, date, start_time, end_time)
VALUES
(1, '2025-12-03', '06:00', '08:00'),
(1, '2025-12-03', '09:00', '12:00'),
(1, '2025-12-04', '08:00', '10:00'),
(1, '2025-12-04', '14:00', '18:00'),
(1, '2025-12-05', '06:00', '08:00'),
(1, '2025-12-05', '09:00', '22:00');

INSERT INTO class (name, trainer_id, room_id, date, start_time, end_time, capacity)
VALUES
('Morning Yoga', 1, 1, '2025-12-03', '06:00', '07:00', 1),
('HIIT',         1, 2, '2025-12-04', '14:00', '15:00', 1),
('Pilates',      1, 3, '2025-12-05', '06:00', '07:00', 1);


INSERT INTO ptsession (member_id, trainer_id, room_id, date, start_time, end_time)
VALUES
(1, 1, 1, '2025-12-03', '10:00', '11:00'),
(2, 1, 2, '2025-12-04', '08:00', '09:00'),
(3, 1, 3, '2025-12-05', '09:00', '10:00');


INSERT INTO Equipment (room_id, name, type, operational_status) VALUES
(1, 'Treadmill A', 'Cardio', 'Operational'),
(1, 'Treadmill B', 'Cardio', 'Operational'),
(2, 'Yoga Mats', 'Flexibility', 'Operational'),
(3, 'Boxing Gloves Set', 'Boxing', 'Needs Repair');


INSERT INTO MaintenanceLog (equipment_id, admin_id, issue_reported, resolve_date, log_status) VALUES
(4, 1, 'Tear in glove padding', NULL, 'Pending'),
(1, 2, 'Motor noise detected', '2025-12-02', 'Resolved');

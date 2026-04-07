INSERT INTO tasks (title, description, case_id, status, priority, assigned_to, due_date, created_at) VALUES
('Initial investigation review', 'Review all initial evidence and documents for the case', 1, 'PENDING', 'HIGH', 'detective_smith', '2026-05-01 00:00:00', '2026-04-01 09:00:00'),
('Interview main witness', 'Conduct detailed interview with the primary witness', 1, 'IN_PROGRESS', 'HIGH', 'detective_jones', '2026-04-15 00:00:00', '2026-04-01 10:00:00'),
('Collect forensic evidence', 'Gather and catalog all forensic evidence from the scene', 2, 'PENDING', 'MEDIUM', 'detective_smith', '2026-04-20 00:00:00', '2026-04-02 09:00:00'),
('Write investigation report', 'Compile findings into official investigation report', 2, 'PENDING', 'LOW', NULL, '2026-05-15 00:00:00', '2026-04-02 10:00:00'),
('Coordinate with forensics lab', 'Submit samples to forensics lab and track results', 3, 'COMPLETED', 'MEDIUM', 'detective_jones', '2026-04-10 00:00:00', '2026-04-03 09:00:00');

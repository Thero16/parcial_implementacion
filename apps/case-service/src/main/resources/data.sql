-- Clean table and reset identity
TRUNCATE TABLE cases RESTART IDENTITY CASCADE;

-- Insert mock cases
INSERT INTO cases (
    title,
    description,
    status,
    priority,
    assigned_detective,
    created_at
)
VALUES
    (
        'Murder at Aris Mansion',
        'Investigation of the death of S.A. found hanging in his study with golden ropes.',
        'OPEN',
        'HIGH',
        'Detective Holmes',
        '2026-03-20 09:30:00'
    ),
    (
        'Golden Bathtub Crime Scene',
        'M.G. was discovered inside a bathtub filled with gold coins and compromising photographs nearby.',
        'IN_PROGRESS',
        'HIGH',
        'Detective Marple',
        '2026-03-18 11:20:00'
    ),
    (
        'Disappearance at Cuba Lagoon',
        'Case involving Isis the professor, last seen aboard a luxury yacht in Cuba Lagoon.',
        'OPEN',
        'MEDIUM',
        'Detective Poirot',
        '2026-03-22 14:15:00'
    ),
    (
        'Gonzaga Mansion Robbery',
        'Luxury mansion robbery connected to the serial killer investigation.',
        'CLOSED',
        'LOW',
        'Detective Watson',
        '2026-03-10 08:00:00'
    ),
    (
        'Secret Tunnel Discovery',
        'A hidden tunnel connecting the Aris and Bonet mansions was discovered during the investigation.',
        'IN_PROGRESS',
        'HIGH',
        'Detective Holmes',
        '2026-03-31 15:25:00'
    );
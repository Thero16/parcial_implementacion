-- Clean table and reset identity
TRUNCATE TABLE people RESTART IDENTITY CASCADE;

-- Insert 10 mock people
INSERT INTO people (
    full_name,
    role,
    age,
    description,
    case_id
)
VALUES
    (
        'Sebastian Aris',
        'VICTIM',
        58,
        'Owner of Aris Mansion. Found dead in his private study under suspicious circumstances.',
        1
    ),
    (
        'Laura Aris',
        'SUSPECT',
        34,
        'Wife of the victim. Reportedly argued with him the night before the murder.',
        1
    ),
    (
        'Charles Bennett',
        'WITNESS',
        47,
        'Family butler who discovered the body and alerted the authorities.',
        1
    ),
    (
        'Arthur Holmes',
        'DETECTIVE',
        50,
        'Lead detective assigned to the Aris Mansion investigation.',
        1
    ),
    (
        'Marina Gold',
        'VICTIM',
        41,
        'Found deceased inside the golden bathtub in her residence.',
        2
    ),
    (
        'Victor Gold',
        'SUSPECT',
        45,
        'Business partner of the victim with possible financial motives.',
        2
    ),
    (
        'Angela Rivera',
        'WITNESS',
        29,
        'Housemaid who reported hearing screams before the incident.',
        2
    ),
    (
        'Emily Carter',
        'ANALYST',
        37,
        'Forensic analyst reviewing fingerprints and photographic evidence.',
        2
    ),
    (
        'Professor Isis Montenegro',
        'VICTIM',
        52,
        'Professor reported missing after being last seen aboard a yacht near Cuba Lagoon.',
        3
    ),
    (
        'Daniel Cortez',
        'SUSPECT',
        38,
        'Captain of the yacht and the last known person to see the victim.',
        3
    );
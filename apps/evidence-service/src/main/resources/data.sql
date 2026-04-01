TRUNCATE TABLE evidence_custody_history RESTART IDENTITY CASCADE;
TRUNCATE TABLE evidences RESTART IDENTITY CASCADE;

INSERT INTO evidences (
    evidence_id,
    case_id,
    evidence_type,
    description,
    location_found,
    date_collected,
    collected_by,
    file_url,
    custody_status,
    current_custodian
) VALUES
      (
          1,
          1,
          'PHOTO',
          'Photograph of S.A. hanging from the ceiling with golden ropes inside the study.',
          'Aris Mansion Study',
          '2026-03-20 09:45:00',
          'Detective Holmes',
          'https://files.nomolestar.com/evidence/aris-study-photo.jpg',
          'STORED',
          'Detective Holmes'
      ),
      (
          2,
          1,
          'DOCUMENT',
          'Handwritten note found in the victim''s desk containing possible threats.',
          'Aris Mansion Desk',
          '2026-03-20 10:05:00',
          'Detective Holmes',
          'https://files.nomolestar.com/evidence/threat-note.pdf',
          'IN_ANALYSIS',
          'Forensic Lab'
      ),
      (
          3,
          2,
          'PHOTO',
          'Compromising photographs found next to the bathtub.',
          'Golden Bathroom',
          '2026-03-18 11:40:00',
          'Detective Marple',
          'https://files.nomolestar.com/evidence/bathroom-photos.zip',
          'TRANSFERRED',
          'Digital Forensics Team'
      ),
      (
          4,
          2,
          'DNA',
          'DNA sample collected from the edge of the bathtub.',
          'Golden Bathroom',
          '2026-03-18 12:00:00',
          'Forensic Technician Carla',
          'https://files.nomolestar.com/evidence/dna-sample.lab',
          'IN_ANALYSIS',
          'Forensic Biology Department'
      ),
      (
          5,
          3,
          'DOCUMENT',
          'Eclipse project files recovered from the yacht laptop.',
          'Luxury Yacht Cabin',
          '2026-03-22 15:00:00',
          'Detective Poirot',
          'https://files.nomolestar.com/evidence/eclipse-project.zip',
          'STORED',
          'Cybercrime Unit'
      ),
      (
          6,
          3,
          'VIDEO',
          'Security camera footage from the marina entrance at Cuba Lagoon.',
          'Cuba Lagoon Marina',
          '2026-03-22 15:30:00',
          'Detective Poirot',
          'https://files.nomolestar.com/evidence/marina-footage.mp4',
          'COLLECTED',
          'Detective Poirot'
      ),
      (
          7,
          4,
          'WEAPON',
          'Silver knife recovered behind the fireplace in Gonzaga Mansion.',
          'Gonzaga Mansion Living Room',
          '2026-03-10 08:30:00',
          'Detective Watson',
          'https://files.nomolestar.com/evidence/silver-knife.jpg',
          'IN_ANALYSIS',
          'Weapons Lab'
      ),
      (
          8,
          5,
          'DOCUMENT',
          'Blueprint of the hidden tunnel connecting the Aris and Bonet mansions.',
          'Hidden Tunnel Entrance',
          '2026-03-31 16:00:00',
          'Detective Holmes',
          'https://files.nomolestar.com/evidence/tunnel-blueprint.pdf',
          'STORED',
          'Detective Holmes'
      ),
      (
          9,
          5,
          'FINGERPRINT',
          'Fingerprint recovered from the tunnel entrance door handle.',
          'Hidden Tunnel Entrance',
          '2026-03-31 16:20:00',
          'Forensic Technician Diego',
          'https://files.nomolestar.com/evidence/fingerprint-01.png',
          'IN_ANALYSIS',
          'Fingerprint Department'
      ),
      (
          10,
          5,
          'AUDIO',
          'Voice recording discovered in the tunnel mentioning the Aris family.',
          'Inside Hidden Tunnel',
          '2026-03-31 16:45:00',
          'Detective Holmes',
          'https://files.nomolestar.com/evidence/tunnel-recording.wav',
          'TRANSFERRED',
          'Audio Analysis Unit'
      );

INSERT INTO evidence_custody_history (
    history_id,
    evidence_id,
    previous_custodian,
    new_custodian,
    reason,
    transferred_at
) VALUES
      (1, 1, 'NONE', 'Detective Holmes', 'Initial custody assignment', '2026-03-20 09:45:00'),

      (2, 2, 'NONE', 'Detective Holmes', 'Initial custody assignment', '2026-03-20 10:05:00'),
      (3, 2, 'Detective Holmes', 'Forensic Lab', 'Sent for handwriting analysis', '2026-03-20 14:00:00'),

      (4, 3, 'NONE', 'Detective Marple', 'Initial custody assignment', '2026-03-18 11:40:00'),
      (5, 3, 'Detective Marple', 'Digital Forensics Team', 'Transferred for image and metadata analysis', '2026-03-18 17:15:00'),

      (6, 4, 'NONE', 'Forensic Technician Carla', 'Initial custody assignment', '2026-03-18 12:00:00'),
      (7, 4, 'Forensic Technician Carla', 'Forensic Biology Department', 'Transferred for DNA sequencing', '2026-03-18 18:20:00'),

      (8, 5, 'NONE', 'Cybercrime Unit', 'Initial custody assignment', '2026-03-22 15:00:00'),

      (9, 6, 'NONE', 'Detective Poirot', 'Initial custody assignment', '2026-03-22 15:30:00'),

      (10, 7, 'NONE', 'Detective Watson', 'Initial custody assignment', '2026-03-10 08:30:00'),
      (11, 7, 'Detective Watson', 'Weapons Lab', 'Transferred for trace and weapon analysis', '2026-03-10 12:10:00'),

      (12, 8, 'NONE', 'Detective Holmes', 'Initial custody assignment', '2026-03-31 16:00:00'),

      (13, 9, 'NONE', 'Forensic Technician Diego', 'Initial custody assignment', '2026-03-31 16:20:00'),
      (14, 9, 'Forensic Technician Diego', 'Fingerprint Department', 'Transferred for comparison against suspects', '2026-03-31 18:00:00'),

      (15, 10, 'NONE', 'Detective Holmes', 'Initial custody assignment', '2026-03-31 16:45:00'),
      (16, 10, 'Detective Holmes', 'Audio Analysis Unit', 'Transferred for voice enhancement and identification', '2026-03-31 19:10:00');

SELECT setval('evidences_evidence_id_seq', (SELECT MAX(evidence_id) FROM evidences));
SELECT setval('evidence_custody_history_history_id_seq', (SELECT MAX(history_id) FROM evidence_custody_history));
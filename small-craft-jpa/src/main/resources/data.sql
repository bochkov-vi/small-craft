SELECT setval('notification_seq',(SELECT COALESCE(max(id_notification),0)+1 FROM notification));
SELECT setval('exit_notification_seq',(SELECT COALESCE(max(id_exit_notification),0)+1 FROM exit_notification));
SELECT setval('unit_seq',(SELECT COALESCE(max(id_unit),0)+1 FROM unit));
SELECT setval('boat_seq',(SELECT COALESCE(max(id_boat),0)+1 FROM boat));
SELECT setval('person_seq',(SELECT COALESCE(max(id_person),0)+1 FROM person));
SELECT setval('legal_person_seq',(SELECT COALESCE(max(id_legal_person),0)+1 FROM legal_person));

UPDATE notification_number_seq as s SET number = (SELECT COALESCE(MAX(number) + 1, 1) FROM notification WHERE notification.year = s.year);
UPDATE boat_number_seq SET number = (SELECT COALESCE(MAX(b.registration_number) + 1, 1) FROM boat b) WHERE id = 0;




ALTER SEQUENCE boat_seq RESTART WITH (SELECT COALESCE(max(id_boat),1) FROM boat);
UPDATE notification_number_seq as s SET number = (SELECT COALESCE(MAX(number) , 1) FROM notification WHERE notification.year = s.year);
UPDATE boat_number_seq SET number = (SELECT COALESCE(MAX(b.registration_number), 1) FROM boat b) WHERE id = 0;

select boat_seq.last_value from boat_seq


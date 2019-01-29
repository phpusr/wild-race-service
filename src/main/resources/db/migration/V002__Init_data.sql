-- Default config
INSERT INTO config VALUES (1, '', false, 0, '', false, false, -1, false);

-- Default temp_data
INSERT INTO temp_data VALUES (1, '2019-01-01 00:00:01');

-- Default user(username: phpusr, password: 1, role: admin)
insert into user_table values (1, false, false, true, '$2a$08$Pq0wY2TbwouY3hAIgu8/FeePgnWw0L1Mi3cKlKEt35g4mcV6MHouy', false, 'phpusr');
insert into user_role values (1, 0);
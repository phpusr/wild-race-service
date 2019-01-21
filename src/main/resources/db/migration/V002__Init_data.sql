-- Default config
INSERT INTO config VALUES (1, '', false, 0, '', false, false, -1, false);

-- Default temp_data
INSERT INTO temp_data VALUES (1, '2019-01-01 00:00:01');

-- Default user
-- create extension if not exists pgcrypto;
insert into user_table values (1, false, false, true, '1', false, 'phpusr');
update user_table set password = crypt(password, gen_salt('bf', 8));
insert into user_role values (1, 0);
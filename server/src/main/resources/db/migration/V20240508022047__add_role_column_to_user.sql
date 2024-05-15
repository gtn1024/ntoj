ALTER TABLE t_users
    ADD role VARCHAR(255);

UPDATE t_users
    SET role = 'root'
    WHERE user_role > 1;

UPDATE t_users
    SET role = 'default'
    WHERE user_role = 1;

UPDATE t_users
    SET role = 'banned'
    WHERE user_role = 0;

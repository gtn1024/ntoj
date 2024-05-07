ALTER TABLE t_users
    ADD display_name VARCHAR(255);

UPDATE t_users
    SET display_name = real_name;

ALTER TABLE t_users
    DROP COLUMN real_name;

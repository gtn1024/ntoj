ALTER TABLE t_groups_users
    DROP CONSTRAINT fk_tgrouse_on_group;

ALTER TABLE t_groups_users
    DROP CONSTRAINT fk_tgrouse_on_user;

ALTER TABLE t_groups_users
    ADD group_id BIGINT;

ALTER TABLE t_groups_users
    ADD user_id BIGINT;

-- Copy data from old columns to new columns
UPDATE t_groups_users
    SET group_id = t_groups_group_id;

UPDATE t_groups_users
    SET user_id = users_user_id;

ALTER TABLE t_groups_users
    ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE t_groups_users
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE t_groups_users
    ADD CONSTRAINT fk_tgrouse_on_group FOREIGN KEY (group_id) REFERENCES t_groups (group_id);

ALTER TABLE t_groups_users
    ADD CONSTRAINT fk_tgrouse_on_user FOREIGN KEY (user_id) REFERENCES t_users (user_id);

ALTER TABLE t_groups_users
    DROP COLUMN t_groups_group_id;

ALTER TABLE t_groups_users
    DROP COLUMN users_user_id;

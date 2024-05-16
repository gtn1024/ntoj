ALTER TABLE t_groups
    ADD creator_user_id BIGINT;

UPDATE t_groups
SET creator_user_id = (SELECT user_id
                       FROM t_users
                       WHERE role = 'root'
                       LIMIT 1);

ALTER TABLE t_groups
    ALTER COLUMN creator_user_id SET NOT NULL;

ALTER TABLE t_groups
    ADD CONSTRAINT FK_T_GROUPS_ON_CREATOR_USER FOREIGN KEY (creator_user_id) REFERENCES t_users (user_id);

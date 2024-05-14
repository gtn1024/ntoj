ALTER TABLE t_homeworks
    ADD author_user_id BIGINT;

UPDATE t_homeworks
SET author_user_id = (SELECT user_id
                      FROM t_users
                      WHERE role = 'root'
                      LIMIT 1);

ALTER TABLE t_homeworks
    ALTER COLUMN author_user_id SET NOT NULL;

ALTER TABLE t_homeworks
    ADD CONSTRAINT FK_T_HOMEWORKS_ON_AUTHOR_USER FOREIGN KEY (author_user_id) REFERENCES t_users (user_id);

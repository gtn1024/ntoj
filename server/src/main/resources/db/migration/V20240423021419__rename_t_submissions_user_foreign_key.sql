ALTER TABLE t_submissions
    DROP CONSTRAINT fk_t_submissions_on_user_user;

ALTER TABLE t_submissions
    ADD user_id BIGINT;

UPDATE t_submissions
    SET user_id = user_user_id;

ALTER TABLE t_submissions
    ADD CONSTRAINT FK_T_SUBMISSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES t_users (user_id);

ALTER TABLE t_submissions
    DROP COLUMN user_user_id;

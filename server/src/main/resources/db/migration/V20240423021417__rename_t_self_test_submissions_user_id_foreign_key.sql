ALTER TABLE t_self_test_submissions
    DROP CONSTRAINT fk_t_self_test_submissions_on_user_user;

ALTER TABLE t_self_test_submissions
    ADD user_id BIGINT;

UPDATE t_self_test_submissions
    SET user_id = user_user_id;

ALTER TABLE t_self_test_submissions
    ADD CONSTRAINT FK_T_SELF_TEST_SUBMISSIONS_ON_USER FOREIGN KEY (user_id) REFERENCES t_users (user_id);

ALTER TABLE t_self_test_submissions
    DROP COLUMN user_user_id;

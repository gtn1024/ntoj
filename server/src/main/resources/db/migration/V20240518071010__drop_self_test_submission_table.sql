ALTER TABLE t_self_test_submissions
    DROP CONSTRAINT fk_t_self_test_submissions_on_user;

DROP TABLE t_self_test_submissions CASCADE;

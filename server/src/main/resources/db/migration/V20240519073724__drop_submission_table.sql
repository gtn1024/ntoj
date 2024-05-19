ALTER TABLE t_submissions
    DROP CONSTRAINT fk_t_submissions_on_problem;

ALTER TABLE t_submissions
    DROP CONSTRAINT fk_t_submissions_on_user;

DROP TABLE t_submissions CASCADE;

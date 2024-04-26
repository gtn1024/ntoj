ALTER TABLE t_submissions
    DROP CONSTRAINT fk_t_submissions_on_problem_problem;

ALTER TABLE t_submissions
    ADD problem_id BIGINT;

UPDATE t_submissions
    SET problem_id = problem_problem_id;

ALTER TABLE t_submissions
    ADD CONSTRAINT FK_T_SUBMISSIONS_ON_PROBLEM FOREIGN KEY (problem_id) REFERENCES t_problems (problem_id);

ALTER TABLE t_submissions
    DROP COLUMN problem_problem_id;

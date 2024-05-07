ALTER TABLE t_submissions
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE t_submissions
    ALTER COLUMN problem_id SET NOT NULL;

ALTER TABLE t_submissions
    ALTER COLUMN user_id SET NOT NULL;

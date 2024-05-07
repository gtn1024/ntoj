ALTER TABLE t_problems
    ALTER COLUMN author_user_id SET NOT NULL;

ALTER TABLE t_problems
    ALTER COLUMN memory_limit SET NOT NULL;

ALTER TABLE t_problems
    ALTER COLUMN testcase_file_id SET NOT NULL;

ALTER TABLE t_problems
    ALTER COLUMN time_limit SET NOT NULL;

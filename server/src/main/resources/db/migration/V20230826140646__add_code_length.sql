ALTER TABLE t_problems
    ADD code_length INTEGER DEFAULT 16;

ALTER TABLE t_problems
    ALTER COLUMN code_length SET NOT NULL;

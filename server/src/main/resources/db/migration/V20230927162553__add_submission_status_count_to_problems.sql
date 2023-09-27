ALTER TABLE t_problems
    ADD accepted_times BIGINT NOT NULL DEFAULT 0;

ALTER TABLE t_problems
    ADD submit_times BIGINT NOT NULL DEFAULT 0;

ALTER TABLE t_contests
    ADD problems JSONB DEFAULT '[]' NOT NULL;

ALTER TABLE t_contests_problems
    DROP CONSTRAINT fk_tconpro_on_contest;

ALTER TABLE t_contests_problems
    DROP CONSTRAINT fk_tconpro_on_problem;

DROP TABLE t_contests_problems CASCADE;

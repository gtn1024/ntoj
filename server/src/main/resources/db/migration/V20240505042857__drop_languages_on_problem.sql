ALTER TABLE t_problems_languages
    DROP CONSTRAINT fk_tprolan_on_language;

ALTER TABLE t_problems_languages
    DROP CONSTRAINT fk_tprolan_on_problem;

DROP TABLE t_problems_languages CASCADE;

ALTER TABLE t_problems
    DROP COLUMN allow_all_languages;

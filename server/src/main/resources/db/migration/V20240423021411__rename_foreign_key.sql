-- operations for table t_contests_languages
ALTER TABLE t_contests_languages
    DROP CONSTRAINT fk_tconlan_on_contest;

ALTER TABLE t_contests_languages
    DROP CONSTRAINT fk_tconlan_on_language;

ALTER TABLE t_contests_languages
    ADD contest_id BIGINT;

ALTER TABLE t_contests_languages
    ADD language_id BIGINT;

UPDATE t_contests_languages
SET contest_id = t_contests_contest_id;

UPDATE t_contests_languages
SET language_id = languages_language_id;

ALTER TABLE t_contests_languages
    ALTER COLUMN contest_id SET NOT NULL;

ALTER TABLE t_contests_languages
    ALTER COLUMN language_id SET NOT NULL;

ALTER TABLE t_contests_languages
    ADD CONSTRAINT fk_tconlan_on_contest FOREIGN KEY (contest_id) REFERENCES t_contests (contest_id);

ALTER TABLE t_contests_languages
    ADD CONSTRAINT fk_tconlan_on_language FOREIGN KEY (language_id) REFERENCES t_languages (language_id);

ALTER TABLE t_contests_languages
    DROP COLUMN languages_language_id;

ALTER TABLE t_contests_languages
    DROP COLUMN t_contests_contest_id;

-- operations for table t_problems_languages
ALTER TABLE t_problems_languages
    DROP CONSTRAINT fk_tprolan_on_language;

ALTER TABLE t_problems_languages
    DROP CONSTRAINT fk_tprolan_on_problem;

ALTER TABLE t_problems_languages
    ADD language_id BIGINT;

ALTER TABLE t_problems_languages
    ADD problem_id BIGINT;

UPDATE t_problems_languages
SET language_id = languages_language_id;

UPDATE t_problems_languages
SET problem_id = t_problems_problem_id;

ALTER TABLE t_problems_languages
    ALTER COLUMN language_id SET NOT NULL;

ALTER TABLE t_problems_languages
    ALTER COLUMN problem_id SET NOT NULL;

ALTER TABLE t_problems_languages
    ADD CONSTRAINT fk_tprolan_on_language FOREIGN KEY (language_id) REFERENCES t_languages (language_id);

ALTER TABLE t_problems_languages
    ADD CONSTRAINT fk_tprolan_on_problem FOREIGN KEY (problem_id) REFERENCES t_problems (problem_id);

ALTER TABLE t_problems_languages
    DROP COLUMN languages_language_id;

ALTER TABLE t_problems_languages
    DROP COLUMN t_problems_problem_id;

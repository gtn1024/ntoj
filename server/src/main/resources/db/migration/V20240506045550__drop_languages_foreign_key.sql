ALTER TABLE t_self_test_submissions
    DROP CONSTRAINT fk_t_self_test_submissions_on_language;

ALTER TABLE t_submissions
    DROP CONSTRAINT fk_t_submissions_on_language;

ALTER TABLE t_contests_languages
    DROP CONSTRAINT fk_tconlan_on_contest;

ALTER TABLE t_contests_languages
    DROP CONSTRAINT fk_tconlan_on_language;

DROP TABLE t_contests_languages CASCADE;

ALTER TABLE t_contests
    DROP COLUMN allow_all_languages;

ALTER TABLE t_submissions
    DROP COLUMN language_id;

ALTER TABLE t_self_test_submissions
    DROP COLUMN language_id;

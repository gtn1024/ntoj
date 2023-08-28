CREATE TABLE t_contests_languages
(
    languages_language_id BIGINT NOT NULL,
    t_contests_contest_id BIGINT NOT NULL
);

ALTER TABLE t_contests
    ADD allow_all_languages BOOLEAN DEFAULT false;

ALTER TABLE t_contests
    ALTER COLUMN allow_all_languages SET NOT NULL;

ALTER TABLE t_contests_languages
    ADD CONSTRAINT fk_tconlan_on_contest FOREIGN KEY (t_contests_contest_id) REFERENCES t_contests (contest_id);

ALTER TABLE t_contests_languages
    ADD CONSTRAINT fk_tconlan_on_language FOREIGN KEY (languages_language_id) REFERENCES t_languages (language_id);

CREATE TABLE t_problems_languages
(
    languages_language_id BIGINT NOT NULL,
    t_problems_problem_id BIGINT NOT NULL
);

ALTER TABLE t_problems_languages
    ADD CONSTRAINT uc_t_problems_languages_languages_language UNIQUE (languages_language_id);

ALTER TABLE t_problems_languages
    ADD CONSTRAINT fk_tprolan_on_language FOREIGN KEY (languages_language_id) REFERENCES t_languages (language_id);

ALTER TABLE t_problems_languages
    ADD CONSTRAINT fk_tprolan_on_problem FOREIGN KEY (t_problems_problem_id) REFERENCES t_problems (problem_id);

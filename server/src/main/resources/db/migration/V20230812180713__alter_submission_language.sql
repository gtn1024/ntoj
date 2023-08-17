ALTER TABLE t_submissions
    ADD language_language_id BIGINT;

ALTER TABLE t_submissions
    ADD CONSTRAINT FK_T_SUBMISSIONS_ON_LANGUAGE_LANGUAGE FOREIGN KEY (language_language_id) REFERENCES t_languages (language_id);

ALTER TABLE t_submissions
    DROP COLUMN language;

ALTER TABLE t_self_test_submissions
    DROP CONSTRAINT fk_t_self_test_submissions_on_language_language;

ALTER TABLE t_self_test_submissions
    ADD language_id BIGINT;

UPDATE t_self_test_submissions
    SET language_id = language_language_id;

ALTER TABLE t_self_test_submissions
    ADD CONSTRAINT uc_t_self_test_submissions_language UNIQUE (language_id);

ALTER TABLE t_self_test_submissions
    ADD CONSTRAINT FK_T_SELF_TEST_SUBMISSIONS_ON_LANGUAGE FOREIGN KEY (language_id) REFERENCES t_languages (language_id);

ALTER TABLE t_self_test_submissions
    DROP COLUMN language_language_id;

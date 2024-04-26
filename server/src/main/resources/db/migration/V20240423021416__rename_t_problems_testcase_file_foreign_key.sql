ALTER TABLE t_problems
    DROP CONSTRAINT fk_t_problems_on_testcases_file;

ALTER TABLE t_problems
    ADD testcase_file_id BIGINT;

UPDATE t_problems
    SET testcase_file_id = test_cases_file_id;

ALTER TABLE t_problems
    ADD CONSTRAINT uc_t_problems_testcase_file UNIQUE (testcase_file_id);

ALTER TABLE t_problems
    ADD CONSTRAINT FK_T_PROBLEMS_ON_TESTCASE_FILE FOREIGN KEY (testcase_file_id) REFERENCES t_files (file_id);

ALTER TABLE t_problems
    DROP COLUMN test_cases_file_id;

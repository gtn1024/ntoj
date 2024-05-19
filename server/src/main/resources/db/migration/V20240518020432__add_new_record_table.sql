CREATE TABLE t_records
(
    record_id       VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    user_id         BIGINT       NOT NULL,
    problem_id      BIGINT,
    origin          VARCHAR(255) NOT NULL,
    contest_id      BIGINT,
    lang            VARCHAR(255) NOT NULL,
    self_test_input TEXT,
    code            TEXT         NOT NULL,
    status          VARCHAR(255) NOT NULL,
    stage           VARCHAR(255) NOT NULL,
    time            INTEGER,
    memory          INTEGER,
    judger_id       VARCHAR(255),
    compile_log     TEXT,
    testcase_result JSONB,
    CONSTRAINT pk_t_records PRIMARY KEY (record_id)
);

ALTER TABLE t_records
    ADD CONSTRAINT FK_T_RECORDS_ON_CONTEST FOREIGN KEY (contest_id) REFERENCES t_contests (contest_id);

ALTER TABLE t_records
    ADD CONSTRAINT FK_T_RECORDS_ON_PROBLEM FOREIGN KEY (problem_id) REFERENCES t_problems (problem_id);

ALTER TABLE t_records
    ADD CONSTRAINT FK_T_RECORDS_ON_USER FOREIGN KEY (user_id) REFERENCES t_users (user_id);

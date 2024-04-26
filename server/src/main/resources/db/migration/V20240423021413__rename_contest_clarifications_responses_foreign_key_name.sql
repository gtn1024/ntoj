ALTER TABLE t_contest_clarifications_responses
    DROP CONSTRAINT fk_tconclares_on_contest_clarification;

ALTER TABLE t_contest_clarifications_responses
    DROP CONSTRAINT fk_tconclares_on_contest_clarification_response;

ALTER TABLE t_contest_clarifications_responses
    ADD clarification_id BIGINT;

ALTER TABLE t_contest_clarifications_responses
    ADD response_id BIGINT;

ALTER TABLE t_contest_clarifications_responses
    ALTER COLUMN clarification_id SET NOT NULL;

ALTER TABLE t_contest_clarifications_responses
    ALTER COLUMN response_id SET NOT NULL;

UPDATE t_contest_clarifications_responses
    SET clarification_id = t_contest_clarifications_clarification_id;

UPDATE t_contest_clarifications_responses
    SET response_id = responses_response_id;

ALTER TABLE t_contest_clarifications_responses
    ADD CONSTRAINT uc_t_contest_clarifications_responses_response UNIQUE (response_id);

ALTER TABLE t_contest_clarifications_responses
    ADD CONSTRAINT fk_tconclares_on_contest_clarification FOREIGN KEY (clarification_id) REFERENCES t_contest_clarifications (clarification_id);

ALTER TABLE t_contest_clarifications_responses
    ADD CONSTRAINT fk_tconclares_on_contest_clarification_response FOREIGN KEY (response_id) REFERENCES t_contest_clarification_responses (response_id);

ALTER TABLE t_contest_clarifications_responses
    DROP COLUMN responses_response_id;

ALTER TABLE t_contest_clarifications_responses
    DROP COLUMN t_contest_clarifications_clarification_id;

ALTER TABLE t_contest_clarifications
    DROP COLUMN contest_contest_id;

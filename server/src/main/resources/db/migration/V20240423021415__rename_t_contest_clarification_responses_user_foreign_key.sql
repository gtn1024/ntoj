ALTER TABLE t_contest_clarification_responses
    DROP CONSTRAINT fk_t_contest_clarification_responses_on_user_user;

ALTER TABLE t_contest_clarification_responses
    ADD user_id BIGINT;

UPDATE t_contest_clarification_responses
    SET user_id = user_user_id;

ALTER TABLE t_contest_clarification_responses
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE t_contest_clarification_responses
    ADD CONSTRAINT FK_T_CONTEST_CLARIFICATION_RESPONSES_ON_USER FOREIGN KEY (user_id) REFERENCES t_users (user_id);

ALTER TABLE t_contest_clarification_responses
    DROP COLUMN user_user_id;

ALTER TABLE t_contest_clarifications
    DROP CONSTRAINT fk_t_contest_clarifications_on_user_user;

ALTER TABLE t_contest_clarifications
    ADD user_id BIGINT;

UPDATE t_contest_clarifications
    SET user_id = user_user_id;

ALTER TABLE t_contest_clarifications
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE t_contest_clarifications
    ADD CONSTRAINT FK_T_CONTEST_CLARIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES t_users (user_id);

ALTER TABLE t_contest_clarifications
    DROP COLUMN user_user_id;

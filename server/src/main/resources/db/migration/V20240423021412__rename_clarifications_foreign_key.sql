ALTER TABLE t_contest_clarifications
    DROP CONSTRAINT fk_t_contest_clarifications_on_contest_contest;

ALTER TABLE t_contests_clarifications
    DROP CONSTRAINT fk_tconcla_on_contest;

ALTER TABLE t_contests_clarifications
    DROP CONSTRAINT fk_tconcla_on_contest_clarification;

ALTER TABLE t_contests_clarifications
    ADD clarification_id BIGINT;

ALTER TABLE t_contests_clarifications
    ADD contest_id BIGINT;

UPDATE t_contests_clarifications
    SET clarification_id = clarifications_clarification_id;

UPDATE t_contests_clarifications
    SET contest_id = t_contests_contest_id;

ALTER TABLE t_contests_clarifications
    ADD CONSTRAINT pk_t_contests_clarifications PRIMARY KEY (clarification_id);

ALTER TABLE t_contests_clarifications
    ADD CONSTRAINT fk_tconcla_on_contest FOREIGN KEY (contest_id) REFERENCES t_contests (contest_id);

ALTER TABLE t_contests_clarifications
    ADD CONSTRAINT fk_tconcla_on_contest_clarification FOREIGN KEY (clarification_id) REFERENCES t_contest_clarifications (clarification_id);

ALTER TABLE t_contests_clarifications
    DROP COLUMN clarifications_clarification_id;

ALTER TABLE t_contests_clarifications
    DROP COLUMN t_contests_contest_id;

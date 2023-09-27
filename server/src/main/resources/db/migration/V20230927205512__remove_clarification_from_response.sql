ALTER TABLE t_contest_clarification_responses
    DROP CONSTRAINT fk_tcontestclarificationresponses_on_clarificationclarification;

ALTER TABLE t_contest_clarification_responses
    DROP COLUMN clarification_clarification_id;

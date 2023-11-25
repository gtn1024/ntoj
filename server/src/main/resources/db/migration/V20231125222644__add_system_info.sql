ALTER TABLE t_judge_client_tokens
    ADD info_last_updated_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE t_judge_client_tokens
    ADD kernel VARCHAR(255);

ALTER TABLE t_judge_client_tokens
    ADD memory_total BIGINT;

ALTER TABLE t_judge_client_tokens
    ADD memory_used BIGINT;

ALTER TABLE t_judge_client_tokens
    ADD os VARCHAR(255);

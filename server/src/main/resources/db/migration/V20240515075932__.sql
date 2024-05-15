CREATE TABLE t_contest_managers
(
    contest_id BIGINT NOT NULL,
    user_id    BIGINT NOT NULL
);

ALTER TABLE t_contest_managers
    ADD CONSTRAINT fk_tconman_on_contest FOREIGN KEY (contest_id) REFERENCES t_contests (contest_id);

ALTER TABLE t_contest_managers
    ADD CONSTRAINT fk_tconman_on_user FOREIGN KEY (user_id) REFERENCES t_users (user_id);

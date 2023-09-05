ALTER TABLE t_contests
    ADD author_user_id BIGINT;

ALTER TABLE t_contests
    ADD CONSTRAINT FK_T_CONTESTS_ON_AUTHOR_USER FOREIGN KEY (author_user_id) REFERENCES t_users (user_id);

ALTER TABLE t_problems
    ADD CONSTRAINT uc_t_problems_p_alias UNIQUE (p_alias);

ALTER TABLE t_problems
    ALTER COLUMN p_alias SET NOT NULL;

ALTER TABLE t_problems
    ALTER COLUMN title SET NOT NULL;

ALTER TABLE t_users
    ADD CONSTRAINT uc_t_users_username UNIQUE (username);

ALTER TABLE t_users
    ALTER COLUMN username SET NOT NULL;

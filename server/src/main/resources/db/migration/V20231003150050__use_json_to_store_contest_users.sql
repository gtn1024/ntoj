ALTER TABLE t_contests
    ADD users JSONB NOT NULL DEFAULT '[]';

ALTER TABLE t_contests_users
    DROP CONSTRAINT fk_tconuse_on_contest;

ALTER TABLE t_contests_users
    DROP CONSTRAINT fk_tconuse_on_user;

DROP TABLE t_contests_users CASCADE;

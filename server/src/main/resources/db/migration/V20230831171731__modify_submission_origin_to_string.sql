ALTER TABLE t_submissions
    DROP COLUMN origin;

ALTER TABLE t_submissions
    ADD origin VARCHAR(255) NOT NULL default 'PROBLEM';

ALTER TABLE t_problems
    ADD allow_all_languages BOOLEAN DEFAULT false;

ALTER TABLE t_problems
    ALTER COLUMN allow_all_languages SET NOT NULL;

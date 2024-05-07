UPDATE t_announcements
    SET visible = true
    WHERE visible IS NULL;

ALTER TABLE t_announcements
    ALTER COLUMN visible SET NOT NULL;

UPDATE t_problems
    SET visible = true
    WHERE visible IS NULL;

ALTER TABLE t_problems
    ALTER COLUMN visible SET NOT NULL;

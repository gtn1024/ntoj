ALTER TABLE t_announcements
    ALTER COLUMN author_user_id SET NOT NULL;

ALTER TABLE t_announcements
    ALTER COLUMN content SET NOT NULL;

ALTER TABLE t_announcements
    ALTER COLUMN title SET NOT NULL;

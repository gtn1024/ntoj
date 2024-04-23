ALTER TABLE t_articles
    ADD visible BOOLEAN DEFAULT true;

ALTER TABLE t_articles
    ALTER COLUMN visible SET NOT NULL;

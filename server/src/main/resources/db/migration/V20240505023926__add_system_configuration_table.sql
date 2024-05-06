CREATE TABLE system
(
    id    VARCHAR(255) NOT NULL,
    value JSONB        NOT NULL,
    CONSTRAINT pk_system PRIMARY KEY (id)
);

CREATE TABLE permission_role
(
    name       VARCHAR(255)  NOT NULL,
    permission VARCHAR(1023) NOT NULL,
    CONSTRAINT pk_permission_role PRIMARY KEY (name)
);

CREATE TABLE access__account
(
    entity_id   CHAR(26)                 not null primary key,
    coach_name   VARCHAR NOT NULL UNIQUE,
    email       VARCHAR UNIQUE,
    password    VARCHAR                  not null,
    is_active   BOOLEAN                  not null,
    last_login  TIMESTAMP WITH TIME ZONE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX ix_access_account_email
    ON access__account (email);

CREATE INDEX ix_access_account_is_active
    ON access__account (is_active);
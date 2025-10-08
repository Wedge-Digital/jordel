CREATE TABLE auth__user
(
    user_id     CHAR(26)                 not null primary key,
    username    VARCHAR NOT NULL UNIQUE,
    email       VARCHAR UNIQUE,
    password    VARCHAR                  not null,
    lang        VARCHAR                  not null,
    is_active   BOOLEAN                  not null,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    last_login  TIMESTAMP WITH TIME ZONE,
    roles       JSONB                    NOT NULL
);

CREATE UNIQUE INDEX ix_access_account_email
    ON auth__user (username);

CREATE INDEX ix_access_account_is_active
    ON auth__user (is_active);
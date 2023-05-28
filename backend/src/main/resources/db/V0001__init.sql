CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
SELECT uuid_generate_v4();

CREATE TABLE IF NOT EXISTS users
(
    id                 serial PRIMARY KEY,
    lichess_id         text NOT NULL UNIQUE, -- index
    created_at         timestamptz NOT NULL DEFAULT NOW(),
    updated_at         timestamptz NOT NULL DEFAULT NOW()
);

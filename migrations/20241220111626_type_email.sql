DROP DOMAIN IF EXISTS "Typing"."Email" CASCADE;

CREATE DOMAIN "Typing"."Email" AS TEXT NOT NULL CONSTRAINT email CHECK (
        value ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$'
);

ALTER DOMAIN "Typing"."Email" OWNER TO postgres;

COMMENT ON DOMAIN "Typing"."Email" IS E'(https://dba.stackexchange.com/questions/68266/what-is-the-best-way-to-store-an-email-address-in-postgresql/165923#165923)';

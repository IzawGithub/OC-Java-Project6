DROP DOMAIN IF EXISTS "Typing"."Argon2" CASCADE;

CREATE DOMAIN "Typing"."Argon2" AS TEXT CONSTRAINT Argon2 CHECK (value ~ '^\$argon2.*');

ALTER DOMAIN "Typing"."Argon2" OWNER TO postgres;

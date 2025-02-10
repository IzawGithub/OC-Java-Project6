DROP SCHEMA IF EXISTS "Typing" CASCADE;

CREATE SCHEMA "Typing";

ALTER SCHEMA "Typing" OWNER TO postgres;

SET search_path TO pg_catalog,
    public,
    "Typing";

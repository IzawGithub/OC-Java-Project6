DROP TABLE IF EXISTS public."user" CASCADE;

CREATE TABLE public."user" (
        id UUID NOT NULL DEFAULT uuid_generate_v4(),
        username TEXT NOT NULL,
        email "Typing"."Email" NOT NULL,
        password "Typing"."Argon2" NOT NULL,
        role TEXT NOT NULL,
        balance NUMERIC DEFAULT 0,
        CONSTRAINT "PK_user_id" PRIMARY KEY (id),
        CONSTRAINT "CK_unsigned_balance" CHECK (balance >= 0),
        CONSTRAINT "UQ_user_email" UNIQUE (email)
);

ALTER TABLE public."user" OWNER TO postgres;

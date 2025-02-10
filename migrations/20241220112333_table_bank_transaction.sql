DROP TABLE IF EXISTS public."bank_transaction" CASCADE;

CREATE TABLE public."bank_transaction" (
        id UUID NOT NULL DEFAULT uuid_generate_v4(),
        amount NUMERIC NOT NULL,
        sens "Typing"."ESens" NOT NULL,
        date TIMESTAMP with TIME ZONE DEFAULT current_timestamp,
        id_user UUID,
        CONSTRAINT "PK_bank_transaction_id" PRIMARY KEY (id),
        CONSTRAINT "CK_unsigned_amount" CHECK (amount > 0)
);

ALTER TABLE public."bank_transaction" OWNER TO postgres;

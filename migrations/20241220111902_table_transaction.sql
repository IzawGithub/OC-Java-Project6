DROP TABLE IF EXISTS public."transaction" CASCADE;

CREATE TABLE public."transaction" (
        id UUID NOT NULL DEFAULT uuid_generate_v4(),
        amount NUMERIC NOT NULL,
        description TEXT,
        date TIMESTAMP with TIME ZONE DEFAULT current_timestamp,
        id_sender UUID NOT NULL,
        id_receiver UUID NOT NULL,
        CONSTRAINT "PK_transaction_id" PRIMARY KEY (id),
        CONSTRAINT "CK_unsigned_amount" CHECK (amount > 0)
);

ALTER TABLE public."transaction" OWNER TO postgres;

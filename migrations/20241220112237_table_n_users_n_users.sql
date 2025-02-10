CREATE TABLE public."many_users_knows_many_users" (
    id_user UUID NOT NULL,
    id_friend UUID NOT NULL,
    CONSTRAINT "PK_many_users_knows_many_users" PRIMARY KEY (id_user, id_friend)
);

ALTER TABLE public."many_users_knows_many_users" DROP CONSTRAINT IF EXISTS "FK_USER" CASCADE;

ALTER TABLE public."many_users_knows_many_users"
ADD CONSTRAINT "FK_USER" FOREIGN KEY (id_user) REFERENCES public."user" (id) MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE public."many_users_knows_many_users" DROP CONSTRAINT IF EXISTS "FK_FRIEND" CASCADE;

ALTER TABLE public."many_users_knows_many_users"
ADD CONSTRAINT "FK_FRIEND" FOREIGN KEY (id_friend) REFERENCES public."user" (id) MATCH FULL ON DELETE CASCADE ON UPDATE CASCADE;

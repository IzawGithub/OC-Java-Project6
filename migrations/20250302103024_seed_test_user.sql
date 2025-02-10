INSERT INTO public."user"(username, email, password, role)
VALUES (
        'John Doe',
        'john.doe@test.com',
        -- PasswordJohnDoe
        '$argon2id$v=19$m=16384,t=2,p=1$U2FsdEpvaG5Eb2U$ZUFRW1E1uppirvcUqAjL1w',
        'USER'
    ),
    (
        'Jane Doe',
        'jane.doe@test.com',
        -- PasswordJaneDoe
        '$argon2id$v=19$m=16384,t=2,p=1$U2FsdEphbmVEb2U$4JWoVz4qkPCV8upyM//MrA',
        'USER'
    );

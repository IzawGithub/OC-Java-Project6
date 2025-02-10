INSERT INTO public."user"(username, email, password, role)
VALUES (
        'PayMyBuddy',
        'user@paymybuddy.com',
        -- PasswordPayMyBuddy
        '$argon2id$v=19$m=16384,t=2,p=1$UGF5TXlCdWRkeQ$GZ5gewp0XM8w12gZUt8CvA',
        'ADMIN'
    );

# errors

## errors/EErrorCrud.java

-crud-error =
    CRUD error:

EErrorCrud-exist =
    {-crud-error}
    User '{$id}' {$exist ->
        [true] already
        [false] does not
        *[other] unknown
    } exist.
EErrorCrud-equals-values =
    {-crud-error}
    Operation cannot be done on the same user '{$id}'.

## errors/EErrorEmail.java

-email-error =
    Email error:

EErrorEmail-invalid =
    {-email-error}
    '{$email}' is not a valid email.
EErrorEmail-null =
    {-email-error}
    Email value is null.

## errors/EErrorMoney.java

-money-error =
    Money error:

EErrorMoney-no-negative =
    {-money-error}
    Cannot represent a negative value as money.
    {-tab}Value: '{$value}'
EErrorMoney-null =
    {-money-error}
    Money value is null.

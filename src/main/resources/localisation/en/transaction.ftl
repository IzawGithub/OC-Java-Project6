# Transaction

## service/TransactionService.java

TransactionService-bank-success-info =
    User '{$user}': Stored new bank transaction.
TransactionService-user-success-info =
    User '{$user_sender}' sent '{$change}' to user '{$user_receiver}'.
    {-tab}Sender:
    {-tab}{-tab}Old balance: '{$sender_balance_old}'
    {-tab}{-tab}New balance: '{$sender_balance_new}'
    {-tab}Receiver:
    {-tab}{-tab}Old balance: '{$receiver_balance_old}'
    {-tab}{-tab}New balance: '{$receiver_balance_new}'
TransactionService-money-exchange-info =
    User '{$id}' {$transactionType ->
        [add] added
        [substract] removed
        *[other]  had a transaction of value
    } '{$change}' {$transactionType ->
        [add] to
        [substract] from
        *[other] unknown
    } their account.


## errors/EErrorTransaction.java

-transaction-error =
    Transaction error:

EErrorTransaction-equal-id =
    {-transaction-error}
    Sender and receiver are the same user.
    {-tab}Id: '{$id}'.
EErrorTransaction-bank-balance-too-low =
    {-transaction-error}
    Balance of user '{$user}' is too low for the transaction.
    {-tab}Current balance: '{$balance}'
    {-tab}Transaction amount: '{$amount}'
    {-tab}Difference: '{$difference}'

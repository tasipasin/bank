# Bank API Task

### Task Description
Create a bank API that should have:
* A method to reset everything in the API, clearing all existing accounts (**/reset**).
* A method to consult balance from an account, receiving the account id by parameter (**/balance**).
    * Return only the balance in the body.
* A method to make transactions with an Account, where the type is passed in the body (**/event**):
    * **Deposit**: Receives the destination and the amount in the body. Creates account if doesn't already exists and deposit the amount to the account. Returns HTTP code (201) with body containing the *destination* account with respectives id and updated balance.
    * **Withdraw**: Receives the origin and the amount in the body. Returns HTTP code (201) with body containing the origin account with respectives id and updated balance.
    * **Transfer**: Receives the origin, the destination and the amount in the body. Creates destination account if doesn't already exists and deposit the amount to the account. Returns HTTP code (201) with body containing the origin account with respectives id and updated balance, and destination account with respectives id and updated balance.
* If a withdraw (transferrence is a type of withdraw) or consult request is made to a non-existing account, should return HTTP Error 404 with 0 at the body.
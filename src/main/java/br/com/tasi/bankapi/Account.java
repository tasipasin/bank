
package br.com.tasi.bankapi;

import java.util.Objects;

/**
 * Account class.
 */
public class Account {

    /** Stores the Account ID. */
    private final int accountId;
    /** Stores the Account Balance. */
    private int balance = 0;

    /**
     * Account class controller.
     * @param accountId Account ID.
     */
    public Account(int accountId) {
        this.accountId = accountId;
    }

    /**
     * Returns the Account ID.
     * @return the Account ID.
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Returns the Account Balance.
     * @return the Account Balance.
     */
    public int getBalance() {
        return balance;
    }

    /**
     * Performs a withdraw from the account. Does NOT check if there is
     * enough balance.
     * @return The balance after the withdraw.
     */
    public int withdraw(int balance) {
        this.balance -= balance;
        return this.balance;
    }

    /**
     * Performs a deposit to the account.
     * @return The balance after the withdraw.
     */
    public int deposit(int balance) {
        this.balance += balance;
        return this.balance;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.accountId);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Account && Objects.equals(obj.hashCode(), this.hashCode());
    }
}

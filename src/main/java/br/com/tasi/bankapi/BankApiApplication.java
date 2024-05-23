
package br.com.tasi.bankapi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BankApiApplication {

    Logger log = LoggerFactory.getLogger(BankApiApplication.class);
    /** Active Accounts list (Allows only one with the same hash). */
    private final Set<Account> activeAccounts = new HashSet<>();

    public static void main(String[] args) {
        SpringApplication.run(BankApiApplication.class, args);
    }

    /**
     * Removes all accounts.
     */
    @PostMapping("/reset")
    public ResponseEntity<Integer> reset() {
        log.info("Accounts reseted");
        this.activeAccounts.clear();
        return ResponseEntity.status(HttpStatus.OK).body(0);
    }

    @GetMapping("/balance")
    public ResponseEntity<Integer> balance(@RequestParam(name = "account_id") int accountId) {
        log.info("Received account_id [{}]", accountId);
        // Gets the account by it's ID
        Account account = this.getAccountById(accountId);
        if (null != account) {
            // Answer 202 and the balance
            return ResponseEntity.status(HttpStatus.OK).body(account.getBalance());
        }
        // Answer 404 and 0
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
    }

    @PostMapping("/event")
    public ResponseEntity<Object> handleEvent(@RequestBody Map<String, Object> body) {
        if (body.containsKey("type")) {
            Map<?, ?> result = this.doTypeEvent(body);
            // If result is empty, the process hasn't been executed
            if (!result.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
    }

    /**
     * Search the Account that has the requested ID.
     * @param accountId Account ID to be found.
     * @return An Account object if there is an account with the
     *         requested ID. Returns <code>null</code> otherwise.
     */
    private Account getAccountById(Integer accountId) {
        return getAccountById(accountId, false);
    }

    /**
     * Search the Account that has the requested ID.
     * @param accountId Account ID to be found.
     * @param createOfNotExists Indicates to create Account if not found.
     * @return An Account object if there is an account with the
     *         requested ID. Returns <code>null</code> otherwise.
     */
    private Account getAccountById(Integer accountId, boolean createOfNotExists) {
        // Searches for the corresponding Account
        Account account = this.activeAccounts.stream()
                .filter(accountIt -> Objects.equals(accountIt.getAccountId(), accountId))
                .findFirst()
                .orElse(null);
        // Create the account if doesn't exists
        if (createOfNotExists && null == account) {
            account = new Account(accountId);
            this.activeAccounts.add(account);
        }
        return account;
    }

    /**
     * Checks the event and executes it, if it's mapped and known.
     * @param body A Map containing the required data to execute the event.
     * @return The result of the event.
     */
    private Map<String, Object> doTypeEvent(Map<String, Object> body) {
        // Gets all the values. If one are not present, assume -1
        String type = (String) body.get("type");
        int amount = (Integer) body.get("amount");
        int origin = Integer.parseInt((String) body.getOrDefault("origin", "-1"));
        int destination = Integer.parseInt((String) body.getOrDefault("destination", "-1"));
        // Select the event type to execute
        switch (type) {
            case "deposit":
                return this.doDeposit(destination, amount);
            case "withdraw":
                return this.doWithdraw(origin, amount);
            case "transfer":
                return this.doTransfer(origin, destination, amount);
            default:
                return new HashMap<>();// Do nothing
        }
    }

    /**
     * Make Deposit Event.
     * @param destination Account to be deposited.
     * @param amount Amount to be deposited.
     * @return The result of the event.
     */
    private Map<String, Object> doDeposit(int destination, int amount) {
        log.info("Making deposit to {} of {}", destination, amount);
        Map<String, Object> result = new HashMap<>();
        Account account = this.getAccountById(destination, true);
        // Checks account
        account.deposit(amount);
        result.put("destination", account.toString());
        log.info("Updated Account Object: [{}]", account);
        return result;
    }

    /**
     * Make Withdraw Event.
     * @param destination Account from where to withdraw.
     * @param amount Amount to be withdrawn.
     * @return The result of the event.
     */
    private Map<String, Object> doWithdraw(int origin, int amount) {
        log.info("Withdrawing to {} of {}", origin, amount);
        Map<String, Object> result = new HashMap<>();
        Account account = this.getAccountById(origin);
        // Checks account
        if (null != account) {
            account.withdraw(amount);
            result.put("origin", account.toString());
            log.info("Updated Account Object: [{}]", account);
        }
        return result;
    }

    /**
     * Make Transference Event.
     * @param destination Account to be deposited.
     * @param destination Account from where to withdraw.
     * @param amount Amount to be transferred.
     * @return The result of the events.
     */
    private Map<String, Object> doTransfer(int origin, int destination, int amount) {
        Map<String, Object> result = new HashMap<>();
        result.putAll(doWithdraw(origin, amount));
        // If result is empty is because the account to withdraw doesn't exists
        // So mustn't make deposit
        if (!result.isEmpty()) {
            result.putAll(doDeposit(destination, amount));
        }
        return result;
    }
}


package br.com.tasi.bankapi;

import java.util.HashSet;
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

    @PostMapping("/helloBody")
    public String helloBody(@RequestBody String body) {
        return "Received body: " + body;
    }

    /**
     * Search the Account that has the requested ID.
     * @param accountId Account ID to be found.
     * @return An Account object if there is an account with the
     *         requested ID. Returns <code>null</code> otherwise.
     */
    private Account getAccountById(Integer accountId) {
        return this.activeAccounts.stream()
                .filter(account -> Objects.equals(account.getAccountId(), accountId))
                .findFirst()
                .orElse(null);
    }
}

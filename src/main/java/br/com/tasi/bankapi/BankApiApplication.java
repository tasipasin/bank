
package br.com.tasi.bankapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BankApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApiApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello do Tasi";
    }

    @GetMapping("/helloTo")
    public String hello(@RequestParam String name) {
        return "Hello do Tasi para " + name;
    }

    @PostMapping("/helloBody")
    public String helloBody(@RequestBody String body) {
        return "Received body: " + body;
    }
}

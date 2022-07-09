package quarkus.accounts;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
// https://quarkus.io/guides/cdi
public final class AccountRepository {
    private final Set<Account> accounts = new HashSet<>();

    // @PostConstruct indicates the method should be called straight after creation
    // of the CDI Bean
    @PostConstruct
    public final void setup() {
        accounts.add(new Account(123456789L, 987654321L, "George Baird", new BigDecimal("354.23")));
        accounts.add(new Account(121212121L, 888777666L, "Mary Taylor", new BigDecimal("560.03")));
        accounts.add(new Account(545454545L, 222444999L, "Diana Rigg", new BigDecimal("422.00")));
    }

    public final Set<Account> getAll() {
        return accounts;
    }

    public final void add(Account account) {
        accounts.add(account);
    }

    public final void update(Account account) {
        delete(account);
        accounts.add(account);
    }

    public final void delete(Account account) {
        accounts.remove(account);
    }
}

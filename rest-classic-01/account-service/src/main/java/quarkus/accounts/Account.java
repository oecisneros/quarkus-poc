package quarkus.accounts;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Predicate;

import javax.json.bind.annotation.JsonbCreator;

public final record Account(Long accountNumber, Long customerNumber, String customerName, BigDecimal balance,
        AccountStatus status) {

    @JsonbCreator
    public Account(Long accountNumber, Long customerNumber, String customerName, BigDecimal balance){
        this(accountNumber, customerNumber, customerName, balance, AccountStatus.OPEN);
    }

    public final Account markOverdrawn() {
        return new Account(accountNumber, customerNumber, customerName, balance, AccountStatus.OVERDRAWN);
    }

    public final Account removeOverdrawnStatus() {
        return new Account(accountNumber, customerNumber, customerName, balance, AccountStatus.OPEN);
    }

    public final Account close() {
        return new Account(accountNumber, customerNumber, customerName, BigDecimal.valueOf(0), AccountStatus.CLOSED);
    }

    public final Account withdrawFunds(BigDecimal amount) {
        return new Account(accountNumber, customerNumber, customerName, balance.subtract(amount), status);
    }

    public final Account addFunds(BigDecimal amount) {
        return new Account(accountNumber, customerNumber, customerName, balance.add(amount), status);
    }

    public final static Predicate<Account> findBy(Long accountNumber) {
        return (Account account) -> account.accountNumber().equals(accountNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var a = (Account) o;
        return accountNumber.equals(a.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
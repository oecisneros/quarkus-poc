package quarkus.accounts;

import java.math.BigDecimal;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import quarkus.infrastructure.ResourceBase;

import static quarkus.accounts.Account.findBy;

@Path("/accounts")
public final class AccountResource extends ResourceBase {
    @Inject
    AccountRepository repository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public final Set<Account> getAccounts() {
        return repository.getAll();
    }

    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public final Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        return repository.getAll()
                .stream()
                .filter(findBy(accountNumber))
                .findFirst()
                .orElseThrow(() -> notFound("Account with id of {0,number,#} does not exist.", accountNumber));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public final Response createAccount(Account account) {
        if (account.accountNumber() == null) {
            throw badRequest("No Account number specified.");
        }

        repository.add(account);
        return created(account);
    }

    @PUT
    @Path("/{accountNumber}/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public final Account addFunds(@PathParam("accountNumber") Long accountNumber,
            String amount) {
        var account = getAccount(accountNumber);
        var newAccount = account.addFunds(new BigDecimal(amount));

        repository.update(newAccount);
        return newAccount;
    }

    @PUT
    @Path("/{accountNumber}/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    public final Account withdrawFunds(@PathParam("accountNumber") Long accountNumber,
            String amount) {
        var account = getAccount(accountNumber);
        var newAccount = account.withdrawFunds(new BigDecimal(amount));

        repository.update(newAccount);
        return newAccount;
    }

    @DELETE
    @Path("/{accountNumber}")
    public final Response deleteAccount(@PathParam("accountNumber") Long accountNumber) {
        var account = getAccount(accountNumber);
        repository.delete(account);

        return noContent();
    }
}

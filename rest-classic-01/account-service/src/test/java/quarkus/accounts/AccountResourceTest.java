package quarkus.accounts;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
//import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public final class AccountResourceTest {
    @Test
    @Order(1)
    void testRetrieveAll() {
        var result = given()
                .when()
                .get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("George Baird"),
                        containsString("Mary Taylor"),
                        containsString("Diana Rigg"))
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(3));
    }

    @Test
    @Order(2)
    void testGetAccount() {
        var account = given()
                .when()
                .get("/accounts/{accountNumber}", 545454545)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.accountNumber(), equalTo(545454545L));
        assertThat(account.customerName(), equalTo("Diana Rigg"));
        assertThat(account.balance(), equalTo(new BigDecimal("422.00")));
        assertThat(account.status(), equalTo(AccountStatus.OPEN));
    }

    @Test
    @Order(3)
    void testCreateAccount() {
        var newAccount = new Account(324324L, 112244L, "Sandy Holmes", new BigDecimal("154.55"));

        var returnedAccount = given()
                .contentType(ContentType.JSON)
                .body(newAccount)
                .when()
                .post("/accounts")
                .then()
                .statusCode(201)
                .extract()
                .as(Account.class);

        assertThat(returnedAccount, notNullValue());
        assertThat(returnedAccount, equalTo(newAccount));

        var result = given()
                .when()
                .get("/accounts")
                .then()
                .statusCode(200)
                .body(
                        containsString("George Baird"),
                        containsString("Mary Taylor"),
                        containsString("Diana Rigg"),
                        containsString("Sandy Holmes"))
                .extract()
                .response();

        List<Account> accounts = result.jsonPath().getList("$");
        assertThat(accounts, not(empty()));
        assertThat(accounts, hasSize(4));
    }

    @Test
    @Order(4)
    void testAccountWithdraw() {
        var account = given()
                .when()
                .get("/accounts/{accountNumber}", 545454545)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.accountNumber(), equalTo(545454545L));
        assertThat(account.customerName(), equalTo("Diana Rigg"));
        assertThat(account.balance(), equalTo(new BigDecimal("422.00")));
        assertThat(account.status(), equalTo(AccountStatus.OPEN));

        var result = given()
                .body("56.21")
                .when()
                .put("/accounts/{accountNumber}/withdraw", 545454545)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(result.accountNumber(), equalTo(545454545L));
        assertThat(result.customerName(), equalTo("Diana Rigg"));
        assertThat(result.balance(), equalTo(account.balance().subtract(new BigDecimal("56.21"))));
        assertThat(result.status(), equalTo(AccountStatus.OPEN));
    }

    @Test
    @Order(4)
    void testAccountDeposit() {
        var account = given()
                .when()
                .get("/accounts/{accountNumber}", 123456789)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(account.accountNumber(), equalTo(123456789L));
        assertThat(account.customerName(), equalTo("George Baird"));
        assertThat(account.balance(), equalTo(new BigDecimal("354.23")));
        assertThat(account.status(), equalTo(AccountStatus.OPEN));

        var result = given()
                .body("28.42")
                .when()
                .put("/accounts/{accountNumber}/deposit", 123456789)
                .then()
                .statusCode(200)
                .extract()
                .as(Account.class);

        assertThat(result.accountNumber(), equalTo(123456789L));
        assertThat(result.customerName(), equalTo("George Baird"));
        assertThat(result.balance(), equalTo(account.balance().add(new BigDecimal("28.42"))));
        assertThat(result.status(), equalTo(AccountStatus.OPEN));
    }
}
package it.polito.ezshop.apiTests;

import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;
import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.Order;
import it.polito.ezshop.exceptions.InvalidCreditCardException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.OperationStatus;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;

public class EZShopTestReceiveCreditCardPayment {

    private static final String creditCardsFile = "CreditCards-tests.txt";

    private static final EZShop shop = new EZShop();
    private static User admin;

    private static final String productCode = "12345678901231";
    private static int totalBalance = 0;
    private static int toBePaid;
    private static int saleId;

    // a credit card from the credit cards file with an initial balance of 150.0
    private static final String creditCard = "4485370086510891";
    private static final double creditCardBalance = 150.0;
    // a credit card from the credit cards file with an initial balance of 0.0
    private static final String emptyCreditCard = "4716258050958645";

    // credit card circuit
    public TextualCreditCardCircuit fakeCreditCardCircuit;


    public EZShopTestReceiveCreditCardPayment() throws Exception {
        admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
    }

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {
        // copy a clean version of the credit cards file
        Files.copy(Paths.get(TextualCreditCardCircuit.CLEAN_TEXT_FILE), Paths.get(creditCardsFile), REPLACE_EXISTING);
        // create a new credit card circuit
        fakeCreditCardCircuit = new TextualCreditCardCircuit(creditCardsFile);

        // reset shop to blank state
        shop.reset();
        shop.setCreditCardCircuit(fakeCreditCardCircuit);
        totalBalance = 0;

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login for shop setup
        shop.login(admin.getUsername(), admin.getPassword());

        // create product for transactions in shop
        int productId = shop.createProductType("description", productCode, 1, "note");
        shop.updatePosition(productId, "1-1-1");

        // set initial balance for the shop
        int addedBalance = 1000;
        shop.recordBalanceUpdate(addedBalance);
        totalBalance += addedBalance;

        // populate shop with a few products
        int productAmount = 200;
        int orderId = shop.payOrderFor(productCode, productAmount, 1);
        shop.recordOrderArrival(orderId);
        totalBalance -= productAmount;

        // record a SALE transaction in CLOSED state
        toBePaid = 5;
        saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, toBePaid);
        shop.endSaleTransaction(saleId);

        // logout after setup
        shop.logout();
    }

    @After
    public void afterEach () throws IOException {
        Files.deleteIfExists(Paths.get(creditCardsFile));
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("receiveCreditCardPayment", Integer.class, String.class);
        testAccessRights(defineCustomer, new Object[] {saleId, creditCard},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException.
     */
    @Test
    public void testInvalidTransactionIdException() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (transactionId) -> shop.receiveCreditCardPayment(transactionId, creditCard));
    }

    /**
     * If the credit card is not valid according to the luhn algorithm, the method should throw an InvalidCreditCardException.
     */
    @Test
    public void testInvalidCreditCardException() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCreditCardException.class, invalidCreditCards,
                (card) -> shop.receiveCreditCardPayment(saleId, card));
    }

    /**
     * If the transaction ID does not exist false should be returned.
     */
    @Test
    public void testIdDoesNotExist() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate ID not assigned to any transaction in the shop
        int nonExistentId = generateId(shop.getCreditsAndDebits(null, null).stream()
                .map(BalanceOperation::getBalanceId)
                .collect(Collectors.toList()));

        // if ID does not exist -1 is returned
        assertFalse(shop.receiveCreditCardPayment(nonExistentId, creditCard));
    }

    /**
     * Tests that an Order can not be paid using receiveCreditCardPayment, only SaleTransactions should be paid using this method.
     */
    @Test
    public void testPayUnpaidOrderFails() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // issue an order
        int orderId = shop.issueOrder(productCode, 10, 1);

        // trying to pay for an order with receiveCreditCardPayment fails
        assertFalse(shop.receiveCreditCardPayment(orderId, creditCard));

        // verify order is still in CLOSED state
        assertEquals("ISSUED", shop.getAllOrders().stream()
                .filter(b -> b.getBalanceId() == orderId)
                .map(Order::getStatus)
                .findAny()
                .orElse(null));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        assertEquals(initialCardBalance, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * If the SaleTransaction has net yet been closed, false should be returned.
     */
    @Test
    public void testPayOpenSaleFails() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // open a sale without closing it
        int openSaleId = shop.startSaleTransaction();
        assertTrue(shop.addProductToSale(openSaleId, productCode, 5));

        // trying to pay for an open sale fails
        assertFalse(shop.receiveCreditCardPayment(openSaleId, creditCard));

        // verify sale is still not paid
        assertNull(getSaleTransaction(saleId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify the card's balance did not update
        assertEquals(initialCardBalance, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * If the credit card number is correct according to luhn's algorithm but is not registered, false should be returned.
     */
    @Test
    public void testCreditCardNotRegistered() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // trying to pay for a sale with unregistered credit card
        Assert.assertFalse(shop.receiveCreditCardPayment(saleId, "1358954993914492"));

        // verify sale is still not paid
        assertNull(getSaleTransaction(saleId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * If the balance of the credit card is less than the transaction's total, false should be returned.
     */
    @Test
    public void testNotEnoughBalance() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(emptyCreditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // trying to pay for a sale with credit card with insufficient funds
        Assert.assertFalse(shop.receiveCreditCardPayment(saleId, emptyCreditCard));

        // verify sale hasn't been paid yet
        assertNull(getSaleTransaction(saleId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify the card's balance did not update
        assertEquals(initialCardBalance, fakeCreditCardCircuit.getBalance(emptyCreditCard), 0.01);
    }

    /**
     * Tests that if the SaleTransaction has been closed and the provided credit card has sufficient balance, the
     * payment is made correctly and the credit card's balance updated
     */
    @Test
    public void testPayTransactionSuccessfully() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // pay transaction successfully
        assertTrue(shop.receiveCreditCardPayment(saleId, creditCard));
        totalBalance += toBePaid;

        // verify sale is in state PAID/COMPLETED
        assertEquals(OperationStatus.COMPLETED, getSaleTransaction(saleId).getStatus());

        // verify system's balance did update correctly
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify the card's balance did update correctly
        assertEquals(initialCardBalance - toBePaid, fakeCreditCardCircuit.getBalance(creditCard), 0.01);

        // setup sale transaction that is too expensive to be paid with the credit card now, but could be paid before
        int secondSaleId = shop.startSaleTransaction();
        shop.addProductToSale(secondSaleId, productCode, (int) creditCardBalance);
        shop.endSaleTransaction(secondSaleId);
        // verify card can't be used anymore to pay this transaction
        assertFalse(shop.receiveCreditCardPayment(secondSaleId, creditCard));

        // verify the card's balance did not update again
        assertEquals(initialCardBalance - toBePaid, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * Tests that if the SaleTransaction has already been paid, performing
     */
    @Test
    public void testPayCompletedSale() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // make sure card has sufficient balance to theoretically pay transaction twice
        assertTrue(creditCardBalance >= 2 * toBePaid);

        // pay for sale so it is in state PAID/COMPLETED
        assertTrue(shop.receiveCreditCardPayment(saleId, creditCard));
        totalBalance += toBePaid;

        // try to pay for sale second time returns false
        Assert.assertFalse(shop.receiveCreditCardPayment(saleId, creditCard));

        // verify sale remains in state PAID/COMPLETED
        assertNotNull(shop.getSaleTransaction(saleId));

        // verify system's balance did not update a second time
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify the card's balance did update correctly
        assertEquals(initialCardBalance - toBePaid, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    private it.polito.ezshop.model.BalanceOperation getSaleTransaction(int sid) throws Exception {
        return shop.getCreditsAndDebits(null, null).stream()
                .filter(b -> b.getBalanceId() == sid)
                .map(b -> ((BalanceOperationAdapter) b).getTransaction())
                .findAny()
                .orElse(null);
    }
}

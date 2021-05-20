package it.polito.ezshop.apiTests;

import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;
import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidCreditCardException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static junit.framework.TestCase.assertEquals;
import static it.polito.ezshop.TestHelpers.*;

public class EZShopTestReturnCreditCardPayment {

    private static final String creditCardsFile = "tmp/CreditCards-tests.txt";

    private static final EZShop shop = new EZShop();
    private static User admin;

    private static final String productCode = "12345678901231";
    private static int totalBalance = 0;
    private static int saleId;
    private static int returnId;
    private static int returnedValue;

    // a credit card from the credit cards file with an initial balance of 150.0
    private static final String creditCard = "4485370086510891";
    private static final double creditCardBalance = 150.0;

    // credit card circuit
    public TextualCreditCardCircuit fakeCreditCardCircuit;

    public EZShopTestReturnCreditCardPayment() throws Exception {
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
        fakeCreditCardCircuit.init();

        // reset shop to blanc state
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
        int productAmount = 10;
        int orderId = shop.payOrderFor(productCode, productAmount, 1);
        shop.recordOrderArrival(orderId);
        totalBalance -= productAmount;

        // record a sale transaction in COMPLETED state
        int saleValue = 5;
        saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, saleValue);
        shop.endSaleTransaction(saleId);
        shop.receiveCashPayment(saleId, saleValue);
        totalBalance += saleValue;

        // record a return transaction in CLOSED state
        returnedValue = 2;
        returnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(returnId, productCode, returnedValue);
        shop.endReturnTransaction(returnId, true);

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("returnCreditCardPayment", Integer.class, String.class);
        testAccessRights(defineCustomer, new Object[] {returnId, creditCard},
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
                (transactionId) -> shop.returnCreditCardPayment(transactionId, creditCard));
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
                (card) -> shop.returnCreditCardPayment(saleId, card));
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
        assertEquals(-1, shop.returnCreditCardPayment(nonExistentId, creditCard), 0.001);
    }

    /**
     * If the credit card number is correct according to luhn's algorithm but is not registered, false should be returned.
     */
    @Test
    public void testCreditCardNotRegistered() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // trying to receive return with unregistered credit card
        Assert.assertEquals(-1, shop.returnCreditCardPayment(returnId, "1358954993914492"), 0.001);

        // verify return is still unpaid
        Assert.assertNull(getStatusOfTransaction(returnId));

        // verify system's balance did not change
        Assert.assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Tests that an Order can not be paid using returnCreditCardPayment, only ReturnTransactions can be paid using this method.
     */
    @Test
    public void testPayUnpaidOrderFails() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // issue an order
        int orderId = shop.issueOrder(productCode, 10, 1);

        // trying to pay for an order with receiveCreditCardPayment fails
        assertEquals(-1, shop.returnCreditCardPayment(orderId, creditCard), 0.001);

        // verify order is still in unpaid state
        Assert.assertNull(getStatusOfTransaction(orderId));

        // verify system's balance did not change
        Assert.assertEquals(totalBalance, shop.computeBalance(), 0.001);

        Assert.assertEquals(initialCardBalance, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * If the ReturnTransaction has net yet been closed an error value should be returned.
     */
    @Test
    public void testReturnOpenReturnFails() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // open a return without closing it
        int openReturnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(openReturnId, productCode, 2);

        // trying to receive cash payment for a still open return fails
        Assert.assertEquals(-1, shop.returnCreditCardPayment(openReturnId, creditCard), 0.001);

        // verify return is still in unpaid state
        Assert.assertNull(getStatusOfTransaction(returnId));

        // verify system's balance did not change
        Assert.assertEquals(totalBalance, shop.computeBalance(), 0.001);

        Assert.assertEquals(initialCardBalance, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * Tests that if the ReturnTransaction has been closed and the provided credit card is registered, the cash is returned
     * correctly and the transaction is marked as PAID/COMPLETED
     */
    @Test
    public void testReturnSuccessfully() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify receive correct amount of cash for return
        Assert.assertEquals(returnedValue, shop.returnCreditCardPayment(returnId, creditCard), 0.001);
        totalBalance -= returnedValue;

        // verify return is in state PAID/COMPLETED
        Assert.assertEquals(OperationStatus.COMPLETED, getStatusOfTransaction(returnId));

        // verify system's balance did update correctly
        Assert.assertEquals(totalBalance, shop.computeBalance(), 0.001);

        Assert.assertEquals(initialCardBalance + returnedValue, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * Tests that if the ReturnTransaction has already been completed, requesting payment for the same return adds no
     * funds to the credit card
     */
    @Test
    public void testReturnCompletedReturnAgain() throws Exception {
        double initialCardBalance = fakeCreditCardCircuit.getBalance(creditCard);

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // pay for return so it is in state PAID/COMPLETED
        Assert.assertEquals(returnedValue, shop.returnCreditCardPayment(returnId, creditCard), 0.001);
        totalBalance -= returnedValue;

        // try to ask for return a second time gives 0 as amount of cash to be returned
        Assert.assertEquals(-1.0, shop.returnCreditCardPayment(returnId, creditCard), 0.001);

        // verify return remains in state PAID/COMPLETED
        Assert.assertEquals(OperationStatus.COMPLETED, getStatusOfTransaction(returnId));

        // verify system's balance did not update a second time
        Assert.assertEquals(totalBalance, shop.computeBalance(), 0.001);

        Assert.assertEquals(initialCardBalance + returnedValue, fakeCreditCardCircuit.getBalance(creditCard), 0.01);
    }

    /**
     * Gets the OperationStatus of the transaction with the given ID or null if it is not in PAID/COMPLETED
     * state or if it does not exist
     *
     * @param returnId ID of the requested transaction
     * @return the Operation status of the transaction as a String,
     *         null not paid or does not exist
     */
    private static OperationStatus getStatusOfTransaction(int returnId) throws Exception {
        return shop.getCreditsAndDebits(null, null).stream()
                .filter(b -> b.getBalanceId() == returnId)
                .map(b -> ((it.polito.ezshop.model.adapters.BalanceOperationAdapter) b).getTransaction().getStatus())
                .findAny()
                .orElse(null);
    }
}

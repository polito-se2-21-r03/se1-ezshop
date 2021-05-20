package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.Order;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;
import static it.polito.ezshop.utils.Utils.generateId;

public class EZShopTestReceiveCashPayment {

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String productCode = "12345678901231";
    private static int totalBalance;
    private static int toBePaid;
    private static int saleId;

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {

        // reset shop to blanc state
        shop.reset();
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

        // record a SALE transaction in CLOSED state
        toBePaid = 5;
        saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, toBePaid);
        shop.endSaleTransaction(saleId);

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("receiveCashPayment", Integer.class, double.class);
        testAccessRights(defineCustomer, new Object[] {saleId, toBePaid},
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
                (transactionId) -> shop.receiveCashPayment(transactionId, 10));
    }

    /**
     * If the payment is 0 or negative, the method should throw InvalidPaymentException.
     */
    @Test
    public void testInvalidPaymentException() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidPaymentException.class, invalidPaymentAmounts,
                (cash) -> shop.receiveCashPayment(saleId, cash));
    }

    /**
     * If the transaction ID does not exist an error value should be returned.
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
        assertEquals(-1, shop.receiveCashPayment(nonExistentId, 10), 0.001);
    }

    /**
     * Tests that an Order can not be paid using receiveCashPayment, only SaleTransactions should be paid using this method.
     */
    @Test
    public void testPayUnpaidOrderFails() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // issue an order
        int orderId = shop.issueOrder(productCode, 10, 1);

        // trying to pay for an order with receiveCashPayment fails
        assertEquals(-1, shop.receiveCashPayment(orderId, 10), 0.001);

        // verify order is still in ISSUED state
        assertEquals("ISSUED", shop.getAllOrders().stream()
                .filter(b -> b.getBalanceId() == orderId)
                .map(Order::getStatus)
                .findAny()
                .orElse(null));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * If the SaleTransaction has net yet been closed an error value should be returned.
     */
    @Test
    public void testPayOpenSaleFails() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // open a sale without closing it
        int openSaleId = shop.startSaleTransaction();
        assertTrue(shop.addProductToSale(openSaleId, productCode, 5));

        // trying to pay for an open sale fails
        assertEquals(-1, shop.receiveCashPayment(openSaleId, 10), 0.001);

        // verify sale did not change state to PAID/COMPLETED
        assertFalse(isTransactionInAccountBook(saleId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * If the given cash amount is less than the transaction's total an error value should be returned.
     */
    @Test
    public void testNotEnoughCash() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // trying to pay for a sale with insufficient funds
        assertEquals(-1, shop.receiveCashPayment(saleId, toBePaid-1), 0.001);

        // verify sale did not change state to PAID/COMPLETED
        assertFalse(isTransactionInAccountBook(saleId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Tests that if the SaleTransaction has been closed and the provided cash is sufficient the payment is made
     * correctly and the required change is returned.
     */
    @Test
    public void testPayTransactionSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify receive correct change when paying for sale
        double change = 3;
        assertEquals(change, shop.receiveCashPayment(saleId, toBePaid+change), 0.001);
        totalBalance += toBePaid;

        // verify sale is in state PAID/COMPLETED
        assertTrue(isTransactionInAccountBook(saleId));

        // verify system's balance did update correctly
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Tests that if the SaleTransaction has already been paid, no new payment is made and an error value is returned
     */
    @Test
    public void testPayCompletedSale() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // pay for sale so it is in state PAID/COMPLETED
        assertEquals(0, shop.receiveCashPayment(saleId, toBePaid), 0.001);
        totalBalance += toBePaid;

        // try to pay for sale second time returns -1
        assertEquals(-1, shop.receiveCashPayment(saleId, toBePaid), 0.001);

        // verify sale remains in state PAID/COMPLETED
        assertTrue(isTransactionInAccountBook(saleId));

        // verify system's balance did not update a second time
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    private static boolean isTransactionInAccountBook(int id) throws Exception {
        return shop.getCreditsAndDebits(null, null).stream().anyMatch(b -> b.getBalanceId() == id);
    }
}

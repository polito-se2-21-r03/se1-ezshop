package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.Order;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNull;
import static it.polito.ezshop.TestHelpers.*;
import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.assertEquals;

public class EZShopTestReturnCashPayment {

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
    private static int totalBalance = 0;
    private static int saleId;
    private static int returnId;
    private static int returnedValue;

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
        Method defineCustomer = EZShop.class.getMethod("returnCashPayment", Integer.class);
        testAccessRights(defineCustomer, new Object[] {returnId},
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
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::returnCashPayment);
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
        assertEquals(-1, shop.returnCashPayment(nonExistentId), 0.001);
    }

    /**
     * Tests that an Order can not be paid using returnCashPayment, only ReturnTransactions should be paid using this method.
     */
    @Test
    public void testPayUnpaidOrderFails() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // issue an order
        int orderId = shop.issueOrder(productCode, 10, 1);

        // trying to pay for an order with returnCashPayment fails
        assertEquals(-1, shop.returnCashPayment(orderId), 0.001);

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
     * If the ReturnTransaction has net yet been closed an error value should be returned.
     */
    @Test
    public void testReturnCashForOpenReturnFails() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // open a return without closing it
        int openReturnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(openReturnId, productCode, 2);

        // trying to receive cash payment for a still open return fails
        assertEquals(-1, shop.returnCashPayment(openReturnId), 0.001);

        // verify return has not been recorded into the account book
        assertNull(getStatusOfTransaction(openReturnId));

        // verify system's balance did not change
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Tests that if the ReturnTransaction has been closed, the cash is returned correctly and the transaction is marked
     * as PAID/COMPLETED
     */
    @Test
    public void testReturnCashSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify receive correct amount of cash for return
        assertEquals(returnedValue, shop.returnCashPayment(returnId), 0.001);
        totalBalance -= returnedValue;

        // verify return is in state PAID/COMPLETED
        assertEquals(OperationStatus.COMPLETED, getStatusOfTransaction(returnId));

        // verify system's balance did update correctly
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Tests that if the ReturnTransaction has already been completed, requesting to return the same transaction for a
     * second time returns an error value
     */
    @Test
    public void testReturnCompletedReturnAgain() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // pay for return so it is in state PAID/COMPLETED
        assertEquals(returnedValue, shop.returnCashPayment(returnId), 0.001);
        totalBalance -= returnedValue;

        // try to ask for return a second time returns -1
        assertEquals(-1, shop.returnCashPayment(returnId), 0.001);

        // verify return remains in state PAID/COMPLETED
        assertEquals(OperationStatus.COMPLETED, getStatusOfTransaction(returnId));

        // verify system's balance did not update a second time
        assertEquals(totalBalance, shop.computeBalance(), 0.001);
    }

    /**
     * Gets the OperationStatus of the return transaction with the given ID or null if not found
     *
     * @param returnId ID of the requested ReturnTransaction
     * @return the Operation status of the return transaction as a String,
     *         null if not found
     */
    private static OperationStatus getStatusOfTransaction(int returnId) throws Exception {
        return shop.getCreditsAndDebits(null, null).stream()
                .filter(b -> b.getBalanceId() == returnId)
                .map(b -> ((BalanceOperationAdapter) b).getTransaction().getStatus())
                .findAny()
                .orElse(null);
    }
}

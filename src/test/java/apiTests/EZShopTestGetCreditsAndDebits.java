package apiTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.model.*;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static unitTests.TestHelpers.testAccessRights;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class EZShopTestGetCreditsAndDebits {

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Andrea", "123", Role.ADMINISTRATOR);

    private static LocalDate afterFirst;
    private static LocalDate beforeLast;

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login for shop setup
        shop.login(admin.getUsername(), admin.getPassword());

        // create product for transactions in shop
        String productCode = "12345678901231";
        int productId = shop.createProductType("description", productCode, 1, "note");
        shop.updatePosition(productId, "1-1-1");

        // set time to 5 days ago
        shop.setClock(Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-5)));

        // record a COMPLETED CREDIT transaction
        shop.recordBalanceUpdate(1000);

        // set time to 4 days ago
        shop.setClock(Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-4)));

        // store date after first transaction
        afterFirst = LocalDate.now(shop.getClock());

        // record a COMPLETED DEBIT transaction
        shop.recordBalanceUpdate(-10);

        // record a COMPLETED ORDER transaction
        int orderId = shop.payOrderFor(productCode, 10, 1);
        shop.recordOrderArrival(orderId);

        // set time to 3 days ago
        shop.setClock(Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-3)));

        // record a COMPLETED SALE transaction
        int saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, 10);
        shop.endSaleTransaction(saleId);
        shop.receiveCashPayment(saleId, 10);

        // store time before last few transactions
        beforeLast = LocalDate.now(shop.getClock());

        // record a COMPLETED RETURN transaction
        int returnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(returnId, productCode, 5);
        shop.endReturnTransaction(returnId, true);
        shop.returnCashPayment(returnId);

        // set time to 2 days ago
        shop.setClock(Clock.offset(Clock.systemDefaultZone(), Duration.ofDays(-2)));

        // add ORDER that is in PAID state
        shop.payOrderFor(productCode, 10, 1);

        // add SALE transaction that is in CLOSED state
        int saleId2 = shop.startSaleTransaction();
        shop.addProductToSale(saleId2, productCode, 10);
        shop.endSaleTransaction(saleId2);

        // add RETURN transaction that is in OPEN state
        shop.startReturnTransaction(saleId);

        // logout for tests
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("getCreditsAndDebits", LocalDate.class, LocalDate.class);
        testAccessRights(defineCustomer, new Object[] {null, null},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR});
    }

    /**
     * Test that all balance operations that have an impact on the total balance are returned if both start date and end
     * date are null
     */
    @Test
    public void testGetAll() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get list of all balance operations in order
        List<BalanceOperation> balanceOperations = shop.getCreditsAndDebits(null, null);
        balanceOperations.sort(Comparator.comparing(BalanceOperation::getDate));

        // verify correct amount of balance operations returned
        assertEquals(6, balanceOperations.size());

        // verify the correct balance operations were returned
        assertEquals(BalanceOperationAdapter.CREDIT, balanceOperations.get(0).getType());
        assertEquals(BalanceOperationAdapter.DEBIT, balanceOperations.get(1).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(2).getType());
        assertEquals(BalanceOperationAdapter.SALE, balanceOperations.get(3).getType());
        assertEquals(BalanceOperationAdapter.RETURN, balanceOperations.get(4).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(5).getType());
    }

    /**
     * Test that all balance operations after a given date that have an impact on the total balance are returned
     */
    @Test
    public void testStartDate() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get list of all balance operations in order
        List<BalanceOperation> balanceOperations = shop.getCreditsAndDebits(afterFirst, null);
        balanceOperations.sort(Comparator.comparing(BalanceOperation::getDate));

        // verify correct amount of balance operations returned
        assertEquals(5, balanceOperations.size());

        // verify the correct balance operations were returned
        assertEquals(BalanceOperationAdapter.DEBIT, balanceOperations.get(0).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(1).getType());
        assertEquals(BalanceOperationAdapter.SALE, balanceOperations.get(2).getType());
        assertEquals(BalanceOperationAdapter.RETURN, balanceOperations.get(3).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(4).getType());
    }

    /**
     * Test that all balance operations before a given date that have an impact on the total balance are returned
     */
    @Test
    public void testEndDate() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get list of all balance operations in order
        List<BalanceOperation> balanceOperations = shop.getCreditsAndDebits(null, beforeLast);
        balanceOperations.sort(Comparator.comparing(BalanceOperation::getDate));

        // verify correct amount of balance operations returned
        assertEquals(5, balanceOperations.size());

        // verify the correct balance operations were returned
        assertEquals(BalanceOperationAdapter.CREDIT, balanceOperations.get(0).getType());
        assertEquals(BalanceOperationAdapter.DEBIT, balanceOperations.get(1).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(2).getType());
        assertEquals(BalanceOperationAdapter.SALE, balanceOperations.get(3).getType());
        assertEquals(BalanceOperationAdapter.RETURN, balanceOperations.get(4).getType());
    }

    /**
     * Test that all balance operations between two given dates that have an impact on the total balance are returned
     */
    @Test
    public void testStartEndDate() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get list of all balance operations in order
        List<BalanceOperation> balanceOperations = shop.getCreditsAndDebits(afterFirst, beforeLast);
        balanceOperations.sort(Comparator.comparing(BalanceOperation::getDate));

        // verify correct amount of balance operations returned
        assertEquals(4, balanceOperations.size());

        // verify the correct balance operations were returned
        assertEquals(BalanceOperationAdapter.DEBIT, balanceOperations.get(0).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(1).getType());
        assertEquals(BalanceOperationAdapter.SALE, balanceOperations.get(2).getType());
        assertEquals(BalanceOperationAdapter.RETURN, balanceOperations.get(3).getType());
    }

    /**
     * Test that all balance operations between two given dates that have an impact on the total balance are returned
     * even if the dates order has been switched
     */
    @Test
    public void testReverseDate() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get list of all balance operations in order
        List<BalanceOperation> balanceOperations = shop.getCreditsAndDebits(beforeLast, afterFirst);
        balanceOperations.sort(Comparator.comparing(BalanceOperation::getDate));

        // verify correct amount of balance operations returned
        assertEquals(4, balanceOperations.size());

        // verify the correct balance operations were returned
        assertEquals(BalanceOperationAdapter.DEBIT, balanceOperations.get(0).getType());
        assertEquals(BalanceOperationAdapter.ORDER, balanceOperations.get(1).getType());
        assertEquals(BalanceOperationAdapter.SALE, balanceOperations.get(2).getType());
        assertEquals(BalanceOperationAdapter.RETURN, balanceOperations.get(3).getType());
    }
}

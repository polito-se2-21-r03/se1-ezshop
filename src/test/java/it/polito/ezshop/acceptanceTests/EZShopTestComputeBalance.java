package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.assertEquals;

public class EZShopTestComputeBalance {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.ADMINISTRATOR);

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());
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
     * Tests that the balance is computed correctly
     */
    @Test
    public void testComputeBalance() throws Exception {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // create product for transactions in shop
        String productCode = "12345678901231";
        int productId = shop.createProductType("description", productCode, 1, "note");
        shop.updatePosition(productId, "1-1-1");

        // record a CREDIT transaction
        shop.recordBalanceUpdate(1000);

        // record a DEBIT transaction
        shop.recordBalanceUpdate(-5);

        // record ORDER transaction
        int orderId = shop.payOrderFor(productCode, 320, 1);
        shop.recordOrderArrival(orderId);

        // record SALE transaction
        int saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, 20);
        shop.endSaleTransaction(saleId);
        shop.receiveCashPayment(saleId, 20);

        // record RETURN transaction
        int returnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(returnId, productCode, 10);
        shop.endReturnTransaction(returnId, true);
        shop.returnCashPayment(returnId);

        // add ORDER that is in PAID state
        shop.payOrderFor(productCode, 80, 1);

        // add SALE transaction that is in CLOSED state
        int saleId2 = shop.startSaleTransaction();
        shop.addProductToSale(saleId2, productCode, 160);
        shop.endSaleTransaction(saleId2);

        // add RETURN transaction that is in OPEN state
        shop.startReturnTransaction(saleId);

        // verify that balance is correct
        assertEquals(565, shop.computeBalance());
    }
}

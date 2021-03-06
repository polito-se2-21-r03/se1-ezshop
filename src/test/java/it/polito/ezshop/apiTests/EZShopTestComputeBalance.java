package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.assertEquals;

public class EZShopTestComputeBalance {

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("computeBalance");
        testAccessRights(defineCustomer, new Object[] {},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR});
    }

    /**
     * Tests that the balance is computed correctly
     */
    @Test
    public void testComputeBalance() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // create product for transactions in shop
        String productCode = "12345678901231";
        int productId = shop.createProductType("description", productCode, 1, "note");
        shop.updatePosition(productId, "1-1-1");

        // record a CREDIT transaction (balance = 1000)
        shop.recordBalanceUpdate(1000);
        assertEquals(1000, shop.computeBalance(), 0.001);

        // record a DEBIT transaction (balance = 995)
        shop.recordBalanceUpdate(-5);
        assertEquals(995, shop.computeBalance(), 0.001);

        // record ORDER transaction (balance = 675)
        int orderId = shop.payOrderFor(productCode, 320, 1);
        shop.recordOrderArrival(orderId);
        assertEquals(675, shop.computeBalance(), 0.001);

        // record SALE transaction (balance = 695)
        int saleId = shop.startSaleTransaction();
        shop.addProductToSale(saleId, productCode, 20);
        shop.endSaleTransaction(saleId);
        shop.receiveCashPayment(saleId, 20);
        assertEquals(695, shop.computeBalance(), 0.001);

        // record RETURN transaction (balance = 685)
        int returnId = shop.startReturnTransaction(saleId);
        shop.returnProduct(returnId, productCode, 10);
        shop.endReturnTransaction(returnId, true);
        shop.returnCashPayment(returnId);
        assertEquals(685, shop.computeBalance(), 0.001);

        // add ORDER that is in PAID state (balance = 605)
        shop.payOrderFor(productCode, 80, 1);
        assertEquals(605, shop.computeBalance(), 0.001);

        // add SALE transaction that is in CLOSED state (balance = 605)
        int saleId2 = shop.startSaleTransaction();
        shop.addProductToSale(saleId2, productCode, 160);
        shop.endSaleTransaction(saleId2);
        assertEquals(605, shop.computeBalance(), 0.001);

        // add RETURN transaction that is in OPEN state (balance = 605)
        shop.startReturnTransaction(saleId);
        assertEquals(605, shop.computeBalance(), 0.001);
    }
}

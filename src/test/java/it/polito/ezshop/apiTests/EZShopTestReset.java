package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EZShopTestReset {

    private static final String USERNAME = "Peter";
    private static final String PASSWORD = "123";
    private static final Role ROLE = Role.ADMINISTRATOR;

    private static final String CUSTOMER_NAME = "Jax";

    private static final String PRODUCT_CODE = "12345678901231";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final double PRODUCT_PRICE = 1.0;
    private static final String PRODUCT_NOTE = "note";

    private static final String PRODUCT_LOCATION = "1-1-1";

    private static final double INITIAL_BALANCE = 10000;

    private static final int ORDER_QUANTITY = 100;
    private static final double ORDER_PRICE = 1.5;

    private final EZShop shop = new EZShop();

    @Before
    public void beforeEach() throws Exception {

        // reset shop instance (otherwise persisted data is loaded automatically)
        shop.reset();

        // create a user
        shop.createUser(USERNAME, PASSWORD, ROLE.getValue());

        // login as user
        shop.login(USERNAME, PASSWORD);

        // add a customer
        shop.defineCustomer(CUSTOMER_NAME);

        // add a product
        int productID = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);

        // set product location
        shop.updatePosition(productID, PRODUCT_LOCATION);

        // add funds to the shop
        shop.recordBalanceUpdate(INITIAL_BALANCE);

        // add the product to the inventory
        int orderID = shop.payOrderFor(PRODUCT_CODE, ORDER_QUANTITY, ORDER_PRICE);
        shop.recordOrderArrival(orderID);
    }

    /**
     * Tests that resetting a shop with some entries works correctly
     */
    @Test
    public void testShopReset() throws Exception {

        // reset shop from beforeEach
        shop.reset();

        // check reset was correct
        verifyShopIsInBaseState(shop);
    }

    /**
     * Tests that resetting a shop, that was previously restored from persistently stored data, works correctly.
     */
    @Test
    public void testShopResetAfterRestore() throws Exception {

        // restore shop
        EZShop restoredShop = new EZShop(EZShop.PERSISTENCE_PATH);

        // reset shop
        restoredShop.reset();

        // verify reset was correct
        verifyShopIsInBaseState(restoredShop);
    }

    /**
     * Verify that the shop has reset correctly and is in its base state with no users, products, customers or
     * transactions and a balance of 0. If the shop was not reset correctly the test fails.
     *
     * @param shop shop instance that should have been reset
     */
    private void verifyShopIsInBaseState(EZShop shop) throws Exception {

        // there is no user logged in
        assertFalse(shop.logout());

        // create user (making sure user does not already exist) and login to access API functions
        assertTrue(shop.createUser(USERNAME, PASSWORD, ROLE.getValue()) > 0);
        shop.login(USERNAME, PASSWORD);

        // all stored lists are empty (except just created user)
        assertEquals(1, shop.getAllUsers().size());
        assertEquals(0, shop.getAllCustomers().size());
        assertEquals(0, shop.getAllProductTypes().size());
        assertEquals(0, shop.getAllOrders().size());
        assertEquals(0, shop.getAccountBook().getAllTransactions().size());
        assertEquals(0, shop.getAccountBook().getBalance(), 0.001);
    }
}

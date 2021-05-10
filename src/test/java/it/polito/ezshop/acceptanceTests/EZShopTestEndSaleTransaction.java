package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidPaymentException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests on the EZShop.endSaleTransaction() method.
 */
public class EZShopTestEndSaleTransaction {

    // product
    private static final String PRODUCT_CODE = "12345678901231";
    private static final Double PRODUCT_PRICE = 15.0;
    private static final Integer PRODUCT_QUANTITY = 10;
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final String PRODUCT_NOTE = "note";
    private static final String PRODUCT_POSITION = "1-1-1";

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // add a product to the shop
        int id1 = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
        shop.updatePosition(id1, PRODUCT_POSITION);
        shop.updateQuantity(id1, PRODUCT_QUANTITY);

        // create a new transaction
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, PRODUCT_CODE, 1);
    }

    /**
     * Tests that access rights are handled correctly by endSaleTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("endSaleTransaction", Integer.class);
        Object[] params = {tid};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::computePointsForSale);
    }

    /**
     * If the transaction does not exists, the method should return false
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.endSaleTransaction(tid + 1));
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testEndSaleTransactionSuccessfully() throws Exception {
        // first time the method should return true
        assertTrue(shop.endSaleTransaction(tid));
        // afterwards, the method should return false
        assertFalse(shop.endSaleTransaction(tid));

        // pay the transaction and call the method again
        shop.receiveCashPayment(tid, 50.0);
        assertFalse(shop.endSaleTransaction(tid));
    }

}

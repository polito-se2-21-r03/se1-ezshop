package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestPayOrder {
    private static final EZShop shop = new EZShop();
    private static  User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProductType product;
    private Integer target;
    @Before
    public void beforeEach() throws
            InvalidProductCodeException, InvalidProductDescriptionException, InvalidQuantityException, InvalidPricePerUnitException,
            InvalidProductIdException, InvalidOrderIdException, UnauthorizedException,
            InvalidUsernameException, InvalidPasswordException, InvalidLocationException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a new product that will be updated later in the tests
        String barcode = "12345678901231";
        shop.createProductType("desc", "12345678901231", 10.0, "note");
        product = shop.getProductTypeByBarCode(barcode);
        target = shop.issueOrder(product.getBarCode(), 100, 5.0);
    }
    /**
     * Tests that access rights are handled correctly by issueOrder.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("payOrder", Integer.class);
        Object[] params = {target};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }
    /**
     * If the order id is null or <= 0, the method should throw InvalidOrderIdException
     */
    @Test()
    public void testInvalidOrderId() {
        // test values for the product code parameter
        // "12345678901232" is an invalid product code (wrong check digit)
        Arrays.asList(null, 0, -1).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidOrderIdException.class, () -> {
                // try to update a product with the boundary value
                shop.payOrder(value);
            });
        });
    }
    /**
     * If an order with that (valid) id does not exist, the method should return false
     */
    @Test()
    public void testFalsePayOrder() throws UnauthorizedException, InvalidOrderIdException {
        // set balance to €4000.0
        shop.recordBalanceUpdate(4000.0);
        // check that balance is actually 4000.0 euros
        assertEquals(4000.0, shop.computeBalance(), 0.001);

        // Set a random order id which is not related to any orders and check if the method returns false
        assertFalse(shop.payOrder(target+1));

        // set balance to €1.0
        shop.recordBalanceUpdate(-3999.0);
        // check that balance is actually 1.0 euro
        assertEquals(1, shop.computeBalance(), 0.001);

        // PayOrder should return false because there are not enough funds
        assertFalse(shop.payOrder(target));
    }

    @Test()
    public void testValidPayOrder() throws UnauthorizedException, InvalidOrderIdException {
        // set balance to €4000.0
        shop.recordBalanceUpdate(4000.0);
        // check that balance is actually 4000.0 euros
        assertEquals(4000.0, shop.computeBalance(), 0.001);
        assertTrue(shop.payOrder(target));

        // I'd like to check if Operation status of the order is actually paid but i cannot do this. Something like the line above
        // assertEquals(OperationStatus.PAID, target);

    }
}

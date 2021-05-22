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

public class EZShopTestRecordOrderArrival {
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
        shop.updatePosition(product.getId(), "1-2-3");
        target = shop.issueOrder(product.getBarCode(), 100, 5.0);
    }
    /**
     * Tests that access rights are handled correctly by recordOrderArrival.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("recordOrderArrival", Integer.class);
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
                shop.recordOrderArrival(value);
            });
        });
    }
    /**
     * Simulating the successful payment of an order, it should return true
     */
    @Test()
    public void testValidRecordOrderArrival() throws InvalidLocationException, UnauthorizedException, InvalidOrderIdException {
        // set balance to €4000.0
        shop.recordBalanceUpdate(4000.0);
        // check that balance is actually 4000.0 euros
        assertEquals(4000.0, shop.computeBalance(), 0.001);

        // Change status to PAID
        shop.payOrder(target);

        assertTrue(shop.recordOrderArrival(target));
    }
    /**
     * Simulating the unsuccessful payment of an order, it should return false
     */
    @Test()
    public void testInvalidRecordOrderArrival() throws InvalidLocationException, UnauthorizedException, InvalidOrderIdException {
        // set balance to €4000.0
        shop.recordBalanceUpdate(40.0);
        // check that balance is actually 4000.0 euros
        assertEquals(40.0, shop.computeBalance(), 0.001);

        // Change status to PAID
        shop.payOrder(target);

        assertFalse(shop.recordOrderArrival(target));
    }

}

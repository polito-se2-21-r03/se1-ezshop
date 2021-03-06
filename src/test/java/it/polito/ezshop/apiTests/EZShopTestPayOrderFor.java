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

public class EZShopTestPayOrderFor {
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
    @Before
    public void beforeEach() throws
            InvalidProductCodeException, InvalidProductDescriptionException, InvalidPricePerUnitException,
            UnauthorizedException,
            InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
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
    }
    /**
     * Tests that access rights are handled correctly by issueOrder.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("payOrderFor", String.class, int.class, double.class);
        Object[] params = {product.getBarCode(), 100, 5.0};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }
    /**
     * If the barcode is null or invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        // "12345678901232" is an invalid product code (wrong check digit)
        Arrays.asList(null, "", "123456789B123A", "12345678901232").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.payOrderFor(value, 100, 5.0);
            });
        });
    }
    /**
     * If the quantity is <= 0, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidQuantity() {
        // boundary values for the id parameter
        Arrays.asList(-1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidQuantityException.class, () -> {
                // try to update a product with the boundary value
                shop.payOrderFor(product.getBarCode(), value, 5.0);
            });
        });
    }
    /**
     * If the price per unit is <= 0, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidPricePerUnit() {
        // boundary values for the id parameter
        Arrays.asList(-1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidPricePerUnitException.class, () -> {
                // try to update a product with the boundary value
                shop.payOrderFor(product.getBarCode(), 100, value);
            });
        });
    }
    /**
     * If a product with that (valid) barcode does not exist, the method should return -1
     */
    @Test()
    public void testNonExistingProduct() throws UnauthorizedException, InvalidQuantityException,
            InvalidProductCodeException, InvalidPricePerUnitException {
        Integer id = shop.payOrderFor("9788879924337", 100, 5.0);
        assertNotNull(id);
        assertEquals(new Integer(-1), id);
    }
    @Test()
    public void testInvalidPayOrderFor() throws UnauthorizedException, InvalidQuantityException,
            InvalidProductCodeException, InvalidPricePerUnitException {
        // set balance to ???4.0
        shop.recordBalanceUpdate(4.0);
        // check that balance is actually 4.0 euros
        assertEquals(4.0, shop.computeBalance(), 0.001);

        Integer id = shop.payOrderFor(product.getBarCode(), 100, 5.0);
        assertNotNull(id);
        assertEquals(new Integer(-1), id);
    }
    @Test()
    public void testValidPayOrderFor() throws UnauthorizedException, InvalidQuantityException,
            InvalidProductCodeException, InvalidPricePerUnitException {
        // set balance to ???4000.0
        shop.recordBalanceUpdate(4000.0);
        // check that balance is actually 4000.0 euros
        assertEquals(4000.0, shop.computeBalance(), 0.001);

        Integer id = shop.payOrderFor(product.getBarCode(), 100, 5.0);
        assertNotNull(id);
        assertTrue(id > 0);
    }
}

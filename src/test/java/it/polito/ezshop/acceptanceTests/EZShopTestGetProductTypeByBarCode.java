package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.acceptanceTests.TestHelpers.assertThrows;
import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getProductTypeByBarCode() method.
 */
public class EZShopTestGetProductTypeByBarCode {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final String PRODUCT_CODE_3 = "123456789012";

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    @Before
    public void beforeEach() throws
            InvalidPricePerUnitException, InvalidProductDescriptionException, InvalidProductCodeException,
            UnauthorizedException, InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a product p1 with barcode PRODUCT_CODE_1
        shop.createProductType("desc", PRODUCT_CODE_1, 10.0, "note");

        // insert a product p2 with barcode PRODUCT_CODE_2
        shop.createProductType("desc", PRODUCT_CODE_2, 10.0, "note");
    }

    /**
     * Tests that access rights are handled correctly by getProductTypeByBarCode.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("getProductTypeByBarCode", String.class);
        Object[] params = {PRODUCT_CODE_1};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        // "12345678901232" is an invalid product code (wrong check digit)
        Arrays.asList(null, "", "123456789B123A", "12345678901232").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.getProductTypeByBarCode(value);
            });
        });
    }

    /**
     * If a product with the given code does not exists, the method should return null.
     */
    @Test()
    public void testNonExistingProductCode() throws InvalidProductCodeException, UnauthorizedException {
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_3));
    }

    /**
     * If a product with the given code exists, the method should return it.
     */
    @Test()
    public void testExistingProductCodes() throws InvalidProductCodeException, UnauthorizedException {
        ProductType result = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        assertNotNull(result);
        assertEquals(PRODUCT_CODE_1, result.getBarCode());

        result = shop.getProductTypeByBarCode(PRODUCT_CODE_2);
        assertNotNull(result);
        assertEquals(PRODUCT_CODE_2, result.getBarCode());
    }

}

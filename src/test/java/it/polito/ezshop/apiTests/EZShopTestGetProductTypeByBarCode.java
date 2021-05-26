package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getProductTypeByBarCode() method.
 */
public class EZShopTestGetProductTypeByBarCode {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final String PRODUCT_CODE_3 = "123456789012";

    private static final String PRODUCT_DESCRIPTION = "description";
    private static final double PRODUCT_PRICE = 10.0;
    private static final String PRODUCT_NOTE = "note";

    private final EZShopInterface shop = new EZShop();
    private final User admin;

    public EZShopTestGetProductTypeByBarCode () throws Exception {
        admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
    }

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a product with barcode PRODUCT_CODE_1
        shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE_1, PRODUCT_PRICE, PRODUCT_NOTE);

        // insert a product with barcode PRODUCT_CODE_2
        shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE_2, PRODUCT_PRICE, PRODUCT_NOTE);
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
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test invalid values for the product code parameter
        // for each invalid value check that the correct exception is thrown
        for (String value : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> shop.getProductTypeByBarCode(value));
        }
    }


    /**
     * If a product with the given code does not exists, the method should return null.
     */
    @Test()
    public void testNonExistingProductCode() throws Exception {
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_3));
    }

    /**
     * If a product with the given code exists, the method should return it.
     */
    @Test()
    public void testExistingProductCodes() throws Exception {
        ProductType result = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        assertNotNull(result);
        assertEquals(PRODUCT_CODE_1, result.getBarCode());

        result = shop.getProductTypeByBarCode(PRODUCT_CODE_2);
        assertNotNull(result);
        assertEquals(PRODUCT_CODE_2, result.getBarCode());
    }

}

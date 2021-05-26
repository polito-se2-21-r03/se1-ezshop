package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductType() method.
 */
public class EZShopTestDeleteProductType {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_CODE_2 = "1234567890128";

    private static final String PRODUCT_DESCRIPTION = "description";
    private static final double PRODUCT_PRICE = 10.0;
    private static final String PRODUCT_NOTE = "note";

    private final EZShopInterface shop = new EZShop();
    private final User admin;

    public EZShopTestDeleteProductType() throws Exception {
        admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
    }

    private ProductType product1, product2;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a few products
        shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE_1, PRODUCT_PRICE, PRODUCT_NOTE);
        product1 = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE_2, PRODUCT_PRICE, PRODUCT_NOTE);
        product2 = shop.getProductTypeByBarCode(PRODUCT_CODE_2);
    }

    /**
     * Tests that access rights are handled correctly by deleteProductType.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteProductType", Integer.class);
        Object[] params = {product1.getId()};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidId() throws Exception {
        // boundary values for the id parameter
        // for each boundary value check that the correct exception is thrown
        for (Integer value : TestHelpers.invalidProductIDs) {
            assertThrows(InvalidProductIdException.class, () -> {
                // try to update a product with the boundary value
                shop.deleteProductType(value);
            });

            // verify both products are still there
            assertNotNull(shop.getProductTypeByBarCode(PRODUCT_CODE_1));
            assertNotNull(shop.getProductTypeByBarCode(PRODUCT_CODE_2));
        }
    }

    /**
     * Nominal case (authorized user, valid id)
     */
    @Test()
    public void testValid() throws Exception {
        assertTrue(shop.deleteProductType(product1.getId()));
        // verify if the product was actually removed
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_1));

        assertTrue(shop.deleteProductType(product2.getId()));
        // verify if the product was actually removed
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_2));

        // the product was previously removed -> the method should return false
        assertFalse(shop.deleteProductType(product1.getId()));
    }

}

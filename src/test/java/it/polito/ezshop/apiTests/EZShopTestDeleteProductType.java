package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductType() method.
 */
public class EZShopTestDeleteProductType {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_CODE_2 = "1234567890128";

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProductType p1, p2;

    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            InvalidPricePerUnitException, InvalidProductDescriptionException, InvalidProductCodeException,
            UnauthorizedException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a few products
        shop.createProductType("desc", PRODUCT_CODE_1, 10.0, "note");
        p1 = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        shop.createProductType("desc", PRODUCT_CODE_2, 10.0, "note");
        p2 = shop.getProductTypeByBarCode(PRODUCT_CODE_2);
    }

    /**
     * Tests that access rights are handled correctly by deleteProductType.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteProductType", Integer.class);
        Object[] params = {p1.getId()};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidId() {
        // boundary values for the id parameter
        Arrays.asList(null, -1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductIdException.class, () -> {
                // try to update a product with the boundary value
                shop.deleteProductType(value);
            });
        });
    }

    /**
     * Nominal case (authorized user, valid id)
     */
    @Test()
    public void testValid() throws UnauthorizedException,
            InvalidProductIdException, InvalidProductCodeException {
        assertTrue(shop.deleteProductType(p1.getId()));
        // verify if the product was actually removed
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_1));

        assertTrue(shop.deleteProductType(p2.getId()));
        // verify if the product was actually removed
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_2));

        // the product was previously removed -> the method should return false
        assertFalse(shop.deleteProductType(p1.getId()));
    }

}

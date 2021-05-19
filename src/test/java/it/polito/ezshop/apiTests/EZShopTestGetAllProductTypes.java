package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getAllProductTypes() method.
 */
public class EZShopTestGetAllProductTypes {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final String PRODUCT_CODE_3 = "123456789012";

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());
    }

    /**
     * Tests that access rights are handled correctly by getProductTypeByBarCode.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("getAllProductTypes");
        Object[] params = {};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * The method should initially return an empty list.
     * After a new product is inserted, the method should return a list that contains
     * the new product.
     */
    @Test()
    public void testValid() throws UnauthorizedException, InvalidProductDescriptionException,
            InvalidProductCodeException, InvalidPricePerUnitException {
        List<ProductType> products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(0, products.size());

        // insert a few products
        shop.createProductType("desc", PRODUCT_CODE_1, 10.0, "note");
        shop.createProductType("desc", PRODUCT_CODE_2, 10.0, "note");
        shop.createProductType("desc", PRODUCT_CODE_3, 10.0, "note");

        products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(3, products.size());

        // check if the list contains the expected products
        List<String> barcodes = products.stream().map(ProductType::getBarCode).collect(Collectors.toList());
        assertTrue(barcodes.contains(PRODUCT_CODE_1));
        assertTrue(barcodes.contains(PRODUCT_CODE_2));
        assertTrue(barcodes.contains(PRODUCT_CODE_3));
    }

}

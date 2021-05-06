package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests on the EZShop.getAllProductTypes() method.
 */
public class EZShopTestGetAllProductTypes {

    private static final User cashier = new User(1, "cashier", "cashier", Role.CASHIER);
    private static final User shopManager = new User(2, "shopManager", "shopManager", Role.SHOP_MANAGER);
    private static final User admin = new User(3, "administrator", "administrator", Role.ADMINISTRATOR);
    private static final EZShop shop = new EZShop();

    /**
     * Add new users to the shop.
     */
    @BeforeClass
    public static void init() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // create users
        shop.createUser(cashier.getUsername(), cashier.getPassword(), cashier.getRole());
        shop.createUser(shopManager.getUsername(), shopManager.getPassword(), shopManager.getRole());
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
    }

    /**
     * Log out the current user (if any) before each test.
     */
    @Before
    public void logout() {
        shop.logout();
    }

    /**
     * If no user is currently logged in, the method should throw UnauthorizedException.
     */
    @Test(expected = UnauthorizedException.class)
    public void testUnauthorizedUser() throws UnauthorizedException {
        shop.getAllProductTypes();
    }

    /**
     * If a cashier is currently logged in, the method should NOT throw any exceptions.
     */
    @Test()
    public void testCashier() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.getAllProductTypes();
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw any exceptions.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        shop.getAllProductTypes();
    }

    /**
     * If an administrator is currently logged in, the method should NOT throw any exceptions.
     */
    @Test()
    public void testAdministrator() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.getAllProductTypes();
    }

    /**
     * The method should initially return an empty list.
     * After a new product is inserted, the method should return a list that contains
     * the new product.
     */
    @Test()
    public void testValid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        List<ProductType> products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(0, products.size());

        shop.createProductType("desc", "12345678901231", 10.0, "note");

        products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(products.get(0).getBarCode(), "12345678901231");
    }

}

package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductType() method.
 */
public class EZShopTestDeleteProductType {

    private static final User cashier = new User(1, "cashier", "cashier", Role.CASHIER);
    private static final User shopManager = new User(2, "shopManager", "shopManager", Role.SHOP_MANAGER);
    private static final User admin = new User(3, "administrator", "administrator", Role.ADMINISTRATOR);
    private static final EZShop shop = new EZShop();

    // barcode of the product to be removed
    private static final String barcode = "12345678901231";

    // id of the product to be removed
    private Integer productToRemove;


    /**
     * Reset the shop before each test.
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            InvalidPricePerUnitException, InvalidProductDescriptionException, InvalidProductCodeException,
            UnauthorizedException {
        shop.reset();

        // create users
        shop.createUser(cashier.getUsername(), cashier.getPassword(), cashier.getRole());
        shop.createUser(shopManager.getUsername(), shopManager.getPassword(), shopManager.getRole());
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());

        // add a product to the inventory
        String description = "desc";
        double price = 10.0;
        String note = "note";

        shop.login(admin.getUsername(), admin.getPassword());
        productToRemove = shop.createProductType(description, barcode, price, note);
        shop.logout();
    }

    /**
     * If the id is null, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testNullId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.deleteProductType(null);
    }

    /**
     * If the id is negative, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testNegativeId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.deleteProductType(-5);
    }

    /**
     * If the id is zero, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testZeroId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.deleteProductType(0);
    }

    /**
     * If a cashier is currently logged in, the method should throw UnauthorizedException.
     */
    @Test(expected = UnauthorizedException.class)
    public void testCashier() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException {
        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.deleteProductType(productToRemove);
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        shop.deleteProductType(productToRemove);
    }

    /**
     * Nominal case (authorized user, valid id)
     */
    @Test()
    public void testValid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException, InvalidProductCodeException {
        shop.login(admin.getUsername(), admin.getPassword());

        boolean result = shop.deleteProductType(productToRemove + 1);
        assertFalse(result);

        result = shop.deleteProductType(productToRemove);
        assertTrue(result);

        assertNull(shop.getProductTypeByBarCode(barcode));
    }

}

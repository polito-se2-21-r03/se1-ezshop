package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductType() method.
 */
public class EZShopTestDeleteProductType extends EZShopTestBase {

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
        super.initializeUsers();

        // add a product to the inventory
        String description = "desc";
        double price = 10.0;
        String note = "note";

        loginAs(admin);
        productToRemove = shop.createProductType(description, barcode, price, note);
        shop.logout();
    }

    /**
     * If the id is null, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testNullId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        loginAs(admin);

        shop.deleteProductType(null);
    }

    /**
     * If the id is negative, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testNegativeId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        loginAs(admin);

        shop.deleteProductType(-5);
    }

    /**
     * If the id is zero, the method should throw InvalidProductIdException.
     */
    @Test(expected = InvalidProductIdException.class)
    public void testZeroId() throws UnauthorizedException, InvalidProductIdException, InvalidPasswordException,
            InvalidUsernameException {
        loginAs(admin);

        shop.deleteProductType(0);
    }

    /**
     * If a cashier is currently logged in, the method should throw UnauthorizedException.
     */
    @Test(expected = UnauthorizedException.class)
    public void testCashier() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException {
        loginAs(cashier);

        shop.deleteProductType(productToRemove);
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException {
        loginAs(shopManager);

        shop.deleteProductType(productToRemove);
    }

    /**
     * Nominal case (authorized user, valid id)
     */
    @Test()
    public void testValid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductIdException, InvalidProductCodeException {
        loginAs(admin);

        boolean result = shop.deleteProductType(productToRemove + 1);
        assertFalse(result);

        result = shop.deleteProductType(productToRemove);
        assertTrue(result);

        assertNull(shop.getProductTypeByBarCode(barcode));
    }

}

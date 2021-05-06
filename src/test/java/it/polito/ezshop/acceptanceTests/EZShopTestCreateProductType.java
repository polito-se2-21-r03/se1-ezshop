package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests on the EZShop.createProductType() method.
 */
public class EZShopTestCreateProductType {

    private static final User cashier = new User(1, "cashier", "cashier", Role.CASHIER);
    private static final User shopManager = new User(2, "shopManager", "shopManager", Role.SHOP_MANAGER);
    private static final User admin = new User(3, "administrator", "administrator", Role.ADMINISTRATOR);
    private static final EZShop shop = new EZShop();


    /**
     * Log out the current user (if any) before each test.
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        shop.reset();

        // create users
        shop.createUser(cashier.getUsername(), cashier.getPassword(), cashier.getRole());
        shop.createUser(shopManager.getUsername(), shopManager.getPassword(), shopManager.getRole());
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
    }

    /**
     * If a the description of the product is null, the method should throw an exception
     */
    @Test(expected = InvalidProductDescriptionException.class)
    public void testNullDescription() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType(null, "code", 10.0, "note");
    }

    /**
     * If a the description of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductDescriptionException.class)
    public void testEmptyDescription() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType("", "code", 10.0, "note");
    }

    /**
     * If a the barcode of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testNullBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType("desc", null, 10.0, "note");
    }

    /**
     * If a the barcode of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testEmptyBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType("desc", "", 10.0, "note");
    }

    /**
     * If a the barcode of the product is invalid, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testInvalidBarcode1() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        // the check digit of the barcode is not correct
        shop.createProductType("desc", "12345678901235", 10.0, "note");
    }

    /**
     * If a the barcode of the product is invalid, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testInvalidBarcode2() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        // the barcode is not a number
        shop.createProductType("desc", "1234567890123A", 10.0, "note");
    }

    /**
     * If a the price of the product is negative, the method should throw an exception
     */
    @Test(expected = InvalidPricePerUnitException.class)
    public void testNegativePrice() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType("desc", "12345678901231", -10.0, "note");
    }

    /**
     * If a the price of the product is zero, the method should throw an exception
     */
    @Test(expected = InvalidPricePerUnitException.class)
    public void testZeroPrice() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        // the barcode is not a number
        shop.createProductType("desc", "12345678901231", 0.0, "note");
    }

    /**
     * If no user is currently logged in, the method should throw UnauthorizedException.
     */
    @Test(expected = UnauthorizedException.class)
    public void testUnauthorizedUser() throws UnauthorizedException, InvalidProductDescriptionException,
            InvalidProductCodeException, InvalidPricePerUnitException {
        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * If a cashier is currently logged in, the method should throw UnauthorizedException.
     */
    @Test(expected = UnauthorizedException.class)
    public void testCashier() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * If an admin is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testAdmin() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(admin.getUsername(), admin.getPassword());

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * Nominal case (authorized user, valid parameters)
     */
    @Test()
    public void testValid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        String description = "desc";
        String barcode = "12345678901231";
        double price = 10.0;
        String note = "note";

        Integer id = shop.createProductType(description, barcode, price, note);
        assertNotNull(id);
        assertTrue(id > 0);

        ProductType p = shop.getProductTypeByBarCode(barcode);
        assertNotNull(p);
        assertEquals(description, p.getProductDescription());
        assertEquals(barcode, p.getBarCode());
        assertEquals(price, p.getPricePerUnit(), 0.001);
        assertEquals(note, p.getNote());
    }

    /**
     * If a product with the same barcode already exists, the method should return -1
     */
    @Test()
    public void testDuplicatedBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        String description = "desc";
        String barcode = "12345678901231";
        double price = 10.0;
        String note = "note";

        Integer id = shop.createProductType(description, barcode, price, note);
        assertNotNull(id);
        assertTrue(id > 0);

        id = shop.createProductType(description, barcode, price, note);
        assertNotNull(id);
        assertEquals(-1, (int) id);
    }

    /**
     * If the note is null, an empty string should be saved.
     */
    @Test()
    public void testNullNote() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        shop.login(shopManager.getUsername(), shopManager.getPassword());

        String description = "desc";
        String barcode = "12345678901231";
        double price = 10.0;
        String note = null;

        Integer id = shop.createProductType(description, barcode, price, note);
        assertNotNull(id);
        assertTrue(id > 0);

        ProductType p = shop.getProductTypeByBarCode(barcode);
        assertNotNull(p);
        assertEquals("", p.getNote());
    }

}

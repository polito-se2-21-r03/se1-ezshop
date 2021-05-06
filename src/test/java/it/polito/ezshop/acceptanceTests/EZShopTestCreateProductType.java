package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests on the EZShop.createProductType() method.
 */
public class EZShopTestCreateProductType extends EZShopTestBase {

    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        super.initializeUsers();
    }

    /**
     * If a the description of the product is null, the method should throw an exception
     */
    @Test(expected = InvalidProductDescriptionException.class)
    public void testNullDescription() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType(null, "code", 10.0, "note");
    }

    /**
     * If a the description of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductDescriptionException.class)
    public void testEmptyDescription() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType("", "code", 10.0, "note");
    }

    /**
     * If a the barcode of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testNullBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType("desc", null, 10.0, "note");
    }

    /**
     * If a the barcode of the product is empty, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testEmptyBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType("desc", "", 10.0, "note");
    }

    /**
     * If a the barcode of the product is invalid, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testInvalidBarcode1() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        // the check digit of the barcode is not correct
        shop.createProductType("desc", "12345678901235", 10.0, "note");
    }

    /**
     * If a the barcode of the product is invalid, the method should throw an exception
     */
    @Test(expected = InvalidProductCodeException.class)
    public void testInvalidBarcode2() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        // the barcode is not a number
        shop.createProductType("desc", "1234567890123A", 10.0, "note");
    }

    /**
     * If a the price of the product is negative, the method should throw an exception
     */
    @Test(expected = InvalidPricePerUnitException.class)
    public void testNegativePrice() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType("desc", "12345678901231", -10.0, "note");
    }

    /**
     * If a the price of the product is zero, the method should throw an exception
     */
    @Test(expected = InvalidPricePerUnitException.class)
    public void testZeroPrice() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

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
        loginAs(cashier);

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(shopManager);

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * If an admin is currently logged in, the method should NOT throw UnauthorizedException.
     */
    @Test()
    public void testAdmin() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(admin);

        shop.createProductType("desc", "12345678901231", 10.0, "note");
    }

    /**
     * Nominal case (authorized user, valid parameters)
     */
    @Test()
    public void testValid() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(shopManager);

        ProductType model = generateValidProductType();

        Integer id = shop.createProductType(model.getProductDescription(), model.getBarCode(),
                model.getPricePerUnit(), model.getNote());
        assertNotNull(id);
        assertTrue(id > 0);

        ProductType p = shop.getProductTypeByBarCode(model.getBarCode());
        assertNotNull(p);
        assertEquals(model.getProductDescription(), p.getProductDescription());
        assertEquals(model.getBarCode(), p.getBarCode());
        assertEquals(model.getPricePerUnit(), p.getPricePerUnit(), 0.001);
        assertEquals(model.getNote(), p.getNote());
    }

    /**
     * If a product with the same barcode already exists, the method should return -1
     */
    @Test()
    public void testDuplicatedBarcode() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(shopManager);

        ProductType model = generateValidProductType();

        Integer id = shop.createProductType(model.getProductDescription(), model.getBarCode(),
                model.getPricePerUnit(), model.getNote());
        assertNotNull(id);
        assertTrue(id > 0);

        id = shop.createProductType(model.getProductDescription(), model.getBarCode(),
                model.getPricePerUnit(), model.getNote());
        assertNotNull(id);
        assertEquals(-1, (int) id);
    }

    /**
     * If the note is null, an empty string should be saved.
     */
    @Test()
    public void testNullNote() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        loginAs(shopManager);

        ProductType model = generateValidProductType();

        Integer id = shop.createProductType(model.getProductDescription(), model.getBarCode(),
                model.getPricePerUnit(), null);
        assertNotNull(id);
        assertTrue(id > 0);

        ProductType p = shop.getProductTypeByBarCode(model.getBarCode());
        assertNotNull(p);
        assertEquals("", p.getNote());
    }

}

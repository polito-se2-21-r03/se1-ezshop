package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests on the EZShop.getAllProductTypes() method.
 */
public class EZShopTestGetAllProductTypes extends EZShopTestBase {

    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        super.initializeUsers();
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
        loginAs(cashier);

        shop.getAllProductTypes();
    }

    /**
     * If a shop manager is currently logged in, the method should NOT throw any exceptions.
     */
    @Test()
    public void testShopManager() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        loginAs(shopManager);

        shop.getAllProductTypes();
    }

    /**
     * If an administrator is currently logged in, the method should NOT throw any exceptions.
     */
    @Test()
    public void testAdministrator() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {
        loginAs(admin);

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
        loginAs(admin);

        List<ProductType> products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(0, products.size());

        ProductType model = generateValidProductType();
        shop.createProductType(model.getProductDescription(), model.getBarCode(),
                model.getPricePerUnit(), model.getNote());

        products = shop.getAllProductTypes();
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(products.get(0).getBarCode(), "12345678901231");
    }

}

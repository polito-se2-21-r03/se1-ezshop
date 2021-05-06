package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the EZShop.defineCustomer(String) method.
 */
public class EZShopTestDefineCustomer {

    private static final User cashier = new User(1, "cashier", "cashier", Role.CASHIER);
    private static final User shopManager = new User(2, "shopManager", "shopManager", Role.SHOP_MANAGER);
    private static final User admin = new User(3, "administrator", "administrator", Role.ADMINISTRATOR);
    private static final EZShop shop = new EZShop();

    /**
     * Creates a clean shop instance for each test
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
     * If the currently logged in user is a cashier, she has sufficient rights to call the method
     */
    @Test
    public void testCashierIsAuthorized() throws InvalidCustomerNameException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.defineCustomer("Pietro");
    }

    /**
     * If the currently logged in user is a shop manager, she has sufficient rights to call the method
     */
    @Test
    public void testShopManagerIsAuthorized() throws InvalidCustomerNameException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException {

        shop.login(shopManager.getUsername(), shopManager.getPassword());

        shop.defineCustomer("Pietro");
    }

    /**
     * If the currently logged in user is an admin, she has sufficient rights to call the method
     */
    @Test
    public void testAdminIsAuthorized() throws InvalidCustomerNameException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException {

        shop.login(admin.getUsername(), admin.getPassword());

        shop.defineCustomer("Pietro");
    }

    /**
     * If no user is currently logged in an UnauthorizedException is thrown
     */
    @Test(expected = UnauthorizedException.class)
    public void testNoLoggedInUserCausesUnauthorizedException() throws InvalidCustomerNameException, UnauthorizedException {
        shop.defineCustomer("Pietro");
    }

    /**
     * If an empty customerName is provided an InvalidCustomerNameException should be thrown
     */
    @Test(expected = InvalidCustomerNameException.class)
    public void testInvalidCustomerNameExceptionIfNameEmpty() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.defineCustomer("");
    }

    /**
     * If null is passed as an argument for customerName an InvalidCustomerNameException should be thrown
     */
    @Test(expected = InvalidCustomerNameException.class)
    public void testInvalidCustomerNameExceptionIfNameNull() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        shop.defineCustomer(null);
    }

    /**
     * If a customer name is provided, that is not unique within the system an error value should be returned
     */
    @Test
    public void testOnlyUniqueCustomerNamesAllowed() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        assertTrue(shop.defineCustomer("Pietro") > 0);
        assertTrue(shop.defineCustomer("Pietro") == -1);
    }

    /**
     * Define a single customer successfully
     */
    @Test
    public void testDefineSingleCustomerSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        assertTrue(shop.defineCustomer("Pietro") > 0);
    }

    /**
     * Define a set of customers successfully
     */
    @Test
    public void testDefineManyCustomersSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(cashier.getUsername(), cashier.getPassword());

        assertTrue(shop.defineCustomer("Pietro") > 0);
        assertTrue(shop.defineCustomer("Andrea") > 0);
        assertTrue(shop.defineCustomer("Sarah") > 0);
        assertTrue(shop.defineCustomer("Maria") > 0);
    }
}

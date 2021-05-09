package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests for the EZShop.defineCustomer(String) method.
 */
public class EZShopTestDefineCustomer {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.CASHIER);

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        shop.reset();
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("defineCustomer", String.class);
        testAccessRights(defineCustomer, new Object[] {"Pietro"},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * If an empty customerName is provided an InvalidCustomerNameException should be thrown
     */
    @Test(expected = InvalidCustomerNameException.class)
    public void testInvalidCustomerNameExceptionIfNameEmpty() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(user.getUsername(), user.getPassword());
        shop.defineCustomer("");
    }

    /**
     * If null is passed as an argument for customerName an InvalidCustomerNameException should be thrown
     */
    @Test(expected = InvalidCustomerNameException.class)
    public void testInvalidCustomerNameExceptionIfNameNull() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(user.getUsername(), user.getPassword());
        shop.defineCustomer(null);
    }

    /**
     * If a customer name is provided, that is not unique within the system an error value should be returned
     */
    @Test
    public void testOnlyUniqueCustomerNamesAllowed() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(user.getUsername(), user.getPassword());
        assertTrue(shop.defineCustomer("Pietro") > 0);

        // return error value on duplicate customer name
        assertTrue(shop.defineCustomer("Pietro") == -1);
    }

    /**
     * Define a single customer successfully
     */
    @Test
    public void testDefineSingleCustomerSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(user.getUsername(), user.getPassword());
        assertTrue(shop.defineCustomer("Pietro") > 0);
    }

    /**
     * Define a set of customers successfully
     */
    @Test
    public void testDefineManyCustomersSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        shop.login(user.getUsername(), user.getPassword());

        assertTrue(shop.defineCustomer("Pietro") > 0);
        assertTrue(shop.defineCustomer("Andrea") > 0);
        assertTrue(shop.defineCustomer("Sarah") > 0);
        assertTrue(shop.defineCustomer("Maria") > 0);
    }
}

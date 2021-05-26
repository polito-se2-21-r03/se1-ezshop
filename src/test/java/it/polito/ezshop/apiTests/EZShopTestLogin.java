package it.polito.ezshop.apiTests;


import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;


public class EZShopTestLogin {
    private static final EZShop shop = new EZShop();
    private static User user1;
    private static User user2;

    public EZShopTestLogin() throws Exception {
        user1 = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        user2 = new User(1, "Leonardo", "234", Role.CASHIER);
    }
    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {
        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user1.getUsername(), user1.getPassword(), user1.getRole().getValue());
        shop.createUser(user2.getUsername(), user2.getPassword(), user2.getRole().getValue());


    }
    /**
     * Tests that an InvalidUserNameException is thrown if the user name is null or empty
     */
    @Test
    public void testInvalidUsernameException() {

        // verify correct exception is thrown
        testInvalidValues(InvalidUsernameException.class, invalidUserUsernames,
                (name) -> shop.login(name, user1.getPassword()));

    }
    /**
     * Tests that an InvalidPasswordException is thrown if the user password is null or empty
     */
    @Test
    public void testInvalidUserPasswordException() {

        // verify correct exception is thrown
        testInvalidValues(InvalidUserPasswordException.class, invalidUserPassword,
                (password) -> shop.login(user1.getUsername(), password));

    }
    /**
     * Tests for login method finiseh with success
     */
    @Test
    public void testLoginFinishSuccessfully() throws InvalidPasswordException, InvalidUsernameException {

        // login return test
        assertNotNull(shop.login(user2.getUsername(), user2.getPassword()));

    }
    /**
     * Tests for login method finiseh with success
     */
    @Test
    public void testLoginFailed() throws InvalidPasswordException, InvalidUsernameException {

        //wrong password and username
        String fakeUsername = "Giuseppe";
        String fakePassword = "987";

        // login return test for wrong username
        assertNull(shop.login(fakeUsername, user2.getPassword()));

        // login return test for wrong password
        assertNull(shop.login(user2.getUsername(), fakePassword));

    }
}

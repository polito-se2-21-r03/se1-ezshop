package it.polito.ezshop.apiTests;


import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;



public class EZShopTestLogout {
    private static final EZShop shop = new EZShop();
    private static User user1;

    public EZShopTestLogout() throws Exception {
        user1 = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
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

    }
    /**
     * Tests for logout method finishes with success
     */
    @Test
    public void testLogoutFinishSuccessfully() throws InvalidPasswordException, InvalidUsernameException {

        shop.login(user1.getUsername(), user1.getPassword());

        assertTrue(shop.logout());

    }
    /**
     * Tests for logout method finishes with success
     */
    @Test
    public void testLogoutFailed() {

        assertFalse(shop.logout());

    }
}

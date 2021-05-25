package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.User;
import it.polito.ezshop.exceptions.*;
import org.junit.Before;
import org.junit.Test;


import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.createUser() method.
 */
public class EZShopTestCreateUser {


    private final EZShopInterface shop = new EZShop();
    private static final String user1Name = "Pietro";
    private static final String user2Name = "Andrea";
    private static final String password1 = "123";
    private static final String password2 = "456";
    private static final String user1Role = "SHOP_MANAGER";
    private static final String user2Role = "ADMINISTRATOR";

    @Before
    public void beforeEach() throws Exception {
        // reset shop to clean state
        shop.reset();

        // add an user to shop
        Integer user1ID = shop.createUser(user1Name, password1, user1Role);


    }
    /**
     * Tests that an InvalidUserNameException is thrown if the user name is null or empty
     */
    @Test
    public void testInvalidUsernameException() {

        // verify correct exception is thrown
        testInvalidValues(InvalidUsernameException.class, invalidUserUsernames,
                (name) -> shop.createUser(name, password2, user2Role));

    }
    /**
     * Tests that an InvalidPasswordException is thrown if the user password is null or empty
     */
    @Test
    public void testInvalidUserPasswordException() {

        // verify correct exception is thrown
        testInvalidValues(InvalidUserPasswordException.class, invalidUserPassword,
                (password) -> shop.createUser(user2Name, password, user2Role));

    }
    /**
     * Tests that an InvalidRoleException is thrown if the user role is empty, null or not among the set of admissible values
     */
    @Test
    public void testInvalidRoleException() {

        // verify correct exception is thrown
        testInvalidValues(InvalidRoleException.class, invalidUserRoles,
                (userRole) -> shop.createUser(user2Name, password2, userRole));

    }
    /**
     * Tests that return -1 if there is an error while saving the user or if another user with the same username exists
     */
    @Test
    public void testErrorCaseReturn() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {

        Integer expected = -1;
        assertEquals(expected, shop.createUser(user1Name, password1, user1Role));
    }
    /**
     * Tests for that method return the id of the new user ( > 0 ) and not null.
     */
    @Test
    public void testCreateUserSuccesfully() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException {
        Integer id2 = shop.createUser(user2Name,password2,user2Role);

        assertNotNull(id2);
        assertTrue(id2 > 0);

        //verify the created product
        User user2 = shop.getUser(id2);
        assertNotNull(user2);
        assertEquals(id2, user2.getId());
        assertEquals(user2Name, user2.getUsername());
        assertEquals(user2Role, user2.getRole());
        assertEquals(password2, user2.getPassword());
    }


}

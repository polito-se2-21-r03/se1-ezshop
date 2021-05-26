package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static it.polito.ezshop.TestHelpers.*;
import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteUser() method.
 */
public class EZShopTestDeleteUser {


    private final EZShopInterface shop = new EZShop();
    private static User admin;
    private static final String user1Name = "Pietro";
    private static final String user2Name = "Andrea";
    private static final String password1 = "234";
    private static final String password2 = "456";
    private static final String user1Role = "SHOP_MANAGER";
    private static final String user2Role = "CASHIER";
    private static Integer user1ID;
    private static Integer user2ID;

    public EZShopTestDeleteUser() throws Exception {
        admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
    }

    @Before
    public void beforeEach() throws Exception {
        // reset shop to clean state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());


        // add two user more to shop after then total (3)
        user1ID = shop.createUser(user1Name, password1, user1Role);
        user2ID = shop.createUser(user2Name, password2, user2Role);

        // logout after setup
        shop.logout();
    }
    /**
     * Tests that access rights are handled correctly by deleteUser.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method deleteUser = EZShop.class.getMethod("deleteUser", Integer.class);
        testAccessRights(deleteUser, new Object[]{1},
                new Role[]{Role.ADMINISTRATOR});
    }
    /**
     * Tests that an InvalidUserIdException is throw if id is less than or equal to 0 or if it is null.
     *
     */
    @Test
    public void testInvalidUserIdException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());


        // verify correct exception is thrown
        testInvalidValues(InvalidUserIdException.class, invalidUserIDs, shop::deleteUser);

    }
    /**
     * Tests deleting a user successfully
     */
    @Test
    public void testDeleteUserSuccessfully() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException, InvalidUserIdException {
        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // delete user successfully
        assertTrue(shop.deleteUser(user1ID));

        // verify user does not exist anymore
        assertEquals(2, shop.getAllUsers().size());
        assertNull(shop.getUser(user1ID));

        // verify other user still exist
        assertEquals(user2ID, shop.getUser(user2ID).getId());
        assertEquals(user2Name, shop.getUser(user2ID).getUsername());
    }
    /**
     * Tests that false is returned if no user with that ID exists
     */
    @Test
    public void testFalseIfUserDoesNotExist() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidUserIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any user
        int nonExistentId = generateId(
                shop.getAllUsers().stream()
                        .map(it.polito.ezshop.data.User::getId)
                        .collect(Collectors.toList()));

        // return false when user does not exist
        assertFalse(shop.deleteUser(nonExistentId));

        // verify no users have been deleted
        assertEquals(3, shop.getAllUsers().size());
    }
}
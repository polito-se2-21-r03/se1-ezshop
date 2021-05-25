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
 * Tests on the EZShop.createUser() method.
 */
public class EZShopTestGetUser {


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

    public EZShopTestGetUser() throws Exception {
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
        Method createUser = EZShop.class.getMethod("deleteUser", Integer.class);
        testAccessRights(createUser, new Object[]{1},
                new Role[]{Role.ADMINISTRATOR});
    }
    /**
     * Tests that an InvalidUserIdException is throw if id is less than or equal to 0 or if it is null.
     */
    @Test
    public void testInvalidUserIdException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());


        // verify correct exception is thrown
        testInvalidValues(InvalidUserIdException.class, invalidUserIDs, shop::deleteUser);

    }
    /**
     * Tests that null is returned if no user with the given ID exists
     */
    @Test
    public void testNullIfUserNotExists() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any user
        int nonExistentId = generateId(
                shop.getAllUsers().stream()
                        .map(it.polito.ezshop.data.User::getId)
                        .collect(Collectors.toList()));

        // return false when user does not exist
        assertNull(shop.getUser(nonExistentId));
    }
    /**
     * Tests that user is returned correctly
     */
    @Test
    public void testUserReturnedSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get user
        it.polito.ezshop.data.User user = shop.getUser(EZShopTestGetUser.user1ID);

        // verify that user data is correct
        assertEquals(EZShopTestGetUser.user1ID, user.getId());
        assertEquals(EZShopTestGetUser.user1Name, user.getUsername());
        assertEquals(EZShopTestGetUser.user1Role, user.getRole());
        assertEquals(EZShopTestGetUser.password1, user.getPassword());
    }
}

package it.polito.ezshop.apiTests;


import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Method;
import java.util.List;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getAllUsers() method.
 */
public class EZShopTestGetAllUsers {


    private final EZShopInterface shop = new EZShop();
    private static User admin;
    private static final String user1Name = "Pietro";
    private static final String user2Name = "Andrea";
    private static final String password1 = "234";
    private static final String password2 = "456";
    private static final String user1Role = "ShopManager";
    private static final String user2Role = "Cashier";
    private static Integer user1ID;
    private static Integer user2ID;

    public EZShopTestGetAllUsers() throws Exception {
        admin = new User(1, "Andrea", "123", "Administrator");
    }

    @Before
    public void beforeEach() throws Exception {
        // reset shop to clean state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());

        // logout after setup
        shop.logout();
    }
    /**
     * Tests that access rights are handled correctly by deleteUser.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method deleteUser = EZShop.class.getMethod("getAllUsers");
        testAccessRights(deleteUser, new Object[]{1},
                new Role[]{Role.ADMINISTRATOR});
    }
    /**
     * Tests that an empty list(it could not be empty, at least there should be one administrator user) is returned when no user are present in the shop
     */
    @Test
    public void testGetBaseUserList() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // throw if the base list does not include just administrator
        assertEquals(1, shop.getAllUsers().size());
    }
    /**
     * Tests that a list of users is returned successfully
     */
    @Test
    public void testGetUsersSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add two users to the shop
        user1ID = shop.createUser(user1Name, password1, user1Role);
        user2ID = shop.createUser(user2Name, password2, user2Role);

        // verify that correct amount of users have been returned
        List<it.polito.ezshop.data.User> users = shop.getAllUsers();
        assertEquals(2, users.size());

        // get returned users
        it.polito.ezshop.data.User returnedUser1 = users.stream()
                .filter(c -> c.getId().equals(user1ID))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.User returnedUser2 =  users.stream()
                .filter(c -> c.getId().equals(user2ID))
                .findAny()
                .orElse(null);

        assertNotNull(returnedUser1);
        assertEquals(user1ID, returnedUser1.getId());
        assertEquals(user1Name, returnedUser1.getUsername());
        assertEquals(user1Role, returnedUser1.getRole());
        assertEquals(password1, returnedUser1.getPassword());

        assertNotNull(returnedUser2);
        assertEquals(user2ID, returnedUser2.getId());
        assertEquals(user2Name, returnedUser2.getUsername());
        assertEquals(user2Role, returnedUser2.getRole());
        assertEquals(password2, returnedUser2.getPassword());
    }
}

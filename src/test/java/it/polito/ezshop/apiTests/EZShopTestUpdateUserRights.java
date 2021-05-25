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
 * Tests on the EZShop.updateUserRights() method.
 */
public class EZShopTestUpdateUserRights {


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

    public EZShopTestUpdateUserRights() throws Exception {
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
     * Tests that access rights are handled correctly by updateUserRights.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method updateUserRights = EZShop.class.getMethod("updateUserRights", Integer.class, String.class);
        testAccessRights(updateUserRights, new Object[]{1},
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
        testInvalidValues(InvalidUserIdException.class, invalidUserIDs,
                (userID) -> shop.updateUserRights(userID, user2Role));

    }
    /**
     * Tests that an InvalidRoleException is thrown if the user role is empty, null or not among the set of admissible values
     */
    @Test
    public void testInvalidRoleException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidRoleException.class, invalidUserRoles,
                (userRole) -> shop.updateUserRights(user1ID, userRole));

    }
    /**
     * Tests that return false if the user does not exist
     */
    @Test
    public void testFalseIfUserDoesNotExist() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidUserIdException, InvalidRoleException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any user
        int nonExistentId = generateId(
                shop.getAllUsers().stream()
                        .map(it.polito.ezshop.data.User::getId)
                        .collect(Collectors.toList()));

        // return false when user does not exist
        assertFalse(shop.updateUserRights(nonExistentId, user2Role));


    }
    /**
     * Tests that return true
     */
    @Test
    public void testUpdateUserRightsFinishSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidUserIdException, InvalidRoleException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        //new role
        String role = "SHOP_MANAGER";

        //return true if update successfully
        boolean result = shop.updateUserRights(user2ID, role);
        assertTrue(result);

        //check role change successfully
        assertEquals(role, shop.getUser(user2ID).getRole());

    }
}

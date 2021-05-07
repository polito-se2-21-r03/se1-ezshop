package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.startSaleTransaction() method.
 */
public class EZShopTestStartSaleTransaction {

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());
    }

    /**
     * Tests that access rights are handled correctly by updateProduct.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("startSaleTransaction");
        Object[] params = {};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * Create one or more sale transaction(s) successfully
     */
    @Test
    public void testStartSaleTransactionSuccessfully() throws UnauthorizedException {
        // start a new sale transaction
        Integer id1 = shop.startSaleTransaction();
        assertNotNull(id1);
        assertTrue(id1 > 0);

        // start a new sale transaction
        Integer id2 = shop.startSaleTransaction();
        assertNotNull(id2);
        assertTrue(id2 > 0);

        // verify if the two IDs are different
        assertNotEquals(id1, id2);
    }

}

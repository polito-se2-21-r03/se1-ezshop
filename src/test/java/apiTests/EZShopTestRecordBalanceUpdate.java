package apiTests;

import it.polito.ezshop.data.BalanceOperation;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.model.Credit;
import it.polito.ezshop.model.Debit;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import static unitTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestRecordBalanceUpdate {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.ADMINISTRATOR);

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("recordBalanceUpdate", double.class);
        testAccessRights(defineCustomer, new Object[] {0},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR});
    }

    /**
     * Tests that a positive balance update is recorded correctly
     */
    @Test
    public void testRecordPositiveBalanceUpdate() throws Throwable {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // add some positive value as balance update
        double totalBalance = 10;
        assertTrue(shop.recordBalanceUpdate(totalBalance));

        // verify that resulting balance is correct
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify that operation is recorded as CREDIT
        List<BalanceOperation> accountBook = shop.getCreditsAndDebits(null, null);
        assertEquals(1, accountBook.size());
        assertTrue(accountBook.get(0) instanceof Credit);
    }

    /**
     * Tests that a negative balance update that would result in a negative total balance is not performed
     */
    @Test
    public void testNegativeBalanceUpdateDisallowed() throws Throwable {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // try to add negative balance update
        assertFalse(shop.recordBalanceUpdate(-10));

        // balance has not been reduced below 0
        assertEquals(0, shop.computeBalance(), 0.001);

        // verify that no operations have been recorded in the account book
        assertEquals(0, shop.getCreditsAndDebits(null, null).size());
    }

    /**
     * Tests that a negative balance update is recorded correctly iff the resulting total balance is not negative
     */
    @Test
    public void testRecordNegativeBalanceUpdate() throws Throwable {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        double totalBalance = 0;
        double balanceUpdate;

        // add some positive value as balance update
        balanceUpdate = 10;
        assertTrue(shop.recordBalanceUpdate(balanceUpdate));
        totalBalance += balanceUpdate;

        // record a negative balance update
        balanceUpdate = -5;
        assertTrue(shop.recordBalanceUpdate(balanceUpdate));
        totalBalance += balanceUpdate;

        // try and fail to record a negative balance update
        balanceUpdate = -10;
        assertFalse(shop.recordBalanceUpdate(balanceUpdate));

        // record another negative balance update
        balanceUpdate = -5;
        assertTrue(shop.recordBalanceUpdate(balanceUpdate));
        totalBalance += balanceUpdate;

        // verify that resulting balance is correct
        assertEquals(totalBalance, shop.computeBalance(), 0.001);

        // verify that CREDIT and DEBIT operations have been recorded in correct order
        List<BalanceOperation> accountBook = shop.getCreditsAndDebits(null, null);
        accountBook.sort(Comparator.comparing(BalanceOperation::getDate));
        assertEquals(3, accountBook.size());
        assertTrue(accountBook.get(0) instanceof Credit);
        assertTrue(accountBook.get(1) instanceof Debit);
        assertTrue(accountBook.get(2) instanceof Debit);
    }
}

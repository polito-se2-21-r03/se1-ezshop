package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.*;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.startSaleTransaction() method.
 */
public class EZShopTestStartSaleTransaction {

    private final EZShopInterface shop = new EZShop();
    private final User admin;

    public EZShopTestStartSaleTransaction() throws Exception {
        admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
    }

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());
    }

    /**
     * Tests that access rights are handled correctly by startSaleTransaction.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("startSaleTransaction");
        Object[] params = {};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * Start one or more sale transaction(s) successfully
     */
    @Test
    public void testStartSaleTransactionSuccessfully() throws UnauthorizedException {
        int transactionsCount = shop.getCreditsAndDebits(null, null).size();
        double initialBalance = shop.computeBalance();

        // start a new sale transaction
        Integer id1 = shop.startSaleTransaction();
        assertNotNull(id1);
        assertTrue(id1 > 0);

        // verify the new balance operation
        BalanceOperation op = ((EZShop) shop).getAccountBook().getTransaction(id1);
        assertNotNull(op);
        assertEquals(OperationStatus.OPEN, op.getStatus());
        assertEquals(0.0, op.getMoney(), 0.01);
        assertTrue(op instanceof SaleTransaction);
        assertEquals(0, ((SaleTransaction) op).getTransactionItems().size());
        assertEquals(0.0, ((SaleTransaction) op).computeTotal(), 0.01);

        // start a new sale transaction
        Integer id2 = shop.startSaleTransaction();
        assertNotNull(id2);
        assertTrue(id2 > 0);

        // verify if the two IDs are different
        assertNotEquals(id1, id2);

        // verify the list of transactions didn't change
        assertEquals(transactionsCount, shop.getCreditsAndDebits(null, null).size());
        // verify the balance of the shop didn't change
        assertEquals(initialBalance, shop.computeBalance(), 0.01);
    }

}

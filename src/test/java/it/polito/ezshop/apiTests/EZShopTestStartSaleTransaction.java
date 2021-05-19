package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.startSaleTransaction() method.
 */
public class EZShopTestStartSaleTransaction extends EZShopTestBase {

    @Before
    public void beforeEach() throws Exception {
        super.reset();
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

package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getSaleTransaction(Integer) method.
 */
public class EZShopTestGetSaleTransaction {

    private final EZShopInterface shop = new EZShop();

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword(),
                TestHelpers.admin.getRole().getValue());
        // and log in with that user
        shop.login(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword());

        // add product1 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);

        tid = shop.startSaleTransaction();
        // add product1 to the transaction
        shop.addProductToSale(tid, product1.getBarCode(), 1);
    }

    /**
     * Tests that access rights are handled correctly by getSaleTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("getSaleTransaction", Integer.class);
        Object[] params = {tid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> shop.getSaleTransaction(value));
        }
    }

    /**
     * Get a sale transaction successfully
     */
    @Test
    public void testGetSaleTransactionSuccessfully() throws Exception {
        // the method should initially return null (transaction is not closed)
        assertNull(shop.getSaleTransaction(tid));
        // close the sale transaction
        shop.endSaleTransaction(tid);

        // check that the transaction is correctly returned
        SaleTransaction st = shop.getSaleTransaction(tid);
        assertNotNull(st);
        assertEquals(tid, st.getTicketNumber());

        // delete the transaction and check that it is not returned anymore
        shop.deleteSaleTransaction(tid);
        assertNull(shop.getSaleTransaction(tid));
    }

}

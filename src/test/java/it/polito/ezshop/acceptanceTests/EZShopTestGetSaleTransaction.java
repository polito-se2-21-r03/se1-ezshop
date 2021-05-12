package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getSaleTransaction(Integer) method.
 */
public class EZShopTestGetSaleTransaction extends EZShopTestBase {

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1);

        // create a new transaction
        tid = shop.startSaleTransaction();
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
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::getSaleTransaction);
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

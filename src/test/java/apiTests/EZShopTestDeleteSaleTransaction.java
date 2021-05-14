package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteSaleTransaction(Integer) method.
 */
public class EZShopTestDeleteSaleTransaction extends EZShopTestBase {

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add product1 to the shop
        addProducts(product1);

        // create a new transaction and add product1 to it
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), 1);
    }

    /**
     * Tests that access rights are handled correctly by deleteSaleTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteSaleTransaction", Integer.class);
        Object[] params = {tid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::deleteSaleTransaction);
    }

    /**
     * If the transaction does not exists, the method should return false
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.deleteSaleTransaction(tid + 1));
    }

    /**
     * If the transaction has already been paid, the method should return false
     */
    @Test
    public void testPaidTransaction() throws Exception {
        assertTrue(shop.endSaleTransaction(tid));
        shop.receiveCashPayment(tid, 50.0);

        assertFalse(shop.deleteSaleTransaction(tid));
        assertNotNull(shop.getSaleTransaction(tid));
    }

    /**
     * Delete a sale transaction successfully
     */
    @Test
    public void testDeleteSaleTransactionSuccessfully() throws Exception {
        assertTrue(shop.deleteSaleTransaction(tid));
        assertNull(shop.getSaleTransaction(tid));
    }

}

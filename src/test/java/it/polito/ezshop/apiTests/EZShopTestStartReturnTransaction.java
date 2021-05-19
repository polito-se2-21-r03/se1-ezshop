package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.startReturnTransaction(Integer) method.
 */
public class EZShopTestStartReturnTransaction extends EZShopTestBase {

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1, product2);

        // create a new transaction and add product1 to it
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), 1);
    }

    /**
     * Tests that access rights are handled correctly by startReturnTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("startReturnTransaction", Integer.class);
        Object[] params = {tid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::startReturnTransaction);
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertEquals(-1, (int) shop.startReturnTransaction(tid + 1));
    }

    /**
     * If the transaction has not been paid yet, the method should return -1
     */
    @Test()
    public void testNotPaidTransaction() throws Exception {
        assertEquals(-1, (int) shop.startReturnTransaction(tid));
    }

    /**
     * Start a return transaction successfully
     */
    @Test
    public void testStartReturnTransactionSuccessfully() throws Exception {
        // pay the transaction before creating a return transaction
        shop.endSaleTransaction(tid);
        shop.receiveCashPayment(tid, product1.getPricePerUnit());

        assertTrue(shop.startReturnTransaction(tid) > 0);
    }
}

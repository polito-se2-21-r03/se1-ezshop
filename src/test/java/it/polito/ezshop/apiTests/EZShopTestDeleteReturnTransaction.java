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
 * Tests on the EZShop.deleteReturnTransaction(Integer) method.
 */
// TODO: what is the desired behaviour if deleteReturnTransaction is called before endReturnTransaction?
public class EZShopTestDeleteReturnTransaction extends EZShopTestBase {

    public static final Integer PRODUCT_RETURN_AMOUNT_1 = 1;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 3;
    private Integer rid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add product1 to the shop
        addProducts(product1);

        // create a new transaction and add product1 and product2 to it
        Integer tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.endSaleTransaction(tid);

        // pay the transaction
        shop.receiveCashPayment(tid, product1.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_1);

        // create a return transaction
        rid = shop.startReturnTransaction(tid);
        shop.returnProduct(rid, product1.getBarCode(), PRODUCT_RETURN_AMOUNT_1);
        shop.endReturnTransaction(rid, true);
    }

    /**
     * Tests that access rights are handled correctly by deleteReturnTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteReturnTransaction", Integer.class);
        Object[] params = {rid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw {@link InvalidTransactionIdException}
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (value) -> shop.deleteReturnTransaction(rid));
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingReturnTransaction() throws Exception {
        assertFalse(shop.deleteReturnTransaction(rid + 1));
    }

    /**
     * Delete a return transaction successfully
     */
    @Test
    public void testDeleteReturnTransactionSuccessfully() throws Exception {
        assertTrue(shop.deleteReturnTransaction(rid));
        assertFalse(shop.deleteReturnTransaction(rid));
    }

    /**
     * If the transaction has already been paid, the method should return false
     */
    @Test
    public void testDeletePaidReturnTransactionSuccessfully() throws Exception {
        shop.returnCashPayment(rid);
        assertFalse(shop.deleteReturnTransaction(rid));
    }

}

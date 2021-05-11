package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.endReturnTransaction(Integer, boolean) method.
 */
public class EZShopTestEndReturnTransaction extends EZShopTestBase {

    public static final Integer PRODUCT_RETURN_AMOUNT_1 = 1;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 3;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 2;
    private Integer tid, rid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1, product2, product3);

        // create a new transaction and add product1 and product2 to it
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
        shop.endSaleTransaction(tid);

        // pay the transaction
        double total = product1.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_1 +
                product2.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_2;
        shop.receiveCashPayment(tid, total);

        // create a return transaction
        rid = shop.startReturnTransaction(tid);
        shop.returnProduct(rid, product1.getBarCode(), PRODUCT_RETURN_AMOUNT_1);
    }

    /**
     * Tests that access rights are handled correctly by endReturnTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("endReturnTransaction", Integer.class, boolean.class);
        Object[] params = {rid, true};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw {@link InvalidTransactionIdException}
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (value) -> shop.endReturnTransaction(rid, true));
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingReturnTransaction() throws Exception {
        assertFalse(shop.endReturnTransaction(rid + 1, true));
    }

    /**
     * Commit a return transaction
     */
    @Test
    public void testCommit() throws Exception {
        // get the initial total of the sale transaction
        double initialTotal = shop.getSaleTransaction(tid).getPrice();

        // commit the return transaction
        assertTrue(shop.endReturnTransaction(rid, true));

        // verify that the sale transaction was correctly updated
        SaleTransaction st = shop.getSaleTransaction(tid);
        int delta = PRODUCT_TRANSACTION_AMOUNT_1 - st.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findAny().orElse(-1);
        assertEquals((int) PRODUCT_RETURN_AMOUNT_1, delta);
        assertNotEquals(initialTotal, st.getPrice(), 0.01);

        // try to commit/rollback again -> the method should return false
        assertFalse(shop.endReturnTransaction(rid, true));
        assertFalse(shop.endReturnTransaction(rid, false));
    }

    /**
     * Rollback a return transaction
     */
    @Test
    public void testRollback() throws Exception {
        // get the initial total of the sale transaction
        double initialTotal = shop.getSaleTransaction(tid).getPrice();

        // rollback the return transaction
        assertTrue(shop.endReturnTransaction(rid, false));

        // verify that the sale transaction was not updated
        SaleTransaction st = shop.getSaleTransaction(tid);
        int delta = PRODUCT_TRANSACTION_AMOUNT_1 - st.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findAny().orElse(-1);
        assertEquals(0, delta);
        assertEquals(initialTotal, st.getPrice(), 0.01);

        // the transaction should not exist anymore -> the method should return false
        assertFalse(shop.endReturnTransaction(rid, false));
        assertFalse(shop.endReturnTransaction(rid, true));
    }

}

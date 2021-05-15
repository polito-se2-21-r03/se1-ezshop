package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.endReturnTransaction(Integer, boolean) method.
 */
public class EZShopTestEndReturnTransaction extends EZShopTestBase {

    private static final Integer SALE_1_PRODUCT_1_RETURN_AMOUNT = 1;
    private static final Integer SALE_1_PRODUCT_1_AMOUNT = 3;
    private static final Integer SALE_1_PRODUCT_2_AMOUNT = 2;

    private static final Integer SALE_2_PRODUCT_2_RETURN_AMOUNT = 4;
    private static final Integer SALE_2_PRODUCT_2_AMOUNT = 4;
    private static final Integer SALE_2_PRODUCT_3_AMOUNT = 1;
    private static final double SALE_2_PRODUCT_2_DISCOUNT_RATE = .25;
    private static final double SALE_2_DISCOUNT_RATE = .12;

    // sale transaction 1, return transaction 1
    private Integer sid1, rid1;

    // sale transaction 2, return transaction 2
    private Integer sid2, rid2;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1, product2, product3);

        // create sale transaction 1 and add product1 and product2 to it
        sid1 = shop.startSaleTransaction();
        shop.addProductToSale(sid1, product1.getBarCode(), SALE_1_PRODUCT_1_AMOUNT);
        shop.addProductToSale(sid1, product2.getBarCode(), SALE_1_PRODUCT_2_AMOUNT);
        shop.endSaleTransaction(sid1);

        // pay sale transaction 1
        shop.receiveCashPayment(sid1, shop.getSaleTransaction(sid1).getPrice());

        // create a return for sale transaction 1
        rid1 = shop.startReturnTransaction(sid1);
        shop.returnProduct(rid1, product1.getBarCode(), SALE_1_PRODUCT_1_RETURN_AMOUNT);

        // create sale transaction 2 and add product2 and product3 to it
        sid2 = shop.startSaleTransaction();
        shop.addProductToSale(sid2, product2.getBarCode(), SALE_2_PRODUCT_2_AMOUNT);
        shop.addProductToSale(sid2, product3.getBarCode(), SALE_2_PRODUCT_3_AMOUNT);
        shop.applyDiscountRateToProduct(sid2, product2.getBarCode(), SALE_2_PRODUCT_2_DISCOUNT_RATE);
        shop.applyDiscountRateToSale(sid2, SALE_2_DISCOUNT_RATE);
        shop.endSaleTransaction(sid2);

        // pay sale transaction 2
        shop.receiveCashPayment(sid2, shop.getSaleTransaction(sid2).getPrice());

        // create a return for sale transaction 2
        rid2 = shop.startReturnTransaction(sid2);
        shop.returnProduct(rid2, product2.getBarCode(), SALE_2_PRODUCT_2_RETURN_AMOUNT);
    }

    /**
     * Tests that access rights are handled correctly by endReturnTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("endReturnTransaction", Integer.class, boolean.class);
        Object[] params = {rid1, true};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw {@link InvalidTransactionIdException}
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (value) -> shop.endReturnTransaction(rid1, true));
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingReturnTransaction() throws Exception {
        assertFalse(shop.endReturnTransaction(rid1 + 1, true));
    }

    /**
     * Commit a return transaction
     */
    @Test
    public void testCommitSuccessfully1() throws Exception {
        double initialBalance = shop.computeBalance();
        double initialSaleTransactionValue = shop.getSaleTransaction(sid1).getPrice();
        double expectedReturnValue = computeValue(product1.getPricePerUnit(), SALE_1_PRODUCT_1_RETURN_AMOUNT, 0, 0);

        // commit the return transaction
        assertTrue(shop.endReturnTransaction(rid1, true));

        // verify that the sale transaction was correctly updated
        SaleTransaction st = shop.getSaleTransaction(sid1);
        int delta = SALE_1_PRODUCT_1_AMOUNT - st.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findAny().orElse(0);
        // verify that the amount removed from the transaction is correct
        assertEquals((int) SALE_1_PRODUCT_1_RETURN_AMOUNT, delta);
        // verify that the total of the sale transaction was updated correctly
        double expectedNewTotal = initialSaleTransactionValue - expectedReturnValue;
        assertEquals(expectedNewTotal, st.getPrice(), DOUBLE_COMPARISON_THRESHOLD);

        // verify that the balance was NOT updated
        assertEquals(initialBalance, shop.computeBalance(), DOUBLE_COMPARISON_THRESHOLD);

        // try to commit/rollback again -> the method should return false
        assertFalse(shop.endReturnTransaction(rid1, true));
        assertFalse(shop.endReturnTransaction(rid1, false));
    }

    /**
     * Commit a return transaction
     */
    @Test
    public void testCommitSuccessfully2() throws Exception {
        double initialBalance = shop.computeBalance();
        double initialSaleTransactionValue = shop.getSaleTransaction(sid2).getPrice();
        double expectedReturnValue = computeValue(product2.getPricePerUnit(), SALE_2_PRODUCT_2_RETURN_AMOUNT,
                SALE_2_PRODUCT_2_DISCOUNT_RATE,SALE_2_DISCOUNT_RATE);

        // commit the return transaction
        assertTrue(shop.endReturnTransaction(rid2, true));

        // verify that the sale transaction was correctly updated
        SaleTransaction st = shop.getSaleTransaction(sid2);
        int delta = SALE_2_PRODUCT_2_AMOUNT - st.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product2.getBarCode()))
                .map(TicketEntry::getAmount).findAny().orElse(0);
        // verify that the amount removed from the transaction is correct
        assertEquals((int) SALE_2_PRODUCT_2_RETURN_AMOUNT, delta);
        // verify that the total of the sale transaction was updated correctly
        double expectedNewTotal = initialSaleTransactionValue - expectedReturnValue;
        assertEquals(expectedNewTotal, st.getPrice(), DOUBLE_COMPARISON_THRESHOLD);

        // verify that the balance was NOT updated
        assertEquals(initialBalance, shop.computeBalance(), DOUBLE_COMPARISON_THRESHOLD);

        // try to commit/rollback again -> the method should return false
        assertFalse(shop.endReturnTransaction(rid2, true));
        assertFalse(shop.endReturnTransaction(rid2, false));
    }

    /**
     * Rollback a return transaction
     */
    @Test
    public void testRollback() throws Exception {
        // get the initial total of the sale transaction
        double initialTotal = shop.getSaleTransaction(sid1).getPrice();

        // rollback the return transaction
        assertTrue(shop.endReturnTransaction(rid1, false));

        // verify that the sale transaction was not updated
        SaleTransaction st = shop.getSaleTransaction(sid1);
        int delta = SALE_1_PRODUCT_1_AMOUNT - st.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findAny().orElse(-1);
        assertEquals(0, delta);
        assertEquals(initialTotal, st.getPrice(), 0.01);

        // the transaction should not exist anymore -> the method should return false
        assertFalse(shop.endReturnTransaction(rid1, false));
        assertFalse(shop.endReturnTransaction(rid1, true));
    }

}

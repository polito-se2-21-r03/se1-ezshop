package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.BalanceOperation;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.endReturnTransaction(Integer, boolean) method.
 */
public class EZShopTestEndReturnTransaction {

    private static final Integer SALE_1_PRODUCT_1_AMOUNT = 3;
    private static final Integer SALE_1_PRODUCT_2_AMOUNT = 2;
    private static final Integer SALE_1_PRODUCT_1_RETURN_AMOUNT = 1;
    private static final Integer SALE_1_PRODUCT_1_RETURN_AMOUNT_2 = SALE_1_PRODUCT_1_AMOUNT;

    private static final Integer SALE_2_PRODUCT_2_RETURN_AMOUNT = 4;
    private static final Integer SALE_2_PRODUCT_2_AMOUNT = 4;
    private static final Integer SALE_2_PRODUCT_3_AMOUNT = 1;
    private static final double SALE_2_PRODUCT_2_DISCOUNT_RATE = .25;
    private static final double SALE_2_DISCOUNT_RATE = .12;

    // interface of EZShop
    private final EZShopInterface shop = new EZShop();

    // sale transaction 1, return transaction 1
    private Integer sid1, rid1;

    // sale transaction 2, return transaction 2
    private Integer sid2, rid2;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword(),
                TestHelpers.admin.getRole().getValue());
        // and log in with that user
        shop.login(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword());

        // add product1, product2 and product3 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);
        TestHelpers.addProductToShop(shop, TestHelpers.product2);
        TestHelpers.addProductToShop(shop, TestHelpers.product3);

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
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> shop.endReturnTransaction(value, true));
        }
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingReturnTransaction() throws Exception {
        assertFalse(shop.endReturnTransaction(sid1, true));
        assertFalse(shop.endReturnTransaction(rid1 + 1, true));
    }

    /**
     * Commit a return transaction
     */
    @Test
    public void testEndReturnTransactionSuccessfully1() throws Exception {
        // store state before return transaction
        double initialBalance = shop.computeBalance();
        double initialSaleTransactionValue = Math.abs(getSaleTransaction(sid1).getMoney());
        double expectedReturnValue = product1.getPricePerUnit() * SALE_1_PRODUCT_1_RETURN_AMOUNT;

        // commit the return transaction
        assertTrue(shop.endReturnTransaction(rid1, true));

        // get the sale transaction for which the return was made
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) getSaleTransaction(sid1);

        // compute the number of items that were returned
        int delta = SALE_1_PRODUCT_1_AMOUNT - saleTransaction.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getAmount).findAny().orElse(0);

        // verify that the amount of items returned is correct
        assertEquals((int) SALE_1_PRODUCT_1_RETURN_AMOUNT, delta);

        // verify that the total of the sale transaction was updated correctly
        double expectedNewTotal = initialSaleTransactionValue - expectedReturnValue;
        assertEquals(expectedNewTotal, saleTransaction.computeTotal(), 0.01);

        // verify that the balance was NOT updated
        assertEquals(initialBalance, shop.computeBalance(), 0.01);

        // try to commit/rollback again -> the method should return false
        assertFalse(shop.endReturnTransaction(rid1, true));
        assertFalse(shop.endReturnTransaction(rid1, false));
    }

    /**
     * Commit a return transaction
     */
    @Test
    public void testEndReturnTransactionSuccessfully2() throws Exception {
        // store state before return transaction
        double initialBalance = shop.computeBalance();
        double initialSaleTransactionValue = Math.abs(getSaleTransaction(sid2).getMoney());
        double expectedReturnValue = product2.getPricePerUnit() * SALE_2_PRODUCT_2_RETURN_AMOUNT *
                (1.0 - SALE_2_PRODUCT_2_DISCOUNT_RATE) * (1.0 - SALE_2_DISCOUNT_RATE);

        // commit the return transaction
        assertTrue(shop.endReturnTransaction(rid2, true));

        // get the sale transaction for which the return was made
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) getSaleTransaction(sid2);

        // compute the number of items that were returned
        int delta = SALE_2_PRODUCT_2_AMOUNT - saleTransaction.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product2.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getAmount).findAny().orElse(0);

        // verify that the amount removed from the transaction is correct
        assertEquals((int) SALE_2_PRODUCT_2_RETURN_AMOUNT, delta);

        // verify that the total of the sale transaction was updated correctly
        double expectedNewTotal = initialSaleTransactionValue - expectedReturnValue;
        assertEquals(expectedNewTotal, saleTransaction.computeTotal(), DOUBLE_COMPARISON_THRESHOLD);

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
        double initialTotal = Math.abs(getSaleTransaction(sid1).getMoney());

        // rollback the return transaction
        assertTrue(shop.endReturnTransaction(rid1, false));

        // get the sale transaction for which the return was rolled back
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) getSaleTransaction(sid1);

        // verify that the sale transaction was not updated
        int delta = SALE_1_PRODUCT_1_AMOUNT - saleTransaction.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getAmount).findAny().orElse(-1);
        assertEquals(0, delta);
        assertEquals(initialTotal, saleTransaction.computeTotal(), 0.01);

        // the transaction should not exist anymore -> the method should return false
        assertFalse(shop.endReturnTransaction(rid1, false));
        assertFalse(shop.endReturnTransaction(rid1, true));
    }

    /**
     * Open multiple return transactions for sale 1. Each return transaction on its own
     * is valid (in terms of returned amount). Committing all the return transactions should result in
     * an error.
     *
     * return transaction 1: return 1 unit of product 1 --> OK
     * return transaction 2: return 3 units of product 1 --> OK
     * return transactions 1 + 2: return 4 units of product 1 --> NOK
     */
    @Test
    public void testMultipleReturnTransaction () throws Exception {
        double initialSaleTransactionValue = Math.abs(getSaleTransaction(sid1).getMoney());
        double expectedReturnValue = product1.getPricePerUnit() * SALE_1_PRODUCT_1_RETURN_AMOUNT;

        // create another return transaction for sale 1
        int rid2 = shop.startReturnTransaction(sid1);
        shop.returnProduct(rid2, product1.getBarCode(), SALE_1_PRODUCT_1_RETURN_AMOUNT_2);

        // try to commit both rid1 and rid2
        assertTrue(shop.endReturnTransaction(rid1, true));
        assertFalse(shop.endReturnTransaction(rid2, true));

        // get the sale transaction for which the return was made
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) getSaleTransaction(sid1);

        // compute the number of items that were returned
        int delta = SALE_1_PRODUCT_1_AMOUNT - saleTransaction.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getAmount).findAny().orElse(0);

        // verify that the amount of items returned is correct
        assertEquals((int) SALE_1_PRODUCT_1_RETURN_AMOUNT, delta);

        // verify that the total of the sale transaction was updated correctly
        double expectedNewTotal = initialSaleTransactionValue - expectedReturnValue;
        assertEquals(expectedNewTotal, saleTransaction.computeTotal(), 0.01);
    }

    private BalanceOperation getSaleTransaction(int sid) throws Exception {
        return shop.getCreditsAndDebits(null, null).stream()
                .filter(b -> b.getBalanceId() == sid)
                .map(b -> ((BalanceOperationAdapter) b).getTransaction())
                .findAny()
                .orElse(null);
    }
}

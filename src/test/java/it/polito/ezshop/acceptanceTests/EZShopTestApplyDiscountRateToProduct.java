package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests on the EZShop.applyDiscountRateToProduct(Integer, String, double) method.
 */
public class EZShopTestApplyDiscountRateToProduct extends EZShopTestBase {

    private static final double EXPECTED_DISCOUNT_RATE = 0.25;

    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 5;

    private Integer transactionId;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add product1, product2 and product3 to the shop
        addProducts(product1, product2, product3);

        // create a new transaction
        transactionId = shop.startSaleTransaction();

        // add products product1 and product2 to the transaction
        shop.addProductToSale(transactionId, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(transactionId, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
    }

    /**
     * Tests that access rights are handled correctly by applyDiscountRateToProduct.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("applyDiscountRateToProduct", Integer.class,
                String.class, double.class);
        Object[] params = {transactionId, product1.getBarCode(), EXPECTED_DISCOUNT_RATE};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId()  {
        testInvalidValues(InvalidTransactionIdException.class, invalidProductIDs,
                (value) -> shop.applyDiscountRateToProduct(value, product1.getBarCode(), EXPECTED_DISCOUNT_RATE));
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        testInvalidValues(InvalidProductCodeException.class, invalidProductCodes,
                (value) -> shop.applyDiscountRateToProduct(transactionId, value, EXPECTED_DISCOUNT_RATE));
    }

    /**
     * If the discount rate is not in [0, 1), the method should throw InvalidDiscountRateException
     */
    @Test()
    public void testInvalidDiscountRate() {
        testInvalidValues(InvalidDiscountRateException.class, invalidDiscountRates,
                (value) -> shop.applyDiscountRateToProduct(transactionId, product1.getBarCode(), value));
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.applyDiscountRateToProduct(transactionId, product3.getBarCode(), EXPECTED_DISCOUNT_RATE));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws Exception {
        shop.endSaleTransaction(transactionId);
        assertFalse(shop.applyDiscountRateToProduct(transactionId, product1.getBarCode(), EXPECTED_DISCOUNT_RATE));
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws Exception {
        shop.applyDiscountRateToProduct(transactionId, product1.getBarCode(), EXPECTED_DISCOUNT_RATE);

        // verify the final status of the transaction
        shop.endSaleTransaction(transactionId);
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);

        // check the applied discount rate
        double appliedDiscountRate = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getDiscountRate)
                .orElse(-1.0);
        assertEquals(EXPECTED_DISCOUNT_RATE, appliedDiscountRate, DOUBLE_COMPARISON_THRESHOLD);
    }

}

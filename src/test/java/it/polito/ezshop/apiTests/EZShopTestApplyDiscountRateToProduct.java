package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.applyDiscountRateToProduct(Integer, String, double) method.
 */
public class EZShopTestApplyDiscountRateToProduct {

    private static final double EXPECTED_DISCOUNT_RATE = 0.25;

    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 5;

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

        // add product1, product2 and product3 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);
        TestHelpers.addProductToShop(shop, TestHelpers.product2);
        TestHelpers.addProductToShop(shop, TestHelpers.product3);

        tid = shop.startSaleTransaction();
        // add products product1 and product2 to the transaction
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
    }

    /**
     * Tests that access rights are handled correctly by applyDiscountRateToProduct.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("applyDiscountRateToProduct", Integer.class,
                String.class, double.class);
        Object[] params = {tid, product1.getBarCode(), EXPECTED_DISCOUNT_RATE};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> {
                // apply discount to product1
                shop.applyDiscountRateToProduct(value, product1.getBarCode(), EXPECTED_DISCOUNT_RATE);
            });
        }
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        for (String value : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> {
                // apply discount to product1
                shop.applyDiscountRateToProduct(tid, value, EXPECTED_DISCOUNT_RATE);
            });
        }
    }

    /**
     * If the discount rate is not in [0, 1), the method should throw InvalidDiscountRateException
     */
    @Test()
    public void testInvalidDiscountRate() {
        // test values for the product discount rate parameter
        for (Double value : invalidDiscountRates) {
            assertThrows(InvalidDiscountRateException.class, () -> {
                // apply discount to product1
                shop.applyDiscountRateToProduct(tid, product1.getBarCode(), value);
            });
        }

        // verify the discount rate of product in the transaction did not change
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) ((EZShop) shop).getAccountBook().getTransaction(tid);
        Double discountRate = sale.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getDiscountRate).findFirst().orElse(-1.0);
        assertEquals(0.0, discountRate, 0.01);
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.applyDiscountRateToProduct(tid, product3.getBarCode(), EXPECTED_DISCOUNT_RATE));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws Exception {
        shop.endSaleTransaction(tid);
        assertFalse(shop.applyDiscountRateToProduct(tid, product1.getBarCode(), EXPECTED_DISCOUNT_RATE));

        // verify the discount rate of product in the transaction did not change
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) ((EZShop) shop).getAccountBook().getTransaction(tid);
        double discountRate = sale.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getDiscountRate).findFirst().orElse(-1.0);
        assertEquals(0.0, discountRate, 0.01);

        shop.receiveCashPayment(tid, 1000.0);
        assertFalse(shop.applyDiscountRateToProduct(tid, product1.getBarCode(), EXPECTED_DISCOUNT_RATE));

        // verify the discount rate of product in the transaction did not change
        sale = (it.polito.ezshop.model.SaleTransaction) ((EZShop) shop).getAccountBook().getTransaction(tid);
        discountRate = sale.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getDiscountRate).findFirst().orElse(-1.0);
        assertEquals(0.0, discountRate, 0.01);
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws Exception {
        assertTrue(shop.applyDiscountRateToProduct(tid, product1.getBarCode(), EXPECTED_DISCOUNT_RATE));

        // verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // check the applied discount rate
        double appliedDiscountRate = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getDiscountRate)
                .orElse(-1.0);
        assertEquals(EXPECTED_DISCOUNT_RATE, appliedDiscountRate, DOUBLE_COMPARISON_THRESHOLD);
    }
}

package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.applyDiscountRateToSale(Integer, double) method.
 */
public class EZShopTestApplyDiscountRateToSale extends EZShopTestBase {

    private static final double EXPECTED_DISCOUNT_RATE = 0.25;

    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add product1 to the shop
        addProducts(product1);

        // create a new transaction and add product1 to the it
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
    }

    /**
     * Tests that access rights are handled correctly by applyDiscountRateToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("applyDiscountRateToSale", Integer.class, double.class);
        Object[] params = {tid, EXPECTED_DISCOUNT_RATE};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (value) -> shop.applyDiscountRateToSale(value, EXPECTED_DISCOUNT_RATE));
    }

    /**
     * If the discount rate is not in [0, 1), the method should throw InvalidDiscountRateException
     */
    @Test()
    public void testInvalidDiscountRate() {
        testInvalidValues(InvalidDiscountRateException.class, invalidDiscountRates,
                (value) -> shop.applyDiscountRateToSale(tid, value));
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws Exception {
        assertTrue(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE));

        //  close the transaction
        shop.endSaleTransaction(tid);

        // check the applied discount
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);
        assertEquals(EXPECTED_DISCOUNT_RATE, saleTransaction.getDiscountRate(), 0.001);

        // apply again the discount rate when the transaction is closed
        assertTrue(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE));

        // pay the transaction and verify that the discount can not be applied anymore
        shop.receiveCashPayment(tid, 1000.0);
        assertFalse(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE));
    }

}

package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.applyDiscountRateToSale(Integer, double) method.
 */
public class EZShopTestApplyDiscountRateToSale {

    private static final double EXPECTED_DISCOUNT_RATE = 0.25;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;

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

        // add product1 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);

        tid = shop.startSaleTransaction();
        // add products product1 and product2 to the transaction
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
        // test invalid values for the product id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> {
                // apply discount
                shop.applyDiscountRateToSale(value, EXPECTED_DISCOUNT_RATE);
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
                // apply discount
                shop.applyDiscountRateToSale(tid, value);
            });
        }

        // verify the discount rate of the sale transaction did not change
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) ((EZShop) shop).getAccountBook().getTransaction(tid);
        assertEquals(0.0, sale.getDiscountRate(), 0.01);
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws Exception {
        // compute the initial balance of the shop
        double initialBalance = shop.computeBalance();

        // compute the initial total of the sale
        double initialTotal = ((it.polito.ezshop.model.SaleTransaction) ((EZShop) shop).getAccountBook()
                .getTransaction(tid)).computeTotal();
        double expectedFinalTotal = initialTotal * (1.0 - EXPECTED_DISCOUNT_RATE);

        assertTrue(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE));

        //  close the transaction
        shop.endSaleTransaction(tid);

        // check the applied discount
        SaleTransaction sale = shop.getSaleTransaction(tid);
        assertEquals(EXPECTED_DISCOUNT_RATE, sale.getDiscountRate(), 0.01);
        // check the total of the transaction
        assertEquals(expectedFinalTotal, sale.getPrice(), 0.01);

        // apply again the discount rate when the transaction is closed
        assertTrue(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE));

        // pay the transaction and verify that the discount can not be applied anymore
        double change = shop.receiveCashPayment(tid, 1000.0);
        assertEquals(expectedFinalTotal, 1000.0 - change, 0.01);
        assertFalse(shop.applyDiscountRateToSale(tid, EXPECTED_DISCOUNT_RATE + 0.01));
        assertEquals(initialBalance + expectedFinalTotal, shop.computeBalance(), 0.01);

        // verify discount rate and total price of the transaction did not change
        sale = shop.getSaleTransaction(tid);
        assertEquals(EXPECTED_DISCOUNT_RATE, sale.getDiscountRate(), 0.01);
        assertEquals(expectedFinalTotal, sale.getPrice(), 0.01);
        assertEquals(initialBalance + expectedFinalTotal, shop.computeBalance(), 0.01);
    }

}

package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * Tests on the EZShop.computePointsForSale(Integer) method.
 */
public class EZShopTestComputePointsForSale {

    private static final Double EXPECTED_SALE_DISCOUNT_RATE = 0.5;
    private static final Double EXPECTED_PRODUCT_DISCOUNT_RATE = 0.25;
    private static final Integer EUROS_PER_POINT = 10;

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
    }

    /**
     * Tests that access rights are handled correctly by computePointsForSale
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("computePointsForSale", Integer.class);
        Object[] params = {tid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> shop.computePointsForSale(value));
        }
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingTransaction() throws InvalidTransactionIdException, UnauthorizedException {
        assertEquals(-1, shop.computePointsForSale(tid + 1));
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testComputePointsForSaleSuccessfully() throws Exception {
        int amount = 3;
        double total;

        // initially there are no items in the sale transaction -> 0 points
        assertEquals(0, shop.computePointsForSale(tid));

        // add a product and compute points again
        shop.addProductToSale(tid, product1.getBarCode(), amount);
        total = product1.getPricePerUnit() * amount;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));

        // try to apply a discount and recompute points
        shop.applyDiscountRateToProduct(tid, product1.getBarCode(), EXPECTED_PRODUCT_DISCOUNT_RATE);
        total = (1 - EXPECTED_PRODUCT_DISCOUNT_RATE) * product1.getPricePerUnit() * amount;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));

        // check if points are correctly computed after closing the transaction
        shop.endSaleTransaction(tid);

        // apply a discount to the sale and recompute points
        shop.applyDiscountRateToSale(tid, EXPECTED_SALE_DISCOUNT_RATE);
        total = (1 - EXPECTED_SALE_DISCOUNT_RATE) * total;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));
    }

}

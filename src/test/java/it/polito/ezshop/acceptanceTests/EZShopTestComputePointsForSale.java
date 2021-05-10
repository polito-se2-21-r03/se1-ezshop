package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests on the EZShop.computePointsForSale() method.
 */
public class EZShopTestComputePointsForSale {

    // product
    private static final String PRODUCT_CODE = "12345678901231";
    private static final Double PRODUCT_PRICE = 15.0;
    private static final Integer PRODUCT_QUANTITY = 10;
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final String PRODUCT_NOTE = "note";
    private static final String PRODUCT_POSITION = "1-1-1";

    private static final Double EXPECTED_SALE_DISCOUNT_RATE = 0.5;
    private static final Double EXPECTED_PRODUCT_DISCOUNT_RATE = 0.25;
    private static final Integer EUROS_PER_POINT = 10;

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    /**
     * Id of the transaction whose points should be computed.
     */
    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // add product with code PRODUCT_CODE_1 to the shop
        int id1 = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
        shop.updatePosition(id1, PRODUCT_POSITION);
        shop.updateQuantity(id1, PRODUCT_QUANTITY);

        // create a new transaction
        tid = shop.startSaleTransaction();
    }

    /**
     * Tests that access rights are handled correctly by computePointsForSale
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("computePointsForSale", Integer.class);
        Object[] params = {tid};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::computePointsForSale);
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
    public void testComputePointsForSaleSuccessfully() throws InvalidTransactionIdException, UnauthorizedException,
            InvalidProductCodeException, InvalidQuantityException, InvalidDiscountRateException {
        int amount = 3;
        double total;

        // initially there are no items in the sale transaction -> 0 points
        assertEquals(0, shop.computePointsForSale(tid));

        // add a product and compute points again
        shop.addProductToSale(tid, PRODUCT_CODE, amount);
        total = PRODUCT_PRICE * amount;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));

        // try to apply a discount and recompute points
        shop.applyDiscountRateToProduct(tid, PRODUCT_CODE, EXPECTED_PRODUCT_DISCOUNT_RATE);
        total = (1 - EXPECTED_PRODUCT_DISCOUNT_RATE) * PRODUCT_PRICE * amount;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));

        // check if points are correctly computed after closing the transaction
        shop.endSaleTransaction(tid);

        // apply a discount to the sale and recompute points
        shop.applyDiscountRateToSale(tid, EXPECTED_SALE_DISCOUNT_RATE);
        total = (1 - EXPECTED_SALE_DISCOUNT_RATE) * total;
        assertEquals(((int) total) / EUROS_PER_POINT, shop.computePointsForSale(tid));
    }

}

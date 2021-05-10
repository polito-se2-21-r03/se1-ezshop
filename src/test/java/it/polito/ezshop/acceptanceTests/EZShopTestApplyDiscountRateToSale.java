package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests on the EZShop.applyDiscountRateToSale() method.
 */
public class EZShopTestApplyDiscountRateToSale {

    private static final double expectedDiscountRate = 0.25;

    // in the following tests PRODUCT_CODE_1 and PRODUCT_CODE_2
    // are added to a transaction, while PRODUCT_CODE_3 is not

    // product 1
    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final Integer PRODUCT_INVENTORY_QUANTITY_1 = 10;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    private Integer transactionId;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // add product with code PRODUCT_CODE_1 to the shop
        int id1 = shop.createProductType("desc", PRODUCT_CODE_1, 10.0, "note");
        shop.updatePosition(id1, "1-1-1");
        shop.updateQuantity(id1, PRODUCT_INVENTORY_QUANTITY_1);

        // create a new transaction
        transactionId = shop.startSaleTransaction();

        // add products PRODUCT_CODE_1 and PRODUCT_CODE_2 to the transaction
        shop.addProductToSale(transactionId, PRODUCT_CODE_1, PRODUCT_TRANSACTION_AMOUNT_1);
    }

    /**
     * Tests that access rights are handled correctly by addProductToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("applyDiscountRateToSale", Integer.class, double.class);
        Object[] params = {transactionId, expectedDiscountRate};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidProductIdException.class, invalidTransactionIDs,
                (value) -> shop.applyDiscountRateToSale(value, expectedDiscountRate));
    }

    /**
     * If the discount rate is not in [0, 1), the method should throw InvalidDiscountRateException
     */
    @Test()
    public void testInvalidDiscountRate() {
        testInvalidValues(InvalidDiscountRateException.class, invalidDiscountRates,
                (value) -> shop.applyDiscountRateToSale(transactionId, value));
    }

    /**
     * If the transaction is paid or completed, the method should return false
     */
    @Test()
    public void testPaidOrCompletedTransaction() {
        // TODO
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws InvalidTransactionIdException,
            UnauthorizedException, InvalidDiscountRateException {
        assertTrue(shop.applyDiscountRateToSale(transactionId, expectedDiscountRate));

        //  close the transaction
        shop.endSaleTransaction(transactionId);

        // check the applied discount
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);
        assertEquals(expectedDiscountRate, saleTransaction.getDiscountRate(), 0.001);

        // try to apply again the discount rate when the transaction is closed
        assertTrue(shop.applyDiscountRateToSale(transactionId, expectedDiscountRate));
    }

}

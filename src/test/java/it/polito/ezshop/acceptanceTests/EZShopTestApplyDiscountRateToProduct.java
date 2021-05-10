package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests on the EZShop.applyDiscountRateToProduct() method.
 */
public class EZShopTestApplyDiscountRateToProduct {

    private static final double expectedDiscountRate = 0.25;

    // in the following tests PRODUCT_CODE_1 and PRODUCT_CODE_2
    // are added to a transaction, while PRODUCT_CODE_3 is not

    // product 1
    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final Integer PRODUCT_INVENTORY_QUANTITY_1 = 10;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 5;

    // product 2
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final Integer PRODUCT_INVENTORY_QUANTITY_2 = 20;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 5;

    // product 3
    private static final String PRODUCT_CODE_3 = "123456789012";
    private static final Integer PRODUCT_INVENTORY_QUANTITY_3 = 20;

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

        // add product with code PRODUCT_CODE_2 to the shop
        int id2 = shop.createProductType("desc", PRODUCT_CODE_2, 20.0, "note");
        shop.updatePosition(id2, "1-1-2");
        shop.updateQuantity(id2, PRODUCT_INVENTORY_QUANTITY_2);

        // add product with code PRODUCT_CODE_3 to the shop
        int id3 = shop.createProductType("desc", PRODUCT_CODE_3, 20.0, "note");
        shop.updatePosition(id3, "1-1-3");
        shop.updateQuantity(id3, PRODUCT_INVENTORY_QUANTITY_3);

        // create a new transaction
        transactionId = shop.startSaleTransaction();

        // add products PRODUCT_CODE_1 and PRODUCT_CODE_2 to the transaction
        shop.addProductToSale(transactionId, PRODUCT_CODE_1, PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(transactionId, PRODUCT_CODE_2, PRODUCT_TRANSACTION_AMOUNT_2);
    }

    /**
     * Tests that access rights are handled correctly by addProductToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("applyDiscountRateToProduct", Integer.class, String.class, double.class);
        Object[] params = {transactionId, PRODUCT_CODE_2, expectedDiscountRate};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId()  {
        testInvalidValues(InvalidProductIdException.class, invalidProductIDs,
                (value) -> shop.applyDiscountRateToProduct(value, PRODUCT_CODE_1, expectedDiscountRate));
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        testInvalidValues(InvalidProductCodeException.class, invalidProductCodes,
                (value) -> shop.applyDiscountRateToProduct(transactionId, value, expectedDiscountRate));
    }

    /**
     * If the discount rate is not in [0, 1), the method should throw InvalidDiscountRateException
     */
    @Test()
    public void testInvalidDiscountRate() {
        testInvalidValues(InvalidDiscountRateException.class, invalidDiscountRates,
                (value) -> shop.applyDiscountRateToProduct(transactionId, PRODUCT_CODE_1, value));
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.deleteProductFromSale(transactionId, PRODUCT_CODE_3, 1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws InvalidTransactionIdException, UnauthorizedException,
            InvalidProductCodeException, InvalidQuantityException {
        shop.endSaleTransaction(transactionId);
        assertFalse(shop.deleteProductFromSale(transactionId, PRODUCT_CODE_1, PRODUCT_INVENTORY_QUANTITY_1));
    }

    /**
     * Apply a discount rate successfully
     */
    @Test
    public void testApplyDiscountRateSuccessfully() throws InvalidTransactionIdException,
            UnauthorizedException, InvalidProductCodeException, InvalidDiscountRateException {
        shop.applyDiscountRateToProduct(transactionId, PRODUCT_CODE_1, expectedDiscountRate);

        // verify the final status of the transaction
        shop.endSaleTransaction(transactionId);
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);

        // check the applied discount rate
        double appliedDiscountRate = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(PRODUCT_CODE_1))
                .findAny()
                .map(TicketEntry::getDiscountRate)
                .orElse(-1.0);
        assertEquals(expectedDiscountRate, appliedDiscountRate, DOUBLE_COMPARISON_THRESHOLD);
    }

}

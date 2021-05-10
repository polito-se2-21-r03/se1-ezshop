package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.acceptanceTests.TestHelpers.assertThrows;
import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductFromSale() method.
 */
public class EZShopTestDeleteProductFromSale {

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
        Method targetMethod = EZShop.class.getMethod("deleteProductFromSale", Integer.class, String.class, int.class);
        Object[] params = {transactionId, PRODUCT_CODE_2, PRODUCT_TRANSACTION_AMOUNT_2};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // boundary values for the id parameter
        Arrays.asList(null, -1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidTransactionIdException.class, () -> {
                // try to update a product with the boundary value
                shop.deleteProductFromSale(value, PRODUCT_CODE_1, 1);
            });
        });
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        // "12345678901232" is an invalid product code (wrong check digit)
        Arrays.asList(null, "", "123456789B123A", "12345678901232").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.deleteProductFromSale(transactionId, value, 1);
            });
        });
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     */
    @Test()
    public void testInvalidAmount() {
        // test values for the amount parameter
        Arrays.asList(-10, -1).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidQuantityException.class, () -> {
                // try to update a product with the boundary value
                shop.deleteProductFromSale(transactionId, PRODUCT_CODE_1, value);
            });
        });
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
     * If the quantity of product in the transaction cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        // try to remove the current amount of product + 1
        assertFalse(shop.deleteProductFromSale(transactionId, PRODUCT_CODE_1, 1 + PRODUCT_TRANSACTION_AMOUNT_1));
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
     * Delete products from a sale transaction successfully
     */
    @Test
    public void testDeleteProductFromSaleSuccessfully() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        int initialInventoryLevelForProduct1 = PRODUCT_INVENTORY_QUANTITY_1 - PRODUCT_TRANSACTION_AMOUNT_1;

        // 1. partially remove product 1 from the transaction
        // compute the amount of product 1 to be removed from the transaction
        int amountProduct1ToBeRemoved = PRODUCT_TRANSACTION_AMOUNT_1 / 2;
        assertTrue(shop.addProductToSale(transactionId, PRODUCT_CODE_1, amountProduct1ToBeRemoved));

        // 1.1 verify that the quantity of PRODUCT_CODE_1 in the inventory is correctly updated
        int inventoryLevelForProduct1After = shop.getProductTypeByBarCode(PRODUCT_CODE_1).getQuantity();
        assertEquals(initialInventoryLevelForProduct1 + amountProduct1ToBeRemoved, inventoryLevelForProduct1After);

        // 2. completely remove product 2 from the transaction
        assertTrue(shop.deleteProductFromSale(transactionId, PRODUCT_CODE_2, PRODUCT_TRANSACTION_AMOUNT_2));

        // 2.1 verify that the quantity of PRODUCT_CODE_2 in the inventory is correctly updated
        assertEquals(PRODUCT_INVENTORY_QUANTITY_2, shop.getProductTypeByBarCode(PRODUCT_CODE_2).getQuantity());

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(transactionId);
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);

        // 3.1 check amount of product 1 in the transaction
        int amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(PRODUCT_CODE_1))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(PRODUCT_TRANSACTION_AMOUNT_1 - amountProduct1ToBeRemoved, amountP1);

        // 3.2 check amount of product 2 in the transaction
        // TODO: if a product is completely removed should it appear in the entries list with qty = 0?
        int amountP2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(PRODUCT_CODE_2))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(0);
        assertEquals(0, amountP2);
    }

}

package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductFromSale(Integer, String, int) method.
 */
public class EZShopTestDeleteProductFromSale extends EZShopTestBase {

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

        // add product1 and product2 to the transaction
        shop.addProductToSale(transactionId, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(transactionId, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
    }

    /**
     * Tests that access rights are handled correctly by deleteProductFromSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteProductFromSale", Integer.class, String.class, int.class);
        Object[] params = {transactionId, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // test values for the product id parameter
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, (value) -> {
            shop.deleteProductFromSale(value, product1.getBarCode(), 1);
        });
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        testInvalidValues(InvalidProductCodeException.class, invalidProductCodes, (value) -> {
            shop.deleteProductFromSale(transactionId, value, 1);
        });
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     */
    @Test()
    public void testInvalidAmount() {
        // test values for the amount parameter
        testInvalidValues(InvalidQuantityException.class, invalidProductAmounts, (value) -> {
            shop.deleteProductFromSale(transactionId, product1.getBarCode(), value);
        });
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.deleteProductFromSale(transactionId, product3.getBarCode(), 1));
    }

    /**
     * If the quantity of product in the transaction cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        // try to remove the current amount of product + 1
        assertFalse(shop.deleteProductFromSale(transactionId, product1.getBarCode(), 1 + PRODUCT_TRANSACTION_AMOUNT_1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws InvalidTransactionIdException, UnauthorizedException,
            InvalidProductCodeException, InvalidQuantityException {
        shop.endSaleTransaction(transactionId);
        assertFalse(shop.deleteProductFromSale(transactionId, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));
    }

    /**
     * Delete products from a sale transaction successfully
     */
    @Test
    public void testDeleteProductFromSaleSuccessfully() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        int initialInventoryLevelForProduct1 = product1.getQuantity() - PRODUCT_TRANSACTION_AMOUNT_1;

        // 1. partially remove product 1 from the transaction
        // compute the amount of product 1 to be removed from the transaction
        int amountProduct1ToBeRemoved = PRODUCT_TRANSACTION_AMOUNT_1 / 2;
        assertTrue(shop.addProductToSale(transactionId, product1.getBarCode(), amountProduct1ToBeRemoved));

        // 1.1 verify that the quantity of PRODUCT_CODE_1 in the inventory is correctly updated
        int inventoryLevelForProduct1After = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        assertEquals(initialInventoryLevelForProduct1 + amountProduct1ToBeRemoved, inventoryLevelForProduct1After);

        // 2. completely remove product 2 from the transaction
        assertTrue(shop.deleteProductFromSale(transactionId, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2));

        // 2.1 verify that the quantity of PRODUCT_CODE_2 in the inventory is correctly updated
        assertEquals(product2.getQuantity(), shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity());

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(transactionId);
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);

        // 3.1 check amount of product 1 in the transaction
        int amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(PRODUCT_TRANSACTION_AMOUNT_1 - amountProduct1ToBeRemoved, amountP1);

        // 3.2 check amount of product 2 in the transaction
        // TODO: if a product is completely removed should it appear in the entries list with qty = 0?
        int amountP2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product2.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(0);
        assertEquals(0, amountP2);
    }

}

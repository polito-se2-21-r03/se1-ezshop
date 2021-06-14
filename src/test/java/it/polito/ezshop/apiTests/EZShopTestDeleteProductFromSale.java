package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductFromSale method.
 */
public class EZShopTestDeleteProductFromSale {

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

        // add product1 and product2 to the transaction
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
    }

    /**
     * Tests that access rights are handled correctly by deleteProductFromSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteProductFromSale",
                Integer.class, String.class, int.class);
        Object[] params = {tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2};

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
                // remove product from sale
                shop.deleteProductFromSale(value, product1.getBarCode(), 1);
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
                // remove product from sale
                shop.deleteProductFromSale(tid, value, 1);
            });
        }
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     */
    @Test()
    public void testInvalidAmount() {
        // test invalid values for the amount parameter
        for (Integer value : TestHelpers.invalidTicketEntryAmounts) {
            assertThrows(InvalidQuantityException.class, () -> {
                // remove product from sale
                shop.deleteProductFromSale(tid, product1.getBarCode(), value);
            });
        }
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.deleteProductFromSale(tid, product3.getBarCode(), 1));
    }

    /**
     * If the quantity of product in the transaction cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws Exception {
        Integer initialQtyOnShelves = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();

        // try to remove the current amount of product + 1
        assertFalse(shop.deleteProductFromSale(tid, product1.getBarCode(), 1 + PRODUCT_TRANSACTION_AMOUNT_1));

        // verify the quantity of product in the transaction did not change
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) ((EZShop)shop).getAccountBook().getTransaction(tid);
        Integer qty = sale.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(product1.getBarCode()))
                .map(it.polito.ezshop.model.TicketEntry::getAmount).findFirst().orElse(-1);
        assertEquals(PRODUCT_TRANSACTION_AMOUNT_1, qty);

        // verify the quantity of product1 on the shelves did not change
        assertEquals(initialQtyOnShelves, shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity());
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws Exception {
        Integer initialQtyOnShelves = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();

        // close the transaction...
        shop.endSaleTransaction(tid);
        // ...and try to remove a product
        assertFalse(shop.deleteProductFromSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));

        // pay the transaction...
        SaleTransaction sale = shop.getSaleTransaction(tid);
        shop.receiveCashPayment(tid, sale.getPrice());

        // ...and try to remove a product
        assertFalse(shop.deleteProductFromSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));

        // verify the quantity of product in the transaction did not change
        sale = shop.getSaleTransaction(tid);
        Integer qty = sale.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findFirst().orElse(-1);
        assertEquals(PRODUCT_TRANSACTION_AMOUNT_1, qty);

        // verify the quantity of product1 on the shelves did not change
        assertEquals(initialQtyOnShelves, shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity());
    }

    /**
     * Delete one or more product(s) from a sale transaction successfully
     */
    @Test
    public void testDeleteProductFromSaleSuccessfully() throws Exception {
        Integer initialInventoryLevelForProduct1 = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        Integer initialInventoryLevelForProduct2 = shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity();

        // 1. partially remove product 1 from the transaction
        // compute the amount of product 1 to be removed from the transaction
        int amountProduct1ToBeRemoved = PRODUCT_TRANSACTION_AMOUNT_1 / 2;
        assertTrue(shop.deleteProductFromSale(tid, product1.getBarCode(), amountProduct1ToBeRemoved));

        // 1.1 verify that the quantity of PRODUCT_CODE_1 in the inventory is correctly updated
        int inventoryLevelForProduct1After = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        assertEquals(initialInventoryLevelForProduct1 + amountProduct1ToBeRemoved, inventoryLevelForProduct1After);

        // 2. completely remove product 2 from the transaction
        assertTrue(shop.deleteProductFromSale(tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2));

        // 2.1 verify that the quantity of PRODUCT_CODE_2 in the inventory is correctly updated
        assertEquals((Integer) (initialInventoryLevelForProduct2 + PRODUCT_TRANSACTION_AMOUNT_2),
                shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity()
        );

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // 3.1 check amount of product 1 in the transaction
        int amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(PRODUCT_TRANSACTION_AMOUNT_1 - amountProduct1ToBeRemoved, amountP1);

        // 3.2 verify product2 was completely removed
        assertFalse(saleTransaction.getEntries().stream().anyMatch(x -> x.getBarCode().equals(product2.getBarCode())));
    }

}

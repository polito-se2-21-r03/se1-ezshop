package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.addProductToSale(Integer, String, int) method.
 */
public class EZShopTestAddProductToSale {

    private final EZShopInterface shop = new EZShop();

    /**
     * Id of the new sale transaction
     */
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

        // add product1 and product2 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);
        TestHelpers.addProductToShop(shop, TestHelpers.product2);

        tid = shop.startSaleTransaction();
    }

    /**
     * Tests that access rights are handled correctly by addProductToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("addProductToSale", Integer.class, String.class, int.class);
        Object[] params = {tid, TestHelpers.product1.getBarCode(), 1};

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
                // add product to sale
                shop.addProductToSale(value, TestHelpers.product1.getBarCode(), 1);
            });
        }
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test invalid values for the product code parameter
        for (String code : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> {
                // add product to sale
                shop.addProductToSale(tid, code, 1);
            });
        }
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     */
    @Test()
    public void testInvalidAmount() {
        // test invalid values for the product id parameter
        for (Integer value : TestHelpers.invalidTicketEntryAmounts) {
            assertThrows(InvalidQuantityException.class, () -> {
                // add product to sale
                shop.addProductToSale(tid, TestHelpers.product1.getBarCode(), value);
            });
        }
    }

    /**
     * If the product does not exist, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.addProductToSale(tid, TestHelpers.product3.getBarCode(), 1));
    }

    /**
     * If the quantity of product cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws Exception {
        assertFalse(shop.addProductToSale(tid, TestHelpers.product1.getBarCode(), TestHelpers.product1.getQuantity() + 1));
        assertFalse(shop.addProductToSale(tid, TestHelpers.product2.getBarCode(), TestHelpers.product2.getQuantity() + 1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedOrPaidTransaction() throws Exception {
        shop.endSaleTransaction(tid);
        assertFalse(shop.addProductToSale(tid, TestHelpers.product1.getBarCode(), TestHelpers.product1.getQuantity()));
    }

    /**
     * Add products to a sale transaction successfully
     */
    @Test
    public void testAddProductsToSaleSuccessfully() throws Exception {
        int amount = TestHelpers.product1.getQuantity() / 2;

        // 1. add amount of product1
        assertTrue(shop.addProductToSale(tid, TestHelpers.product1.getBarCode(), amount));

        // 1.1 verify that the quantity of product1 in the inventory is correctly updated
        ProductType p1 = shop.getProductTypeByBarCode(TestHelpers.product1.getBarCode());
        assertEquals((Integer) (TestHelpers.product1.getQuantity() - amount), p1.getQuantity());

        // 2. add a new amount of product1
        assertTrue(shop.addProductToSale(tid, TestHelpers.product1.getBarCode(), TestHelpers.product1.getQuantity() - amount));
        // 2.1 verify that the quantity in the inventory is correctly updated
        p1 = shop.getProductTypeByBarCode(TestHelpers.product1.getBarCode());
        assertEquals((Integer) 0, p1.getQuantity());

        // 3. add product2
        assertTrue(shop.addProductToSale(tid, TestHelpers.product2.getBarCode(), TestHelpers.product2.getQuantity()));
        // 3.1 verify that the quantity of product2 in the inventory is correctly updated
        ProductType p2 = shop.getProductTypeByBarCode(TestHelpers.product2.getBarCode());
        assertEquals((Integer) 0, p2.getQuantity());

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // 3.1 check amount of product 1 in the transaction
        int amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(TestHelpers.product1.getQuantity(), amountP1);

        // 3.2 check amount of product 2 in the transaction
        int amountP2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product2.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(0);
        assertEquals(TestHelpers.product2.getQuantity(), amountP2);
    }

}

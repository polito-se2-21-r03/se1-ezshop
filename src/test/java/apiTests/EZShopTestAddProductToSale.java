package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.addProductToSale(Integer, String, int) method.
 */
public class EZShopTestAddProductToSale extends EZShopTestBase {

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add product1 and product2 to the shop
        addProducts(product1, product2);

        tid = shop.startSaleTransaction();
    }

    /**
     * Tests that access rights are handled correctly by addProductToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("addProductToSale", Integer.class, String.class, int.class);
        Object[] params = {tid, product1.getBarCode(), 1};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // test values for the product id parameter
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, (value) -> {
            shop.addProductToSale(value, product1.getBarCode(), 1);
        });
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        testInvalidValues(InvalidProductCodeException.class, invalidProductCodes, (value) -> {
            shop.addProductToSale(tid, value, 1);
        });
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     * TODO: what if the amount is zero?
     */
    @Test()
    public void testInvalidAmount() {
        // test values for the amount parameter
        testInvalidValues(InvalidQuantityException.class, invalidProductAmounts, (value) -> {
            shop.addProductToSale(tid, product1.getBarCode(), value);
        });
    }

    /**
     * If the product does not exist, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.addProductToSale(tid, product3.getBarCode(), 1));
    }

    /**
     * If the quantity of product cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws Exception {
        assertFalse(shop.addProductToSale(tid, product1.getBarCode(), product1.getQuantity() + 1));
        assertFalse(shop.addProductToSale(tid, product2.getBarCode(), product2.getQuantity() + 1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws Exception {
        shop.endSaleTransaction(tid);
        assertFalse(shop.addProductToSale(tid, product1.getBarCode(), product1.getQuantity()));
    }

    /**
     * Add products to a sale transaction successfully
     */
    @Test
    public void testAddProductsToSaleSuccessfully() throws Exception {
        int amount = product1.getQuantity() / 2;

        // 1. add amount of product1
        assertTrue(shop.addProductToSale(tid, product1.getBarCode(), amount));

        // 1.1 verify that the quantity of product1 in the inventory is correctly updated
        ProductType p1 = shop.getProductTypeByBarCode(product1.getBarCode());
        assertEquals((Integer) (product1.getQuantity() - amount), p1.getQuantity());

        // 2. add a new amount of product1
        assertTrue(shop.addProductToSale(tid, product1.getBarCode(), product1.getQuantity() - amount));
        // 2.1 verify that the quantity in the inventory is correctly updated
        p1 = shop.getProductTypeByBarCode(product1.getBarCode());
        assertEquals((Integer) 0, p1.getQuantity());

        // 3. add product2
        assertTrue(shop.addProductToSale(tid, product2.getBarCode(), product2.getQuantity()));
        // 3.1 verify that the quantity of product2 in the inventory is correctly updated
        ProductType p2 = shop.getProductTypeByBarCode(product2.getBarCode());
        assertEquals((Integer) 0, p2.getQuantity());

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // 3.1 check amount of product 1 in the transaction
        Integer amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(product1.getQuantity(), amountP1);

        // 3.2 check amount of product 2 in the transaction
        Integer amountP2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product2.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(0);
        assertEquals(product2.getQuantity(), amountP2);
    }

}

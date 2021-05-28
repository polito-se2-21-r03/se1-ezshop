package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.product1;
import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteSaleTransaction(Integer) method.
 */
public class EZShopTestDeleteSaleTransaction {

    private static final int PRODUCT1_AMOUNT = 1;
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
        // add products product1 to the transaction
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT1_AMOUNT);
    }

    /**
     * Tests that access rights are handled correctly by deleteSaleTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteSaleTransaction", Integer.class);
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
            assertThrows(InvalidTransactionIdException.class, () -> shop.deleteSaleTransaction(value));
        }
    }

    /**
     * If the transaction does not exists, the method should return false
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.deleteSaleTransaction(tid + 1));
    }

    /**
     * If the transaction has already been paid, the method should return false
     */
    @Test
    public void testPaidTransaction() throws Exception {
        assertTrue(shop.endSaleTransaction(tid));
        shop.endSaleTransaction(tid);
        shop.receiveCashPayment(tid, 50.0);

        assertFalse(shop.deleteSaleTransaction(tid));
        assertTrue(shop.getCreditsAndDebits(null, null).stream().anyMatch(t -> t.getBalanceId() == tid));
    }

    /**
     * Delete a sale transaction successfully
     */
    @Test
    public void testDeleteSaleTransactionSuccessfully() throws Exception {
        int initialQtyProduct1 = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();

        assertTrue(shop.deleteSaleTransaction(tid));
        assertNull(shop.getSaleTransaction(tid));

        int finalQtyProduct1 = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        assertEquals(initialQtyProduct1 + PRODUCT1_AMOUNT, finalQtyProduct1);
    }

}

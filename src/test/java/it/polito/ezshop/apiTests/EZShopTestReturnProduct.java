package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
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
 * Tests on the EZShop.returnProduct(Integer, String, int) method.
 */
public class EZShopTestReturnProduct {

    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 3;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 2;

    private final EZShopInterface shop = new EZShop();

    private Integer rid, sid;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword(),
                TestHelpers.admin.getRole().getValue());
        // and log in with that user
        shop.login(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword());

        // add a product1, product2 and product3 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);
        TestHelpers.addProductToShop(shop, TestHelpers.product2);
        TestHelpers.addProductToShop(shop, TestHelpers.product3);

        // create a new transaction and add product1 and product2 to it
        sid = shop.startSaleTransaction();
        shop.addProductToSale(sid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(sid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
        shop.endSaleTransaction(sid);

        // pay the transaction
        shop.receiveCashPayment(sid, 1000.0);

        // create a return transaction
        rid = shop.startReturnTransaction(sid);
    }

    /**
     * Tests that access rights are handled correctly by returnProduct
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("returnProduct", Integer.class, String.class, int.class);
        Object[] params = {rid, product1.getBarCode(), 1};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw {@link InvalidTransactionIdException}
     */
    @Test()
    public void testInvalidId() {
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> {
                // return product 1
                shop.returnProduct(value, product1.getBarCode(), 1);
            });
        }
    }

    /**
     * If the code is null|empty|not-numeric|invalid, the method should throw {@link InvalidProductCodeException}
     */
    @Test()
    public void testProductCode() {
        // test invalid values for the product code parameter
        for (String value : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> {
                // return product 1
                shop.returnProduct(rid, value, 1);
            });
        }
    }

    /**
     * If the quantity is negative|zero, the method should throw {@link InvalidQuantityException}
     */
    @Test()
    public void testAmounts() {
        // test invalid values for the product code parameter
        for (Integer value : TestHelpers.invalidProductAmounts) {
            assertThrows(InvalidQuantityException.class, () -> {
                // return product 1
                shop.returnProduct(rid, product1.getBarCode(), value);
            });
        }
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.returnProduct(rid + 1, product1.getBarCode(), 1));
        assertFalse(shop.returnProduct(sid, product1.getBarCode(), 1));
    }

    /**
     * Start a return transaction successfully
     */
    @Test
    public void testReturnProductSuccessfully() throws Exception {
        int initialQty1 = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        int initialQty2 = shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity();

        // test non existing product
        assertFalse(shop.returnProduct(rid, product4.getBarCode(), 1));

        // test product not in transaction
        assertFalse(shop.returnProduct(rid, product3.getBarCode(), 1));

        // test amount higher than sale transaction
        assertFalse(shop.returnProduct(rid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2 + 1));

        // return entirely product1
        assertTrue(shop.returnProduct(rid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));
        assertFalse(shop.returnProduct(rid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));

        // return product2
        assertTrue(shop.returnProduct(rid, product2.getBarCode(), 1));
        assertTrue(shop.returnProduct(rid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2 - 1));
        assertFalse(shop.returnProduct(rid, product2.getBarCode(), 1));

        // verify that the quantities on the shelves for the returned products are not updated
        assertEquals(initialQty1, (int) shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity());
        assertEquals(initialQty2, (int) shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity());
    }

}

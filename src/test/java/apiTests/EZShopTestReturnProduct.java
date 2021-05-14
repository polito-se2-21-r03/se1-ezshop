package apiTests;

import it.polito.ezshop.data.EZShop;
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
 * Tests on the EZShop.returnProduct(Integer, String, int) method.
 */
public class EZShopTestReturnProduct extends EZShopTestBase {

    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 3;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_2 = 2;

    private Integer rid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1, product2, product3);

        // create a new transaction and add product1 and product2 to it
        Integer tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.addProductToSale(tid, product2.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_2);
        shop.endSaleTransaction(tid);

        // pay the transaction
        double total = product1.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_1 +
                product2.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_2;
        shop.receiveCashPayment(tid, total);

        // create a return transaction
        rid = shop.startReturnTransaction(tid);
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
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs,
                (value) -> shop.returnProduct(value, product1.getBarCode(), 1));
    }

    /**
     * If the code is null|empty|not-numeric|invalid, the method should throw {@link InvalidProductCodeException}
     */
    @Test()
    public void testProductCode() {
        testInvalidValues(InvalidProductCodeException.class, invalidProductCodes,
                (value) -> shop.returnProduct(rid, value, 1));
    }

    /**
     * If the quantity is negative|zero, the method should throw {@link InvalidQuantityException}
     */
    @Test()
    public void testAmounts() {
        testInvalidValues(InvalidQuantityException.class, invalidProductAmounts,
                (value) -> shop.returnProduct(rid, product1.getBarCode(), value));
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.returnProduct(rid + 1, product1.getBarCode(), 1));
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

        // test correct values
        assertTrue(shop.returnProduct(rid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1));
        assertTrue(shop.returnProduct(rid, product2.getBarCode(), 1));

        // verify that the quantities on the shelves for the returned products are not updated
        assertEquals(initialQty1, (int) shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity());
        assertEquals(initialQty2, (int) shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity());
    }

}

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
 * Tests on the EZShop.deleteReturnTransaction(Integer) method.
 */
public class EZShopTestDeleteReturnTransaction {

    public static final Integer PRODUCT_RETURN_AMOUNT_1 = 1;
    private static final Integer PRODUCT_TRANSACTION_AMOUNT_1 = 3;

    // interface of EZShop
    private final EZShopInterface shop = new EZShop();

    private Integer sid, rid;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword(),
                TestHelpers.admin.getRole().getValue());
        // and log in with that user
        shop.login(TestHelpers.admin.getUsername(), TestHelpers.admin.getPassword());

        // add product1 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);

        // create a new transaction and add product1 and product2 to it
        sid = shop.startSaleTransaction();
        shop.addProductToSale(sid, product1.getBarCode(), PRODUCT_TRANSACTION_AMOUNT_1);
        shop.endSaleTransaction(sid);

        // pay the transaction
        shop.receiveCashPayment(sid, product1.getPricePerUnit() * PRODUCT_TRANSACTION_AMOUNT_1);

        // create a return transaction
        rid = shop.startReturnTransaction(sid);
        shop.returnProduct(rid, product1.getBarCode(), PRODUCT_RETURN_AMOUNT_1);
        shop.endReturnTransaction(rid, true);
    }

    /**
     * Tests that access rights are handled correctly by deleteReturnTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteReturnTransaction", Integer.class);
        Object[] params = {rid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw {@link InvalidTransactionIdException}
     */
    @Test()
    public void testInvalidId() {
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> shop.deleteReturnTransaction(value));
        }
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingReturnTransaction() throws Exception {
        assertFalse(shop.deleteReturnTransaction(sid));
        assertFalse(shop.deleteReturnTransaction(rid + 1));
    }

    /**
     * Delete a non paid return transaction successfully
     */
    @Test
    public void testDeleteNonPaidReturnTransactionSuccessfully() throws Exception {
        assertTrue(shop.deleteReturnTransaction(rid));
        assertNull(((EZShop) shop).getAccountBook().getTransaction(rid));
    }

    /**
     * If the transaction has already been paid, the method should return false
     */
    @Test
    public void testDeletePaidReturnTransactionSuccessfully() throws Exception {
        shop.returnCashPayment(rid);
        assertFalse(shop.deleteReturnTransaction(rid));
        assertNotNull(((EZShop) shop).getAccountBook().getTransaction(rid));
    }

}

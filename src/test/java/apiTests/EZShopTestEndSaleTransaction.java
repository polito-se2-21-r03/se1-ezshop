package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests on the EZShop.endSaleTransaction(Integer) method.
 */
public class EZShopTestEndSaleTransaction extends EZShopTestBase {

    private Integer tid;

    @Before
    public void beforeEach() throws Exception {
        super.reset();

        // add a product1 to the shop
        addProducts(product1);

        // create a new transaction and add product1 to it
        tid = shop.startSaleTransaction();
        shop.addProductToSale(tid, product1.getBarCode(), 1);
    }

    /**
     * Tests that access rights are handled correctly by endSaleTransaction
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("endSaleTransaction", Integer.class);
        Object[] params = {tid};

        testAccessRights(targetMethod, params, Role.values());
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        testInvalidValues(InvalidTransactionIdException.class, invalidTransactionIDs, shop::endSaleTransaction);
    }

    /**
     * If the transaction does not exists, the method should return false
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.endSaleTransaction(tid + 1));
    }

    /**
     * End a sale transaction successfully
     */
    @Test
    public void testEndSaleTransactionSuccessfully() throws Exception {
        // first time the method should return true
        assertTrue(shop.endSaleTransaction(tid));
        // afterwards, the method should return false
        assertFalse(shop.endSaleTransaction(tid));

        // pay the transaction and call the method again
        shop.receiveCashPayment(tid, product1.getPricePerUnit());
        assertFalse(shop.endSaleTransaction(tid));
    }

}

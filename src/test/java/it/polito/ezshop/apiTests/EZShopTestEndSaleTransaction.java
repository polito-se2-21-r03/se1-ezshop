package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.endSaleTransaction(Integer) method.
 */
public class EZShopTestEndSaleTransaction {

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

        // add product1 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);

        tid = shop.startSaleTransaction();
        // add product1 to the transaction
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
        // test invalid values for the transaction id parameter
        for (Integer value : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> shop.endSaleTransaction(value));
        }
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
        assertNull(shop.getSaleTransaction(tid));
        assertTrue(shop.endSaleTransaction(tid));
        assertNotNull(shop.getSaleTransaction(tid));

        // afterwards, the method should return false
        assertFalse(shop.endSaleTransaction(tid));
        assertNotNull(shop.getSaleTransaction(tid));

        // pay the transaction and call the method again
        shop.receiveCashPayment(tid, product1.getPricePerUnit());
        assertFalse(shop.endSaleTransaction(tid));
        assertNotNull(shop.getSaleTransaction(tid));
    }

}

package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.adapters.ProductTypeAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static it.polito.ezshop.TestHelpers.product3;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteSaleTransaction(Integer) method.
 */
public class EZShopTestDeleteSaleTransaction {

    private static final int PRODUCT1_AMOUNT = 1;
    private final EZShopInterface shop = new EZShop();
    private Integer tid;

    private double total;

    private final String P1_RFID0 = "000000000123";
    private final String P1_RFID1 = "000000000124";
    private final String P2_RFID0 = "000000000128";
    private final String P3_RFID0 = "000000000129";

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
        int p1 = TestHelpers.addProductToShop(shop, TestHelpers.product1);
        int p2 = TestHelpers.addProductToShop(shop, product2);
        int p3 = TestHelpers.addProductToShop(shop, product3);

        // reset quantities to 0
        shop.updateQuantity(p1, -product1.getQuantity());
        shop.updateQuantity(p2, -product2.getQuantity());
        shop.updateQuantity(p3, -product3.getQuantity());

        shop.recordBalanceUpdate(1000.0);
        int oid = shop.payOrderFor(TestHelpers.product1.getBarCode(), 2, 10.0);
        shop.recordOrderArrivalRFID(oid, P1_RFID0);

        oid = shop.payOrderFor(product2.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P2_RFID0);

        // add one unit of product 3 with RFID
        oid = shop.payOrderFor(product3.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P3_RFID0);

        // add one unit of product 3 without RFID
        oid = shop.payOrderFor(product3.getBarCode(), 1, 10.0);
        shop.recordOrderArrival(oid);

        tid = shop.startSaleTransaction();

        // add products to the sale transaction
        shop.addProductToSaleRFID(tid, P1_RFID0);
        shop.addProductToSaleRFID(tid, P1_RFID1);
        shop.addProductToSaleRFID(tid, P2_RFID0);
        shop.addProductToSaleRFID(tid, P3_RFID0);
        shop.addProductToSale(tid, product3.getBarCode(), 1);

        total = 2 * product1.getPricePerUnit() + product2.getPricePerUnit() + 2 * product3.getPricePerUnit();
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
        shop.receiveCashPayment(tid, total);

        assertFalse(shop.deleteSaleTransaction(tid));
        assertTrue(shop.getCreditsAndDebits(null, null).stream().anyMatch(t -> t.getBalanceId() == tid));
    }

    /**
     * Delete a sale transaction successfully
     */
    @Test
    public void testDeleteSaleTransactionSuccessfully() throws Exception {
        // initial quantities are all zero

        assertTrue(shop.deleteSaleTransaction(tid));
        assertNull(shop.getSaleTransaction(tid));

        // 1.1 verify product 1
        ProductType p1 = shop.getProductTypeByBarCode(product1.getBarCode());
        assertEquals((Integer) 2, p1.getQuantity());
        assertTrue(((ProductTypeAdapter)p1).get().RFIDexists(P1_RFID0));
        assertTrue(((ProductTypeAdapter)p1).get().RFIDexists(P1_RFID1));

        // 1.1 verify product 2
        ProductType p2 = shop.getProductTypeByBarCode(product2.getBarCode());
        assertEquals((Integer) 1, p2.getQuantity());
        assertTrue(((ProductTypeAdapter)p2).get().RFIDexists(P2_RFID0));

        // 1.1 verify product 3
        ProductType p3 = shop.getProductTypeByBarCode(product3.getBarCode());
        assertEquals((Integer) 2, p3.getQuantity());
        assertTrue(((ProductTypeAdapter)p3).get().RFIDexists(P3_RFID0));
        assertTrue(((ProductTypeAdapter)p3).get().RFIDexists(it.polito.ezshop.model.ProductType.DUMMY_RFID));
    }

}

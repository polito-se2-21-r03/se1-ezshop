package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.adapters.ProductTypeAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static it.polito.ezshop.TestHelpers.*;
import static it.polito.ezshop.utils.Utils.DUMMY_RFID;
import static org.junit.Assert.*;

public class EZShopTestReturnProductRFID {

    private final EZShopInterface shop = new EZShop();

    /**
     * Id of the new sale transaction
     */
    private Integer rid, sid;

    private final String RFIDNotExisting = "123123123123";
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

        // add product1, product2 and product3 to the shop
        int p1 = TestHelpers.addProductToShop(shop, TestHelpers.product1);
        int p2 = TestHelpers.addProductToShop(shop, TestHelpers.product2);
        int p3 = TestHelpers.addProductToShop(shop, TestHelpers.product3);

        // reset quantities to 0
        shop.updateQuantity(p1, -product1.getQuantity());
        shop.updateQuantity(p2, -product2.getQuantity());
        shop.updateQuantity(p3, -product3.getQuantity());

        sid = shop.startSaleTransaction();

        shop.recordBalanceUpdate(1000.0);
        int oid = shop.payOrderFor(TestHelpers.product1.getBarCode(), 2, 10.0);
        shop.recordOrderArrivalRFID(oid, P1_RFID0);

        oid = shop.payOrderFor(TestHelpers.product2.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P2_RFID0);

        // add one unit of product 3 with RFID
        oid = shop.payOrderFor(TestHelpers.product3.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P3_RFID0);

        // add one unit of product 3 without RFID
        oid = shop.payOrderFor(TestHelpers.product3.getBarCode(), 1, 10.0);
        shop.recordOrderArrival(oid);

        // add product1 and product2 to the transaction
        shop.addProductToSaleRFID(sid, P1_RFID0);
        shop.addProductToSaleRFID(sid, P1_RFID1);
        shop.addProductToSaleRFID(sid, P2_RFID0);
        shop.addProductToSaleRFID(sid, P3_RFID0);
        shop.addProductToSale(sid, product3.getBarCode(), 1);
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
                shop.returnProductRFID(value, P1_RFID1);
            });
        }
    }

    /**
     * If the rfid is null|empty|NaN|invalid, the method should throw InvalidRFIDException
     */
    @Test()
    public void testInvalidProductCode() {
        // test invalid values for the product code parameter
        for (String code : TestHelpers.invalidRFIDs) {
            assertThrows(InvalidRFIDException.class, () -> {
                // add product to sale
                shop.returnProductRFID(rid, code);
            });
        }
    }

    /**
     * If the rfid does not exist, the method should return false
     */
    @Test()
    public void testRFIDDoesNotExist() throws Exception {
        assertFalse(shop.returnProductRFID(rid, RFIDNotExisting));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedOrPaidTransaction() throws Exception {
        assertTrue(shop.endReturnTransaction(rid, true));
        assertFalse(shop.returnProductRFID(rid, P1_RFID0));
    }

    /**
     * If the transaction does not exists, the method should return -1
     */
    @Test()
    public void testNonExistingTransaction() throws Exception {
        assertFalse(shop.returnProductRFID(rid + 1, P1_RFID0));
        assertFalse(shop.returnProductRFID(sid, P1_RFID0));
    }

    /**
     *  Try to return product with a given RFID but meanwhile another product with same RFID is registered in the shop
     */
    @Test()
    public void testSameRFID() throws Exception {
        int orderid = shop.payOrderFor(TestHelpers.product1.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(orderid, P2_RFID0);
        assertThrows(InvalidRFIDException.class, () -> {
            // add product to sale
            shop.returnProductRFID(rid, P2_RFID0);
        });
    }

    /**
     * Add products to the return transaction successfully
     */
    @Test
    public void testReturnProductSuccessfully() throws Exception {
        // 1. add P1_RFID0 and P1_RFID1
        assertTrue(shop.returnProductRFID(rid, P1_RFID0));

        // 1.2 add another unit of product 1
        assertTrue(shop.returnProductRFID(rid, P1_RFID1));
        assertTrue(shop.returnProductRFID(rid, P1_RFID0));
        assertFalse(shop.returnProduct(rid, product1.getBarCode(), 1));
        // 1.3 verify that the quantity of product1 in the inventory is correctly updated
        ProductType p1 = shop.getProductTypeByBarCode(TestHelpers.product1.getBarCode());
        assertEquals((Integer) 0, p1.getQuantity());

        // 2. add product2
        assertTrue(shop.returnProductRFID(rid, P2_RFID0));
        // 2.1 verify that the quantity of product2 in the inventory is correctly updated
        ProductType p2 = shop.getProductTypeByBarCode(product2.getBarCode());
        assertEquals((Integer) 0, p2.getQuantity());

        // 3. add product3 (one unit with RFID and one without)
        assertTrue(shop.returnProductRFID(rid, P3_RFID0));
        // 3.1 verify the product with dummy rfid is still there
        ProductType p3;
        //assertTrue(((ProductTypeAdapter) p3).get().RFIDexists(DUMMY_RFID));
        // 3.2 add another unit of product 3
        assertTrue(shop.returnProduct(rid, product3.getBarCode(), 1));
        // 3.3 verify that the quantity of product3 in the inventory is correctly updated
        p3 = shop.getProductTypeByBarCode(product3.getBarCode());
        assertEquals((Integer) 0, p3.getQuantity());

        // 4. verify the final status of the transaction
        assertTrue(shop.endReturnTransaction(rid, true));

        List<ProductType> products = shop.getAllProductTypes();
        // 4.1 check amount of product 1 in the shelves
        it.polito.ezshop.model.ProductType prod1 = products.stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product1.getBarCode()))
                .findAny()
                .map(x -> ((ProductTypeAdapter)x).get())
                .orElse(null);
        assertNotNull(prod1);
        assertEquals(2, prod1.getQuantity());
        assertTrue(prod1.getRFIDs().contains(P1_RFID0));
        assertTrue(prod1.getRFIDs().contains(P1_RFID1));

        // 3.2 check amount of product 2 in the transaction
        it.polito.ezshop.model.ProductType prod2 = products.stream()
                .filter(x -> x.getBarCode().equals(product2.getBarCode()))
                .findAny()
                .map(x -> ((ProductTypeAdapter)x).get())
                .orElse(null);
        assertNotNull(prod2);
        assertEquals(1, prod2.getQuantity());
        assertTrue(prod2.getRFIDs().contains(P2_RFID0));

        // 4.3 check amount of product 3 in the transaction
        it.polito.ezshop.model.ProductType prod3 = products.stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product3.getBarCode()))
                .findAny()
                .map(x -> ((ProductTypeAdapter)x).get())
                .orElse(null);
        assertNotNull(prod3);
        assertEquals(2, prod3.getQuantity());
        assertTrue(prod3.getRFIDs().contains(P3_RFID0));
        assertTrue(prod3.getRFIDs().contains(DUMMY_RFID));
    }
}

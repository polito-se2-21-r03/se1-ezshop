package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.adapters.ProductTypeAdapter;
import it.polito.ezshop.model.adapters.TicketEntryAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.addProductToSaleRFID(Integer, String) method.
 */
public class EZShopTestAddProductToSaleRFID {

    private final EZShopInterface shop = new EZShop();

    /**
     * Id of the new sale transaction
     */
    private Integer tid;

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
    }

    /**
     * Tests that access rights are handled correctly by addProductToSaleRFID.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("addProductToSaleRFID", Integer.class, String.class);
        Object[] params = {tid, P1_RFID0};

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
                shop.addProductToSaleRFID(value, P1_RFID0);
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
                shop.addProductToSaleRFID(tid, code);
            });
        }
    }

    /**
     * If the rfid does not exist, the method should return false
     */
    @Test()
    public void testRFIDDoesNotExist() throws Exception {
        assertFalse(shop.addProductToSaleRFID(tid, RFIDNotExisting));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedOrPaidTransaction() throws Exception {
        shop.endSaleTransaction(tid);
        assertFalse(shop.addProductToSaleRFID(tid, P1_RFID0));
    }

    /**
     * Add products to a sale transaction successfully
     */
    @Test
    public void testAddProductsToSaleSuccessfully() throws Exception {
        // 1. add P1_RFID0 and P1_RFID1
        assertTrue(shop.addProductToSaleRFID(tid, P1_RFID0));
        // 1.1 verify P1_RFID1 is still there
        ProductType p1 = shop.getProductTypeByBarCode(product1.getBarCode());
        assertTrue(((ProductTypeAdapter) p1).get().RFIDexists(P1_RFID1));
        // 1.2 add another unit of product 1
        assertTrue(shop.addProductToSaleRFID(tid, P1_RFID1));
        assertFalse(shop.addProductToSaleRFID(tid, P1_RFID0));
        assertFalse(shop.addProductToSale(tid, product1.getBarCode(), 1));
        // 1.3 verify that the quantity of product1 in the inventory is correctly updated
        p1 = shop.getProductTypeByBarCode(TestHelpers.product1.getBarCode());
        assertEquals((Integer) 0, p1.getQuantity());

        // 2. add product2
        assertTrue(shop.addProductToSaleRFID(tid, P2_RFID0));
        // 2.1 verify that the quantity of product2 in the inventory is correctly updated
        ProductType p2 = shop.getProductTypeByBarCode(product2.getBarCode());
        assertEquals((Integer) 0, p2.getQuantity());

        // 3. add product3 (one unit with RFID and one without)
        assertTrue(shop.addProductToSaleRFID(tid, P3_RFID0));
        // 3.1 verify the product with dummy rfid is still there
        ProductType p3 = shop.getProductTypeByBarCode(product3.getBarCode());
        assertTrue(((ProductTypeAdapter) p3).get().RFIDexists(it.polito.ezshop.model.ProductType.DUMMY_RFID));
        // 3.2 add another unit of product 3
        assertTrue(shop.addProductToSale(tid, product3.getBarCode(), 1));
        // 3.3 verify that the quantity of product3 in the inventory is correctly updated
        p3 = shop.getProductTypeByBarCode(product3.getBarCode());
        assertEquals((Integer) 0, p2.getQuantity());

        // 4. verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // 4.1 check amount of product 1 in the transaction
        it.polito.ezshop.model.TicketEntry te1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product1.getBarCode()))
                .findAny()
                .map(x -> ((TicketEntryAdapter)x).get())
                .orElse(null);
        assertNotNull(te1);
        assertEquals(2, te1.getAmount());
        assertTrue(te1.getRFIDs().contains(P1_RFID0));
        assertTrue(te1.getRFIDs().contains(P1_RFID1));

        // 3.2 check amount of product 2 in the transaction
        it.polito.ezshop.model.TicketEntry te2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product2.getBarCode()))
                .findAny()
                .map(x -> ((TicketEntryAdapter)x).get())
                .orElse(null);
        assertNotNull(te2);
        assertEquals(1, te2.getAmount());
        assertTrue(te2.getRFIDs().contains(P2_RFID0));

        // 4.3 check amount of product 3 in the transaction
        it.polito.ezshop.model.TicketEntry te3 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(TestHelpers.product3.getBarCode()))
                .findAny()
                .map(x -> ((TicketEntryAdapter)x).get())
                .orElse(null);
        assertNotNull(te3);
        assertEquals(2, te3.getAmount());
        assertTrue(te3.getRFIDs().contains(P3_RFID0));
        assertTrue(te3.getRFIDs().contains(it.polito.ezshop.model.ProductType.DUMMY_RFID));
    }

}

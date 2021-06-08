package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.Role;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.deleteProductFromSaleRFID method.
 */
public class EZShopTestDeleteProductFromSaleRFID {

    private final EZShopInterface shop = new EZShop();
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

        // add product1, product2 and product3 to the shop
        TestHelpers.addProductToShop(shop, TestHelpers.product1);
        TestHelpers.addProductToShop(shop, TestHelpers.product2);
        TestHelpers.addProductToShop(shop, TestHelpers.product3);

        tid = shop.startSaleTransaction();

        shop.recordBalanceUpdate(1000.0);
        int oid = shop.payOrderFor(TestHelpers.product1.getBarCode(), 2, 10.0);
        shop.recordOrderArrivalRFID(oid, P1_RFID0);

        oid = shop.payOrderFor(TestHelpers.product2.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P2_RFID0);

        oid = shop.payOrderFor(TestHelpers.product3.getBarCode(), 1, 10.0);
        shop.recordOrderArrivalRFID(oid, P3_RFID0);

        // add product1 and product2 to the transaction
        shop.addProductToSaleRFID(tid, P1_RFID0);
        shop.addProductToSaleRFID(tid, P1_RFID1);
        shop.addProductToSaleRFID(tid, P2_RFID0);
    }

    /**
     * Tests that access rights are handled correctly by deleteProductFromSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("deleteProductFromSaleRFID", Integer.class, String.class);
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
                // remove product from sale
                shop.deleteProductFromSaleRFID(value, P1_RFID0);
            });
        }
    }

    /**
     * If the RFID is null|empty|NaN|invalid, the method should throw InvalidRFIDException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the RFID parameter
        for (String value : invalidRFIDs) {
            assertThrows(InvalidRFIDException.class, () -> {
                // remove product from sale
                shop.deleteProductFromSaleRFID(tid, P1_RFID0);
            });
        }
    }

    /**
     * If the product does not exist in the transaction, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws Exception {
        assertFalse(shop.deleteProductFromSale(tid, product3.getBarCode(), 1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws Exception {
        Integer initialQtyOnShelves = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();

        // close the transaction...
        shop.endSaleTransaction(tid);
        // ...and try to remove a product
        assertFalse(shop.deleteProductFromSaleRFID(tid, P1_RFID0));

        // pay the transaction...
        SaleTransaction sale = shop.getSaleTransaction(tid);
        shop.receiveCashPayment(tid, sale.getPrice());

        // ...and try to remove a product
        assertFalse(shop.deleteProductFromSaleRFID(tid, P1_RFID0));

        // verify the quantity of product in the transaction did not change
        sale = shop.getSaleTransaction(tid);
        Integer qty = sale.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .map(TicketEntry::getAmount).findFirst().orElse(-1);
        assertEquals((Integer) 1, qty);

        // verify the quantity of product1 on the shelves did not change
        assertEquals(initialQtyOnShelves, shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity());
    }

    /**
     * Delete one or more product(s) from a sale transaction successfully
     */
    @Test
    public void testDeleteProductFromSaleSuccessfully() throws Exception {
        Integer initialInventoryLevelForProduct1 = shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity();
        Integer initialInventoryLevelForProduct2 = shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity();

        // 1.1 remove product 1 from the transaction
        assertTrue(shop.deleteProductFromSaleRFID(tid, P1_RFID0));

        // 1.2 completely remove product 2 from the transaction
        assertTrue(shop.deleteProductFromSaleRFID(tid, P2_RFID0));

        // 1.3 verify that the quantity of product 1 in the inventory is correctly updated
        assertEquals((Integer) (initialInventoryLevelForProduct1 + 1),
                shop.getProductTypeByBarCode(product1.getBarCode()).getQuantity()
        );

        // 1.3 verify that the quantity of product 2 in the inventory is correctly updated
        assertEquals((Integer) (initialInventoryLevelForProduct2 + 1),
                shop.getProductTypeByBarCode(product2.getBarCode()).getQuantity()
        );

        // 2. verify the final status of the transaction
        shop.endSaleTransaction(tid);
        SaleTransaction saleTransaction = shop.getSaleTransaction(tid);

        // 2.1 check amount of product 1 in the transaction
        int amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(product1.getBarCode()))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(1, amountP1);

        // 3.2 verify product2 was completely removed
        assertFalse(saleTransaction.getEntries().stream().anyMatch(x -> x.getBarCode().equals(product2.getBarCode())));
    }

}

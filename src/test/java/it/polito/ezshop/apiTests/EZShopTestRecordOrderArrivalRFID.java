package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.exceptions.InvalidOrderIdException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import it.polito.ezshop.model.adapters.ProductTypeAdapter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestRecordOrderArrivalRFID {
    private static final EZShop shop = new EZShop();
    private static User admin;

    private static final double initialBalance = 1000;

    private static int product1ID;
    private static final String product1Description = "Nutella";
    private static final String product1Barcode = "12345678901231";
    private static final String product1Position = "1-1-1";
    private static int product2ID;
    private static final String product2Description = "Coke";
    private static final String product2Barcode = "123456789012";
    private static final String product2Position = "1-1-2";

    private static Integer order1ID;
    private static final int order1Amount = 100;
    private static final String order1ValidRFID = "000000000000";
    private static Integer order2ID;
    private static final int order2Amount = 40;
    private static final String order2UnavailableRFID = "000000000099";
    private static final String order2ValidRFID = "000000000100";

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void beforeEach() throws Exception {

        // reset the state of EZShop
        shop.reset();

        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // add initial funds to shop
        shop.recordBalanceUpdate(initialBalance);

        // insert some products into the shop
        product1ID = shop.createProductType(product1Description, product1Barcode, 1.0, null);
        product2ID = shop.createProductType(product2Description, product2Barcode, 1.0, null);
        shop.updatePosition(product1ID, product1Position);
        shop.updatePosition(product2ID, product2Position);

        // issue order
        order1ID = shop.issueOrder(product1Barcode, order1Amount, 1.0);
        order2ID = shop.issueOrder(product2Barcode, order2Amount, 1.0);
    }

    /**
     * Tests that access rights are handled correctly by recordOrderArrival.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("recordOrderArrivalRFID", Integer.class, String.class);
        Object[] params = {order1ID, order1ValidRFID};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the order id is null or <= 0, the method should throw InvalidOrderIdException
     */
    @Test
    public void testInvalidOrderId() {
        // test invalid values for the product id parameter
        for (Integer id : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidOrderIdException.class, () -> shop.recordOrderArrivalRFID(id, order1ValidRFID));
        }
    }

    /**
     * If the RFID is null|empty|NaN|invalid, the method should throw InvalidRFIDException
     */
    @Test
    public void testInvalidRFID() {
        // test invalid values for the RFID parameter
        for (String rfid : TestHelpers.invalidRFIDs) {
            assertThrows(InvalidRFIDException.class, () -> shop.addProductToSaleRFID(order1ID, rfid));
        }
    }

    /**
     * Record one order arrival successfully
     */
    @Test
    public void testValidRecordOrderArrival() throws Exception {

        // Change status to PAID
        assertTrue(shop.payOrder(order1ID));

        // record successful reception of order
        assertTrue(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // get modified product type
        ProductType product = shop.getAllProductTypes().stream()
                .filter(p -> p.getId() == product1ID)
                .map(p -> ((ProductTypeAdapter) p).get())
                .findAny()
                .orElse(null);
        assertNotNull(product);

        // available quantity was updated correctly
        assertEquals(order1Amount, product.getQuantity());

        // verify correct RFIDs were added to the shop
        List<String> expectedRFIDs = ProductType.generateRFIDs(order1ValidRFID, order1Amount);
        List<String> actualRFIDs = product.getRFIDs();
        // sort lists to compare easier
        expectedRFIDs.sort(String::compareTo);
        actualRFIDs.sort(String::compareTo);
        // compare lists
        assertArrayEquals(expectedRFIDs.toArray(), actualRFIDs.toArray());
    }

    /**
     * Record a second order arrival successfully
     */
    @Test
    public void testRecord2OrderArrivalsSuccessfully() throws Exception {

        // Change statuses to PAID
        assertTrue(shop.payOrder(order1ID));
        assertTrue(shop.payOrder(order2ID));

        // record successful reception of orders
        assertTrue(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));
        assertTrue(shop.recordOrderArrivalRFID(order2ID, order2ValidRFID));

        // get product type of second order
        ProductType product = shop.getAllProductTypes().stream()
                .filter(p -> p.getId() == product2ID)
                .map(p -> ((ProductTypeAdapter) p).get())
                .findAny()
                .orElse(null);
        assertNotNull(product);

        // available quantity was updated correctly
        assertEquals(order2Amount, product.getQuantity());

        // verify correct RFIDs were added to the shop
        List<String> expectedRFIDs = ProductType.generateRFIDs(order2ValidRFID, order2Amount);
        List<String> actualRFIDs = product.getRFIDs();
        // sort lists to compare easier
        expectedRFIDs.sort(String::compareTo);
        actualRFIDs.sort(String::compareTo);
        // compare lists
        assertArrayEquals(expectedRFIDs.toArray(), actualRFIDs.toArray());
    }

    /**
     * Calling recordOrderArrival for an order in CLOSED state returns true and leaves the shop unchanged
     */
    @Test
    public void testOrderInClosedStateNoEffect() throws Exception {

        // Change status to PAID
        assertTrue(shop.payOrder(order1ID));

        // record successful reception of order (set state to CLOSED)
        assertTrue(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // record successful reception of order for second time
        assertTrue(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // get modified product type
        ProductType product = shop.getAllProductTypes().stream()
                .filter(p -> p.getId() == product1ID)
                .map(p -> ((ProductTypeAdapter) p).get())
                .findAny()
                .orElse(null);
        assertNotNull(product);

        // available quantity changed only once
        assertEquals(order1Amount, product.getQuantity());
    }

    /**
     * If the product does not have a location assigned the order can not be recorded
     */
    @Test
    public void testInvalidLocationException() throws Exception {

        // remove location associated to product
        shop.updatePosition(product1ID, null);

        // Change status to PAID
        assertTrue(shop.payOrder(order1ID));

        // trying to record order throws exception
        assertThrows(InvalidLocationException.class, () -> shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // get product type of order
        ProductType product = shop.getAllProductTypes().stream()
                .filter(p -> p.getId() == product1ID)
                .map(p -> ((ProductTypeAdapter) p).get())
                .findAny()
                .orElse(null);
        assertNotNull(product);

        // available quantity did not change
        assertEquals(0, product.getQuantity());
    }

    /**
     * Trying to record the arrival of an order that is in CLOSED state (not PAID) returns false
     */
    @Test
    public void testRecordOrderArrivalClosedStateReturnsFalse() throws Exception {

        // reject record order arrival for unpaid order
        assertFalse(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // verify products were not added to shop
        assertEquals(Optional.of(0), shop.getAllProductTypes().stream().filter(p -> p.getId() == product1ID)
                .findAny().map(p -> ((ProductTypeAdapter) p).get().getRFIDs().size()));
    }

    /**
     * If some of the RFIDs are not available, recordOrderArrivalRFID returns false and no changes are made to the shop
     */
    @Test
    public void testRecordArrivalRFIDsNotAvailableThrowsException() throws Exception {

        // Change statuses to PAID
        assertTrue(shop.payOrder(order1ID));
        assertTrue(shop.payOrder(order2ID));

        // record successful reception of 1. order
        assertTrue(shop.recordOrderArrivalRFID(order1ID, order1ValidRFID));

        // second order arrival can not be recorded
        assertThrows(InvalidRFIDException.class, () -> shop.recordOrderArrivalRFID(order2ID, order2UnavailableRFID));

        // get product type for which recordOrderArrivalRFID failed
        ProductType product = shop.getAllProductTypes().stream()
                .filter(p -> p.getId() == product2ID)
                .map(p -> ((ProductTypeAdapter) p).get())
                .findAny()
                .orElse(null);
        assertNotNull(product);

        // no products were added
        assertEquals(0, product.getQuantity());
    }
}

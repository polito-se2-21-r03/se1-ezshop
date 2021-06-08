package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;

public class TestProductType {

    private static final int id = 1;
    private static final String productDescription = "Coca Cola 1.5L";
    private static final String barcode = "123456789012";
    private static final double pricePerUnit = 1.5;
    private static final String note = "Lorem ipsum";
    private static final int quantity = 2;
    private static final Position position;

    private static final String newProductDescription = "Nutella 750gr";
    private static final String newBarcode = "1234567890128";
    private static final double newPricePerUnit = 9.5;
    private static final String newNote = "dolor sit amet";
    private static final int newQuantity = 5;
    private static final Position newPosition;

    static {
        try {
            position = new Position("1-1-1");
            newPosition = new Position("1-1-2");
        } catch (InvalidLocationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test
    public void testConstructors() throws Exception {
        ProductType product1 = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        assertEquals(id, product1.getId());
        assertEquals(productDescription, product1.getProductDescription());
        assertEquals(barcode, product1.getBarCode());
        assertEquals(pricePerUnit, product1.getPricePerUnit(), 0.001);
        assertEquals(note, product1.getNote());
        assertEquals(0, product1.getQuantity());
        assertNull(product1.getPosition());

        ProductType product2 = new ProductType(id, productDescription, barcode, pricePerUnit, note, quantity, position);
        assertEquals(id, product2.getId());
        assertEquals(productDescription, product2.getProductDescription());
        assertEquals(barcode, product2.getBarCode());
        assertEquals(pricePerUnit, product2.getPricePerUnit(), 0.001);
        assertEquals(note, product2.getNote());
        assertEquals(quantity, product2.getQuantity());
        assertEquals(position, product2.getPosition());
    }

    @Test
    public void testConstructorIllegalIds() {
        for (Integer id:invalidProductIDs) {
            assertThrows(InvalidProductIdException.class, () -> new ProductType(id, productDescription, barcode, pricePerUnit, note));
        }
    }

    @Test
    public void testConstructorIllegalDescriptions() {
        for (String productDescription:invalidProductDescriptions) {
            assertThrows(InvalidProductDescriptionException.class, () -> new ProductType(id, productDescription, barcode, pricePerUnit, note));
        }
    }

    @Test
    public void testConstructorIllegalBarcodes() {
        for (String barcode:invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> new ProductType(id, productDescription, barcode, pricePerUnit, note));
        }
    }

    @Test
    public void testConstructorIllegalPricesPerUnit() {
        for (Double pricePerUnit:invalidPricesPerUnit) {
            assertThrows(InvalidPricePerUnitException.class, () -> new ProductType(id, productDescription, barcode, pricePerUnit, note));
        }
    }

    @Test
    public void testConstructorIllegalQuantities() {
        for (Integer quantity:invalidProductAmounts) {
            assertThrows(InvalidQuantityException.class, () -> new ProductType(id, productDescription, barcode, pricePerUnit, note, quantity, position));
        }
    }

    @Test
    public void testConstructorNoteNullSetsToEmptyString() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, null);
        assertEquals("", product.getNote());
    }

    @Test
    public void testSetIllegalDescriptions() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        for (String productDescription:invalidProductDescriptions) {
            assertThrows(InvalidProductDescriptionException.class, () -> product.setProductDescription(productDescription));
        }
    }

    @Test
    public void testSetIllegalBarcodes() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        for (String barcode:invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> product.setBarCode(barcode));
        }
    }

    @Test
    public void testSetIllegalPricesPerUnit() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        for (Double pricePerUnit:invalidPricesPerUnit) {
            assertThrows(InvalidPricePerUnitException.class, () -> product.setPricePerUnit(pricePerUnit));
        }
    }

    @Test
    public void testSetIllegalQuantities() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        product.setPosition(position);
        for (Integer quantity:invalidProductAmounts) {
            assertThrows(InvalidQuantityException.class, () -> product.setQuantity(quantity));
        }
    }

    @Test
    public void testSetNoteNullResultsEmptyString() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        product.setNote(null);
        assertEquals("", product.getNote());
    }

    @Test
    public void testExceptionSetQuantityWithoutLocation() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note);
        assertNull(product.getPosition());
        assertThrows(IllegalStateException.class, () -> product.setQuantity(10));
    }

    @Test
    public void testGetters() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, quantity, position);

        assertEquals(id, product.getId());

        product.setProductDescription(newProductDescription);
        assertEquals(newProductDescription, product.getProductDescription());

        product.setBarCode(newBarcode);
        assertEquals(newBarcode, product.getBarCode());

        product.setPricePerUnit(newPricePerUnit);
        assertEquals(newPricePerUnit, product.getPricePerUnit(), 0.001);

        product.setNote(newNote);
        assertEquals(newNote, product.getNote());

        product.setQuantity(newQuantity);
        assertEquals(newQuantity, product.getQuantity());
        assertTrue(product.RFIDexists(ProductType.DUMMY_RFID));

        product.setPosition(newPosition);
        assertEquals(newPosition, product.getPosition());
    }

    @Test
    public void testGenerateRFIDs () throws InvalidRFIDException {
        // test a bunch of invalid RFIDs
        for (String invalidRFID : invalidRFIDs) {
            assertThrows(InvalidRFIDException.class, () -> ProductType.generateRFIDs(invalidRFID, 10));
        }

        // the following RFID should generate an overflow
        assertThrows(InvalidRFIDException.class, () -> ProductType.generateRFIDs("999999999999", 10));

        List<String> RFIDs = ProductType.generateRFIDs("000000000123", -1);
        assertNotNull(RFIDs);
        assertEquals(0, RFIDs.size());

        RFIDs = ProductType.generateRFIDs("000000000123", 0);
        assertNotNull(RFIDs);
        assertEquals(0, RFIDs.size());

        RFIDs = ProductType.generateRFIDs("000000000123", 3);
        assertNotNull(RFIDs);
        assertEquals("000000000123", RFIDs.get(0));
        assertEquals("000000000124", RFIDs.get(1));
        assertEquals("000000000125", RFIDs.get(2));
    }

    @Test
    public void testAddRFID () throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, 0, position);

        String RFID = "000000000123";

        // add some invalid RFIDs
        for (String invalidRFID : invalidRFIDs) {
            assertFalse(product.addRFID(invalidRFID));
        }

        // add a valid RFID
        assertTrue(product.addRFID(RFID));
        assertFalse(product.addRFID(RFID));
        assertTrue(product.RFIDexists(RFID));

        // add a dummy RFID
        assertTrue(product.addRFID(ProductType.DUMMY_RFID));
        assertTrue(product.addRFID(ProductType.DUMMY_RFID));
        assertTrue(product.RFIDexists(ProductType.DUMMY_RFID));

        assertEquals(3, product.getQuantity());
    }

    @Test
    public void testAddRFIDs () throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, 0, position);

        List<String> RFIDs = Arrays.asList("000000000123", "000000000124", "000000000125");

        product.addRFIDs(RFIDs);
        assertTrue(product.RFIDexists("000000000123"));
        assertTrue(product.RFIDexists("000000000124"));
        assertTrue(product.RFIDexists("000000000125"));

        assertEquals(3, product.getQuantity());
    }

    @Test
    public void testDummyRFIDs () throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, 0, position);

        product.addDummyRFIDs(3);
        assertTrue(product.RFIDexists(ProductType.DUMMY_RFID));

        assertEquals(3, product.getQuantity());
    }

    @Test
    public void testRemoveRFIDs () throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, 0, position);

        List<String> RFIDs = Arrays.asList("000000000123", "000000000124", "000000000125");

        product.addRFIDs(RFIDs);
        product.removeRFID("000000000123");
        assertFalse(product.RFIDexists("000000000123"));
        assertTrue(product.RFIDexists("000000000124"));
        assertTrue(product.RFIDexists("000000000125"));

        assertEquals(2, product.getQuantity());
    }
}

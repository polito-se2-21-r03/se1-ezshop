package it.polito.ezshop.unitTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import org.junit.Test;

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

        product.setPosition(newPosition);
        assertEquals(newPosition, product.getPosition());
    }
}

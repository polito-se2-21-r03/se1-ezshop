package unitTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.adapters.ProductTypeAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;
import static unitTests.TestHelpers.*;
import static unitTests.TestHelpers.invalidProductAmounts;

public class TestProductTypeAdapter {

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
    private static final String newPosition = "1-1-2";

    static {
        try {
            position = new Position("1-1-1");
        } catch (InvalidLocationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Before
    public void beforeEach() {

    }

    @Test
    public void testConstructor() throws Exception {
        ProductType product = new ProductType(id, productDescription, barcode, pricePerUnit, note, quantity, position);
        ProductTypeAdapter adapter = new ProductTypeAdapter(product);

        assertEquals(new Integer(product.getId()), adapter.getId());
        assertEquals(product.getBarCode(), adapter.getBarCode());
        assertEquals(product.getProductDescription(), adapter.getProductDescription());
        assertEquals(product.getPricePerUnit(), adapter.getPricePerUnit(), 0.001);
        assertEquals(product.getNote(), adapter.getNote());
        assertEquals(new Integer(product.getQuantity()), adapter.getQuantity());
        assertEquals(product.getPosition().toString(), adapter.getLocation());
    }

    @Test
    public void testSetIDIllegal() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        assertThrows(UnsupportedOperationException.class, () -> product.setId(1));
    }

    @Test
    public void testSetIllegalDescriptions() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        for (String productDescription:invalidProductDescriptions) {
            assertThrows(IllegalArgumentException.class, () -> product.setProductDescription(productDescription));
        }
    }

    @Test
    public void testSetIllegalBarcodes() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        for (String barcode:invalidProductCodes) {
            assertThrows(IllegalArgumentException.class, () -> product.setBarCode(barcode));
        }
    }

    @Test
    public void testSetIllegalPricesPerUnit() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        for (Double pricePerUnit:invalidPricesPerUnit) {
            assertThrows(IllegalArgumentException.class, () -> product.setPricePerUnit(pricePerUnit));
        }
    }

    @Test
    public void testSetIllegalQuantities() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        product.setLocation(position.toString());
        for (Integer quantity:invalidProductAmounts) {
            assertThrows(IllegalArgumentException.class, () -> product.setQuantity(quantity));
        }
    }

    @Test
    public void testExceptionSetQuantityWithoutLocation() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));
        assertNull(product.getLocation());
        assertThrows(IllegalStateException.class, () -> product.setQuantity(10));
    }

    @Test
    public void testGetters() throws Exception {
        ProductTypeAdapter product = new ProductTypeAdapter(new ProductType(id, productDescription, barcode, pricePerUnit, note));

        assertEquals(new Integer(id), product.getId());

        product.setProductDescription(newProductDescription);
        assertEquals(newProductDescription, product.getProductDescription());

        product.setBarCode(newBarcode);
        assertEquals(newBarcode, product.getBarCode());

        product.setPricePerUnit(newPricePerUnit);
        assertEquals(newPricePerUnit, product.getPricePerUnit(), 0.001);

        product.setNote(newNote);
        assertEquals(newNote, product.getNote());

        product.setLocation(newPosition);
        assertEquals(newPosition, product.getLocation());

        product.setQuantity(newQuantity);
        assertEquals(new Integer(newQuantity), product.getQuantity());
    }
}

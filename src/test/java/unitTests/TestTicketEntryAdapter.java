package unitTests;

import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.TicketEntry;
import it.polito.ezshop.model.adapters.TicketEntryAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestTicketEntryAdapter {

    private final ProductType product;
    private final int amount = 1;
    private final double discount = 0.2;

    public TestTicketEntryAdapter() throws Exception {
        product = new ProductType(1, "Coca Cola 1.5L", "123456789012", 1.5,
                "Lorem ipsum", 2, new Position("1-1-1"));
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new TicketEntryAdapter(null));
    }

    @Test
    public void testSetters() throws Exception {
        TicketEntry ticketEntry = new TicketEntry(product, amount, 0.2);
        TicketEntryAdapter ticketEntryAdapter = new TicketEntryAdapter(ticketEntry);

        // test get/set barcode
        for (String code : TestHelpers.invalidProductCodes) {
            assertThrows(IllegalArgumentException.class, () -> ticketEntryAdapter.setBarCode(code));
        }

        ticketEntryAdapter.setBarCode("1234567890128");
        assertEquals("1234567890128", ticketEntryAdapter.getBarCode());

        // test get/set product description
        for (String desc : TestHelpers.invalidProductDescriptions) {
            assertThrows(IllegalArgumentException.class, () -> ticketEntryAdapter.setProductDescription(desc));
        }

        ticketEntryAdapter.setProductDescription("desc");
        assertEquals("desc", ticketEntryAdapter.getProductDescription());

        // test get/set amount
        for (Integer amount : TestHelpers.invalidTicketEntryAmounts) {
            assertThrows(IllegalArgumentException.class, () -> ticketEntryAdapter.setAmount(amount));
        }

        ticketEntryAdapter.setAmount(12);
        assertEquals(12, ticketEntryAdapter.getAmount());

        // test get/set price per unit
        for (double price : TestHelpers.invalidPricesPerUnit) {
            assertThrows(IllegalArgumentException.class, () -> ticketEntryAdapter.setPricePerUnit(price));
        }

        ticketEntryAdapter.setPricePerUnit(42.0);
        assertEquals(42.0, ticketEntryAdapter.getPricePerUnit(), 0.01);

        // test get/set discount rate
        for (double discountRate : TestHelpers.invalidDiscountRates) {
            assertThrows(IllegalArgumentException.class, () -> ticketEntryAdapter.setDiscountRate(discountRate));
        }

        ticketEntryAdapter.setDiscountRate(.2);
        assertEquals(.2, ticketEntryAdapter.getDiscountRate(), 0.01);
    }
}

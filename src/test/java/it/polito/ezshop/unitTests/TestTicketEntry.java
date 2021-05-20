package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.model.*;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotEquals;

public class TestTicketEntry {

    private final ProductType product;
    private final int amount = 1;
    private final double discount = 0.2;

    public TestTicketEntry() throws Exception {
        product = new ProductType(1, "Coca Cola 1.5L", "123456789012", 1.5,
                "Lorem ipsum", 2, new Position("1-1-1"));
    }

    @Test
    public void testConstructor() throws Exception {
        assertThrows(NullPointerException.class, () -> new TicketEntry(null, 1));

        for (Integer amount : TestHelpers.invalidTicketEntryAmounts) {
            assertThrows(InvalidQuantityException.class, () -> new TicketEntry(product, amount));
        }

        for (double discount : TestHelpers.invalidDiscountRates) {
            assertThrows(InvalidDiscountRateException.class, () -> new TicketEntry(product, amount, discount));
        }

        TicketEntry ticketEntry = new TicketEntry(product, amount);
        assertEquals(product, ticketEntry.getProductType());
        assertEquals(product.getPricePerUnit(), ticketEntry.getPricePerUnit(), 0.01);
        assertEquals(amount, ticketEntry.getAmount());
        assertEquals(0.0, ticketEntry.getDiscountRate(), 0.01);

        ticketEntry = new TicketEntry(product, amount, 0.2);
        assertEquals(discount, ticketEntry.getDiscountRate(), 0.01);
    }

    @Test
    public void testSetPricePerUnit() throws Exception {
        TicketEntry ticketEntry = new TicketEntry(product, amount, 0.2);

        for (double value : TestHelpers.invalidPricesPerUnit) {
            assertThrows(InvalidPricePerUnitException.class, () -> ticketEntry.setPricePerUnit(value));
        }

        ticketEntry.setPricePerUnit(10.2);
        assertEquals(10.2, ticketEntry.getPricePerUnit(), 0.01);
    }

    @Test
    public void testSetAmount() throws Exception {
        TicketEntry ticketEntry = new TicketEntry(product, amount, 0.2);

        for (Integer value : TestHelpers.invalidTicketEntryAmounts) {
            assertThrows(InvalidQuantityException.class, () -> ticketEntry.setAmount(value));
        }

        ticketEntry.setAmount(42);
        assertEquals(42, ticketEntry.getAmount());
    }

    @Test
    public void testSetDiscountRate() throws Exception {
        TicketEntry ticketEntry = new TicketEntry(product, amount, 0.2);

        for (double discountRate : TestHelpers.invalidDiscountRates) {
            assertThrows(InvalidDiscountRateException.class, () -> ticketEntry.setDiscountRate(discountRate));
        }

        ticketEntry.setDiscountRate(.23);
        assertEquals(.23, ticketEntry.getDiscountRate(), 0.01);
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        TicketEntry obj = new TicketEntry(product, amount, 0.2);
        TicketEntry same = new TicketEntry(product, amount, 0.2);
        TicketEntry different = new TicketEntry(product, amount + 1, 0.6);

        assertNotEquals(obj, null);
        assertNotEquals(obj, "boost coverage");

        assertEquals(obj, obj);

        assertEquals(obj, same);
        assertNotEquals(obj, different);

        assertEquals(obj.hashCode(), same.hashCode());
        assertNotEquals(obj.hashCode(), different.hashCode());
    }
}

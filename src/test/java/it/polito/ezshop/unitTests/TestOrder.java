package it.polito.ezshop.unitTests;

import it.polito.ezshop.model.Order;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TestOrder {

    private static final int id = 1;
    private static final LocalDate date = LocalDate.now();
    private static final String code = "123456789012";
    private static final double price = 1.0;
    private static final int quantity = 1;

    @Test
    public void testConstructor() {
        // invalid date parameter (null)
        assertThrows(NullPointerException.class, () -> new Order(id, null, code, price, quantity));

        // invalid product code parameter (null)
        assertThrows(NullPointerException.class, () -> new Order(id, date, null, price, quantity));

        Order o = new Order(id, date, code, price, quantity);
        assertEquals(id, o.getBalanceId());
        assertEquals(date, o.getDate());
        assertEquals(price, o.getPricePerUnit(), 0.01);
        assertEquals(quantity, o.getQuantity());
    }

    @Test
    public void testSetters() {
        Order o = new Order(id, date, code, price, quantity);

        o.setDate(LocalDate.parse("2021-12-21"));
        assertEquals(LocalDate.parse("2021-12-21"), o.getDate());

        o.setProductCode("1234567890128");
        assertEquals("1234567890128", o.getProductCode());

        o.setPricePerUnit(11.0);
        assertEquals(11.0, o.getPricePerUnit(), 0.01);

        o.setQuantity(11);
        assertEquals(11, o.getQuantity());
    }

    @Test
    public void testEqualsAndHashCode() {
        Order obj = new Order(id, date, code, price, quantity);
        Order same = new Order(id, date, code, price, quantity);
        Order different = new Order(id, date, code, price, 42);

        assertEquals(obj, same);
        assertEquals(obj.hashCode(), same.hashCode());

        assertNotEquals(obj, different);
        assertNotEquals(obj.hashCode(), different.hashCode());
    }

}

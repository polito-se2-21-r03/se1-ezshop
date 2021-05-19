package it.polito.ezshop.unitTests;

import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.ReturnTransactionItem;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ReturnTransactionItem
 */
public class TestReturnTransactionItem {

    private final ProductType product1, product2;
    private final int amount = 2;
    private final double price = 1.5;

    public TestReturnTransactionItem() throws Exception {
        product1 = new ProductType(1, "Coca Cola 1.5L", "123456789012", 1.5,
                "Lorem ipsum", 2, new Position("1-1-1"));
        product2 = new ProductType(2, "Nutella", "123456789012", 2.5,
                "Lorem ipsum", 2, new Position("1-1-1"));
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new ReturnTransactionItem(null, amount, price));

        ReturnTransactionItem item = new ReturnTransactionItem(product1, amount, price);

        assertEquals(product1.getBarCode(), item.getBarCode());
        assertEquals(amount, item.getAmount());
        assertEquals(price, item.getPricePerUnit(), 0.01);
    }

    @Test
    public void testComputeValue() {
        ReturnTransactionItem item = new ReturnTransactionItem(product1, amount, price);
        assertEquals(amount * price, item.computeValue(), 0.01);
    }

    @Test
    public void testEqualsHashCode() {
        ReturnTransactionItem obj = new ReturnTransactionItem(product1, amount, price);
        ReturnTransactionItem same = new ReturnTransactionItem(product1, amount, price);

        ReturnTransactionItem different1 = new ReturnTransactionItem(product1, amount, price / 2.0);
        ReturnTransactionItem different2 = new ReturnTransactionItem(product1, amount + 1, price);
        ReturnTransactionItem different3 = new ReturnTransactionItem(product2, amount , price);

        assertNotEquals(obj, null);
        assertNotEquals(obj, "boost coverage");

        assertEquals(obj, obj);

        assertEquals(obj, same);
        assertNotEquals(obj, different1);
        assertNotEquals(obj, different2);
        assertNotEquals(obj, different3);

        assertEquals(obj.hashCode(), same.hashCode());
        assertNotEquals(obj.hashCode(), different1.hashCode());
    }
}

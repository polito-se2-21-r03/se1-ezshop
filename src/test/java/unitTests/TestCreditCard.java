package unitTests;

import it.polito.ezshop.credit_card_circuit.CreditCard;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test CreditCard
 */
public class TestCreditCard {

    private static final String code = "1111222233334444";
    private static final double balance = 10.0;

    @Test
    public void testConstructor() {
        CreditCard card = new CreditCard(code, balance);

        assertEquals(code, card.getCode());
        assertEquals(balance, card.getBalance(), 0.01);
    }

    @Test
    public void testCheckAvailability() {
        CreditCard card = new CreditCard(code, balance);

        assertTrue(card.checkAvailability(balance / 2.0));
        assertTrue(card.checkAvailability(balance));
        assertFalse(card.checkAvailability(balance + 0.01));
    }

    @Test
    public void testUpdateBalance() {
        CreditCard card = new CreditCard(code, balance);

        // add 5.0
        assertTrue(card.updateBalance(5.0));
        assertEquals(15.0, card.getBalance(), 0.01);

        // remove 10.0
        assertTrue(card.updateBalance(-10.0));
        assertEquals(5.0, card.getBalance(), 0.01);

        // try to remove 10.0 again
        assertFalse(card.updateBalance(-10.0));
        assertEquals(5.0, card.getBalance(), 0.01);
    }

    @Test
    public void testToString() {
        CreditCard card = new CreditCard(code, balance);
        assertEquals("1111222233334444;10.00", card.toString());
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        CreditCard obj = new CreditCard("4485370086510891", 150.0);
        CreditCard same = new CreditCard("4485370086510891", 150.0);
        CreditCard different = new CreditCard("4716258050958645", 0.0);

        assertEquals(obj, same);
        assertNotEquals(obj, different);

        assertEquals(obj.hashCode(), same.hashCode());
        assertNotEquals(obj.hashCode(), different.hashCode());
    }

}

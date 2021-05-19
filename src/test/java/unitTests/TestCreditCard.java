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
    public void testConstructor () {
        CreditCard card = new CreditCard(code, balance);

        assertEquals(code, card.getCode());
        assertEquals(balance, card.getBalance(), 0.01);
    }

    @Test
    public void testSetters() {
        CreditCard card = new CreditCard(code, balance);

        card.setCode("5555666677778884");
        assertEquals("5555666677778884", card.getCode());

        card.setBalance(10.0);
        assertEquals(10.0, card.getBalance(), 0.01);
    }

    @Test
    public void testCheckAvailability () {
        CreditCard card = new CreditCard(code, balance);

        assertTrue(card.checkAvailability(balance / 2.0));
        assertTrue(card.checkAvailability(balance));
        assertFalse(card.checkAvailability(balance + 0.01));
    }

    @Test
    public void testUpdateBalance () {
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
    public void testToString () {
        CreditCard card = new CreditCard(code, balance);
        assertEquals("1111222233334444;10.00", card.toString());
    }

}

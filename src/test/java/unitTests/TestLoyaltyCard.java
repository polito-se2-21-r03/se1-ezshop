package unitTests;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.model.LoyaltyCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestLoyaltyCard {

    private static final String code = "1234567890";
    private static final int points = 10;

    @Test
    public void testConstructor() throws InvalidCustomerCardException {
        for (String code : TestHelpers.invalidCustomerCards) {
            assertThrows(InvalidCustomerCardException.class, () -> new LoyaltyCard(code, points));
        }

        // test invalid number of points
        assertThrows(IllegalArgumentException.class, () -> new LoyaltyCard(code, -1));

        // test zero points
        LoyaltyCard card = new LoyaltyCard(code, 0);

        assertEquals(code, card.getCode());
        assertEquals(points, card.getPoints());

        // test positive number of points
        card = new LoyaltyCard(code, points);

        assertEquals(code, card.getCode());
        assertEquals(points, card.getPoints());
    }

    @Test
    public void testSetPoints() throws Exception {
        LoyaltyCard card = new LoyaltyCard(code, points);

        card.setPoints(12);
        assertEquals(12, card.getPoints());
    }

    @Test
    public void testUpdatePoints() throws Exception {
        LoyaltyCard card = new LoyaltyCard(code, points);

        assertTrue(card.updatePoints(1));
        assertEquals(11, card.getPoints());

        assertTrue(card.updatePoints(0));
        assertEquals(11, card.getPoints());

        assertTrue(card.updatePoints(-1));
        assertEquals(10, card.getPoints());

        assertTrue(card.updatePoints(-10));
        assertEquals(0, card.getPoints());

        assertFalse(card.updatePoints(-10));
        assertEquals(0, card.getPoints());
    }

    @Test
    public void testValidateCode() throws Exception {
        for (String code : TestHelpers.invalidCustomerCards) {
            assertThrows(InvalidCustomerCardException.class, () -> LoyaltyCard.validateCode(code));
        }

        LoyaltyCard.validateCode("1234565764");
    }

    @Test
    public void testGenerateNewCode() throws Exception {
        LoyaltyCard.validateCode(LoyaltyCard.generateNewCode());
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        LoyaltyCard card = new LoyaltyCard(code, points);
        LoyaltyCard cardSame = new LoyaltyCard(code, points);
        LoyaltyCard cardDifferent = new LoyaltyCard(code, points + 1);

        assertEquals(card, cardSame);
        assertNotEquals(card, cardDifferent);

        assertEquals(card.hashCode(), cardSame.hashCode());
        assertNotEquals(card.hashCode(), cardDifferent.hashCode());
    }
}

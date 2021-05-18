package unitTests;

import it.polito.ezshop.model.LoyaltyCard;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestLoyaltyCard {

    private static final String code = "1234567890";
    private static final int points = 10;

    @Test
    public void testConstructor() {
        LoyaltyCard card = new LoyaltyCard(code, points);

        assertEquals(code, card.getCode());
        assertEquals(points, card.getPoints());
    }

    @Test
    public void testSetPoints() {
        LoyaltyCard card = new LoyaltyCard(code, points);

        card.setPoints(12);
        assertEquals(12, card.getPoints());
    }

    @Test
    public void testEqualsHashCode() {
        LoyaltyCard card = new LoyaltyCard(code, points);
        LoyaltyCard cardSame = new LoyaltyCard(code, points);
        LoyaltyCard cardDifferent = new LoyaltyCard(code, points + 1);

        assertEquals(card, cardSame);
        assertNotEquals(card, cardDifferent);

        assertEquals(card.hashCode(), cardSame.hashCode());
        assertNotEquals(card.hashCode(), cardDifferent.hashCode());
    }
}

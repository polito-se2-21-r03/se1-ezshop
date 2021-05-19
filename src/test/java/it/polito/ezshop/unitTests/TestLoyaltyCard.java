package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
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
            assertThrows(InvalidCustomerCardException.class, () -> new LoyaltyCard(code));
        }

        // test correct initialization
        LoyaltyCard card = new LoyaltyCard(code);
        assertEquals(code, card.getCode());
        assertEquals(0, card.getPoints());
    }

    @Test
    public void testSetPoints() throws Exception {
        LoyaltyCard card = new LoyaltyCard(code);

        card.setPoints(12);
        assertEquals(12, card.getPoints());
        card.setPoints(0);
        assertEquals(0, card.getPoints());

        assertThrows(IllegalArgumentException.class, () -> card.setPoints(-5));
    }

    @Test
    public void testIsValidCode() throws Exception {
        for (String code : TestHelpers.invalidCustomerCards) {
            assertFalse(LoyaltyCard.isValidCode(code));
        }

        assertTrue(LoyaltyCard.isValidCode("1234565764"));
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
        LoyaltyCard card = new LoyaltyCard(code);
        LoyaltyCard cardSame = new LoyaltyCard(code);
        LoyaltyCard cardDifferent = new LoyaltyCard(code);

        card.setPoints(points);
        cardSame.setPoints(points);
        cardDifferent.setPoints(points+1);

        assertEquals(card, cardSame);
        assertNotEquals(card, cardDifferent);

        assertEquals(card.hashCode(), cardSame.hashCode());
        assertNotEquals(card.hashCode(), cardDifferent.hashCode());
    }
}

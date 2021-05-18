package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;

import java.util.Objects;
import java.util.Random;

public class LoyaltyCard {
    private final String code;
    private int points;

    public LoyaltyCard(String code, int points) throws InvalidCustomerCardException {
        validateCode(code);

        if (points < 0) {
            throw new IllegalArgumentException("Points must be non negative");
        }

        this.code = code;
        this.points = points;
    }

    /**
     * Generate a loyalty card code.
     *
     * @return a 10 characters long numeric code
     */
    public static String generateNewCode() {
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char) ('0' + ((char) rnd.nextInt('9' - '0')));
            sb.append(c);
        }
        return sb.toString();
    }

    public static void validateCode(String code) throws InvalidCustomerCardException {
        if (code == null || code.equals("")) throw new InvalidCustomerCardException();
        if (code.length() != 10) throw new InvalidCustomerCardException();

        for (int i = 0; i < code.length(); i++) {
            if (!Character.isDigit(code.charAt(i))) {
                throw new InvalidCustomerCardException();
            }
        }
    }

    public String getCode() {
        return code;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean updatePoints (int delta) {
        if (delta < 0 && (this.points + delta) < 0) {
            // not enough points on the card
            return false;
        }

        this.points += delta;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoyaltyCard that = (LoyaltyCard) o;
        return points == that.points && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, points);
    }
}

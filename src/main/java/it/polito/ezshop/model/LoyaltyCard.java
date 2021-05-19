package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;

import java.util.Objects;
import java.util.Random;

public class LoyaltyCard {
    public static final Random rnd = new Random();
    private final String code;
    private int points;

    public LoyaltyCard(String customerCard) throws InvalidCustomerCardException {
        validateCode(customerCard);
        this.code = customerCard;
        this.points = 0;
    }

    @Deprecated
    public LoyaltyCard(String customerCard, int points) {
        this.code = customerCard;
        this.points = points;
    }

    public static String generateNewCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char) ('0' + ((char) rnd.nextInt('9' - '0')));
            sb.append(c);
        }
        return sb.toString();
    }

    public static void validateCode(String code) throws InvalidCustomerCardException {
        if (!isValidCode(code)) {
            throw new InvalidCustomerCardException("The customer card must be a string of 10 digits.");
        }
    }

    public static boolean isValidCode(String code) {

        if (code == null || code.length() != 10) {
            return false;
        }

        for (int i=0; i<code.length(); i++) {
            if (!Character.isDigit(code.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public String getCode() {
        return code;
    }

    public void setPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Loyalty cards do not allow for negative points.");
        }
        this.points = points;
    }

    public int getPoints() {
        return points;
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

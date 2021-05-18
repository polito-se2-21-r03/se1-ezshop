package it.polito.ezshop.model;

import java.util.Objects;
import java.util.Random;

public class LoyaltyCard {
    public static final Random rnd = new Random();
    private final String code;
    private int points;

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

    public String getCode() {
        return code;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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

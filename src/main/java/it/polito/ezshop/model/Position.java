package it.polito.ezshop.model;

import java.util.Objects;

public class Position {

    private int aisleID;
    private String rackID;
    private int levelID;

    public int getAisleID() {
        return aisleID;
    }

    public String getRackID() {
        return rackID;
    }

    public int getLevelID() {
        return levelID;
    }

    @Override
    public String toString() {
        return aisleID + "-" + rackID + "-" + levelID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return aisleID == position.aisleID && levelID == position.levelID && rackID.equals(position.rackID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aisleID, rackID, levelID);
    }

    public Position(Position position) {
        Objects.requireNonNull(position);

        this.aisleID = position.getAisleID();
        this.rackID = position.getRackID();
        this.levelID = position.getLevelID();
    }

    public Position(String position) throws IllegalArgumentException {
        Objects.requireNonNull(position);

        // split string into parts describing each attribute
        String[] positionArray = position.split("-");

        // throw exception if invalid format is used
        if (positionArray.length != 3) {
            throw new IllegalArgumentException("Position must be of form <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>");
        }

        // try parsing position, throw exception if numerical IDs can't be parsed
        try {
            this.aisleID = Integer.parseInt(positionArray[0]);
            this.rackID = positionArray[1];
            this.levelID = Integer.parseInt(positionArray[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("aisleNumber and levelNumber must be Integers");
        }
    }
}

package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidLocationException;

import java.util.Objects;

public class Position {

    private final int aisleID;
    private final String rackID;
    private final int levelID;

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

    /**
     * Copy constructor
     *
     * @param position position to be copied
     */
    public Position(Position position) {
        Objects.requireNonNull(position);

        this.aisleID = position.getAisleID();
        this.rackID = position.getRackID();
        this.levelID = position.getLevelID();
    }

    /**
     * Builds position object from string encoded version following format <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
     *
     * @param position String encoding of position, aisleNumber and levelNumber must be integers
     * @throws IllegalArgumentException If the required format is violated
     */
    public Position(String position) throws InvalidLocationException {
        Objects.requireNonNull(position);

        // split string into parts describing each attribute
        String[] positionArray = position.split("-");

        // throw exception if invalid format is used
        if (positionArray.length != 3) {
            throw new InvalidLocationException("Position must be of form <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>");
        }

        // try parsing position, throw exception if numerical IDs can't be parsed
        try {
            this.aisleID = Integer.parseInt(positionArray[0]);
            this.rackID = positionArray[1];
            this.levelID = Integer.parseInt(positionArray[2]);
        } catch (NumberFormatException e) {
            throw new InvalidLocationException("aisleNumber and levelNumber must be Integers");
        }

        if (rackID.length() == 0) {
            throw new InvalidLocationException("rackAlphabeticIdentifier may not be empty string");
        }
    }
}

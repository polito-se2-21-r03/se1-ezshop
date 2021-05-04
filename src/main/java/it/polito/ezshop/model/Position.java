package it.polito.ezshop.model;

import java.util.Objects;

public class Position {

    private int aisleID;
    private String rackID;
    private int levelID;

    public int getAisleID() { return aisleID; }

    public void setAisleID(int aisleID) { this.aisleID = aisleID; }

    public String getRackID() { return rackID; }

    public void setRackID(String rackID) { this.rackID = rackID; }

    public int getLevelID() { return levelID; }

    public void setLevelID(int levelID) { this.levelID = levelID; }

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

    public static Position parsePosition(String position) {

        int aisleID;
        String rackID;
        int levelID;

        String[] positionArray = position.split("-");

        if (positionArray.length != 3) {
            return null;
        }

        try {
            aisleID = Integer.parseInt(positionArray[0]);
            rackID = positionArray[1];
            levelID = Integer.parseInt(positionArray[2]);
        } catch (NumberFormatException e) {
            return null;
        }

        return new Position(aisleID, rackID, levelID);
    }

    public Position(int aisleID, String rackID, int levelID) {
        this.aisleID = aisleID;
        this.rackID = rackID;
        this.levelID = levelID;
    }
}

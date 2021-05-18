package unitTests;

import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.model.Position;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPosition {

    private static final int aisleID = 1;
    private static final String rackID = "2";
    private static final int levelID = 3;
    private static final String positionString = aisleID + "-" + rackID + "-" + levelID;

    private static final String[] illegalPositions = new String[] {"", "1-2", "1-2-", "1--2", "-1-2", "1-2-3-4"};

    @Test
    public void testConstructors() throws Exception {
        Position position = new Position(positionString);
        assertEquals(aisleID, position.getAisleID());
        assertEquals(rackID, position.getRackID());
        assertEquals(levelID, position.getLevelID());

        Position positionCopy = new Position(position);
        assertEquals(aisleID, positionCopy.getAisleID());
        assertEquals(rackID, positionCopy.getRackID());
        assertEquals(levelID, positionCopy.getLevelID());
    }

    @Test
    public void testConstructorInvalidFormat() {
        for (String positionString:illegalPositions) {
            assertThrows(InvalidLocationException.class, () -> new Position(positionString));
        }
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        Position position = new Position(positionString);
        Position equalPosition = new Position(positionString);
        Position differentPosition = new Position("1-2-4");

        assertEquals(position, equalPosition);
        assertEquals(position.hashCode(), equalPosition.hashCode());

        assertNotEquals(position, differentPosition);
        assertNotEquals(position.hashCode(), differentPosition.hashCode());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(positionString, new Position(positionString).toString());
    }
}

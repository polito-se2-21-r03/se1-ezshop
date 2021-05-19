package unitTests;

import it.polito.ezshop.utils.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestUtils {

    /**
     * If the list parameter is null, the method should
     * return a valid positive integer.
     */
    @Test
    public void testNullIDsList() {
        Integer id = Utils.generateId(null);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    /**
     * If the list parameter is an empty list, the method should
     * return a valid positive integer.
     */
    @Test
    public void testEmptyList() {
        Integer id = Utils.generateId(new ArrayList<>());
        assertNotNull(id);
        assertTrue(id > 0);
    }

    /**
     * If the list parameter is a non empty list, the method should
     * return a valid positive integer. The value should not be
     * already present in the list.
     */
    @Test
    public void test() {
        List<Integer> IDs = new ArrayList<>();

        Integer id = Utils.generateId(IDs);
        assertNotNull(id);
        assertTrue(id > 0);

        IDs.add(id);
        id = Utils.generateId(IDs);
        assertNotNull(id);
        assertTrue(id > 0);
        assertFalse(IDs.contains(id));
    }

}

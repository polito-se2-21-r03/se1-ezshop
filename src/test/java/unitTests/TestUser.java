package unitTests;

import static org.junit.Assert.*;

import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Test;

public class TestUser {

    final Integer id = 1;
    final String username = "username";
    final String password = "password";
    final Role role = Role.ADMINISTRATOR;

    @Test
    public void testConstructor () {
        User user = new User(id, username, password, role);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());

        user = new User(id, username, password, role);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    public void testSetRole() {
        User user = new User(id, username, password, role);

        user.setRole(role);
        assertEquals(role, user.getRole());
    }

    @Test
    public void testEqualsHashCode() {
        User user = new User(id, username, password, role);
        User userSame = new User(id, username, password, role);
        User userDifferent = new User(id, username, password, Role.CASHIER);

        assertEquals(user, userSame);
        assertNotEquals(user, userDifferent);

        assertEquals(user.hashCode(), userSame.hashCode());
        assertNotEquals(user.hashCode(), userDifferent.hashCode());
    }
}

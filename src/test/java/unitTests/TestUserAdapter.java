package unitTests;

import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import it.polito.ezshop.model.adapters.UserAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestUserAdapter {

    final Integer id = 1;
    final String username = "username";
    final String password = "password";
    final Role role = Role.ADMINISTRATOR;

    final User user = new User(id, username, password, role);
    final UserAdapter userAdapter = new UserAdapter(user);

    @Test
    public void testSetters() {
        assertThrows(UnsupportedOperationException.class, () -> userAdapter.setId(id));
        assertThrows(UnsupportedOperationException.class, () -> userAdapter.setUsername(username));
        assertThrows(UnsupportedOperationException.class, () -> userAdapter.setPassword(password));
        assertThrows(UnsupportedOperationException.class, () -> userAdapter.setRole(role.getValue()));
    }

    @Test
    public void testGetters() {
        assertEquals(user.getId(), userAdapter.getId());
        assertEquals(user.getUsername(), userAdapter.getUsername());
        assertEquals(user.getPassword(), userAdapter.getPassword());
        assertEquals(user.getRole(), userAdapter.getRole());
    }

}

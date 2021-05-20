package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import it.polito.ezshop.model.adapters.UserAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestUserAdapter {

    static final Integer id = 1;
    static final String username = "username";
    static final String password = "password";
    static final Role role = Role.ADMINISTRATOR;

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new UserAdapter(null));
    }

    @Test
    public void testSetters() throws Exception {
        User user = new User(id, username, password, role);
        UserAdapter userAdapter = new UserAdapter(user);

        // test get/set id
        assertThrows(UnsupportedOperationException.class, () -> userAdapter.setId(id));
        assertEquals(user.getId(), userAdapter.getId());

        // test get/set username
        for (String username : TestHelpers.invalidUserUsernames) {
            assertThrows(IllegalArgumentException.class, () -> userAdapter.setUsername(username));
        }
        userAdapter.setUsername("Alex");
        assertEquals(user.getUsername(), userAdapter.getUsername());

        // test get/set password
        for (String password : TestHelpers.invalidUserPassword) {
            assertThrows(IllegalArgumentException.class, () -> userAdapter.setPassword(password));
        }
        userAdapter.setPassword("1234");
        assertEquals("1234", userAdapter.getPassword());

        // test get/set role
        for (String role : TestHelpers.invalidUserRoles) {
            assertThrows(IllegalArgumentException.class, () -> userAdapter.setRole(role));
        }
        userAdapter.setRole(Role.CASHIER.getValue());
        assertEquals(Role.CASHIER.getValue(), userAdapter.getRole());
    }
}

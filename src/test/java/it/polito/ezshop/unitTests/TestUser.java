package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestUser {

    final Integer id = 1;
    final String username = "username";
    final String password = "password";
    final Role role = Role.ADMINISTRATOR;

    @Test
    public void testConstructor() throws Exception {
        for (Integer id : TestHelpers.invalidUserIDs) {
            assertThrows(InvalidUserIdException.class, () -> new User(id, username, password, role));
        }

        for (String username : TestHelpers.invalidUserUsernames) {
            assertThrows(InvalidUsernameException.class, () -> new User(id, username, password, role));
        }

        for (String password : TestHelpers.invalidUserPassword) {
            assertThrows(InvalidPasswordException.class, () -> new User(id, username, password, role));
        }

        for (String role : TestHelpers.invalidUserRoles) {
            assertThrows(InvalidRoleException.class, () -> new User(id, username, password, role));
        }

        User user = new User(id, username, password, role);
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());

        user = new User(id, username, password, "Administrator");
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    public void testSetUsername() throws Exception {
        User user = new User(id, username, password, role);

        for (String username : TestHelpers.invalidUserUsernames) {
            assertThrows(InvalidUsernameException.class, () -> user.setUsername(username));
        }

        user.setUsername("Alex");
        assertEquals("Alex", user.getUsername());
    }

    @Test
    public void testSetPassword() throws Exception {
        User user = new User(id, username, password, role);

        for (String password : TestHelpers.invalidUserPassword) {
            assertThrows(InvalidPasswordException.class, () -> user.setPassword(password));
        }

        user.setPassword("1234");
        assertEquals("1234", user.getPassword());
    }

    @Test
    public void testSetRole() throws Exception {
        User user = new User(id, username, password, role);

        assertThrows(InvalidRoleException.class, () -> user.setRole(null));

        user.setRole(Role.CASHIER);
        assertEquals(Role.CASHIER, user.getRole());
    }

    @Test
    public void testValidateId() throws InvalidUserIdException {
        for (Integer id : TestHelpers.invalidUserIDs) {
            assertThrows(InvalidUserIdException.class, () -> User.validateId(id));
        }

        User.validateId(1);
    }

    @Test
    public void testValidateUsername() throws InvalidUsernameException {
        for (String username : TestHelpers.invalidUserUsernames) {
            assertThrows(InvalidUsernameException.class, () -> User.validateUsername(username));
        }

        User.validateUsername("Simone");
    }

    @Test
    public void testValidatePassword() throws InvalidPasswordException {
        for (String password : TestHelpers.invalidUserPassword) {
            assertThrows(InvalidPasswordException.class, () -> User.validatePassword(password));
        }

        User.validatePassword("1234");
    }

    @Test
    public void testValidateRole() throws InvalidRoleException {
        for (String role : TestHelpers.invalidUserRoles) {
            assertThrows(InvalidRoleException.class, () -> User.validateRole(role));
        }

        User.validateRole("Administrator");
        User.validateRole(Role.ADMINISTRATOR);
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        User user = new User(id, username, password, role);
        User userSame = new User(id, username, password, role);
        User userDifferent = new User(id, username, password, Role.CASHIER);

        assertEquals(user, userSame);
        assertNotEquals(user, userDifferent);

        assertEquals(user.hashCode(), userSame.hashCode());
        assertNotEquals(user.hashCode(), userDifferent.hashCode());
    }
}

package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUserIdException;
import it.polito.ezshop.exceptions.InvalidUsernameException;

import java.util.Objects;

public class User {

    private Integer id;
    private String username;
    private String password;
    private Role role;

    public User(Integer id, String username, String password, Role role) throws InvalidUserIdException,
            InvalidUsernameException, InvalidRoleException, InvalidPasswordException {
        this.setId(id);
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
    }

    public User(Integer id, String username, String password, String role) throws InvalidUserIdException,
            InvalidUsernameException, InvalidRoleException, InvalidPasswordException {
        this(id, username, password, Role.fromString(role));
    }

    public static void validateId(Integer id) throws InvalidUserIdException {
        if (id == null || id <= 0) {
            throw new InvalidUserIdException();
        }
    }

    public static void validateUsername(String username) throws InvalidUsernameException {
        if (username == null || username.equals("")) {
            throw new InvalidUsernameException("Username must not be null or empty");
        }
    }

    public static void validatePassword(String password) throws InvalidPasswordException {
        if (password == null || password.equals("")) {
            throw new InvalidPasswordException("Password must not be null or empty");
        }
    }

    public static void validateRole(Role role) throws InvalidRoleException {
        if (role == null) {
            throw new InvalidRoleException("Invalid role");
        }
    }

    public static void validateRole(String role) throws InvalidRoleException {
        validateRole(Role.fromString(role));
    }

    public Integer getId() {
        return this.id;
    }

    private void setId(Integer id) throws InvalidUserIdException {
        validateId(id);
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) throws InvalidUsernameException {
        validateUsername(username);
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) throws InvalidPasswordException {
        validatePassword(password);
        this.password = password;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) throws InvalidRoleException {
        validateRole(role);
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }
}

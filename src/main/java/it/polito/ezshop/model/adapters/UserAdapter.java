package it.polito.ezshop.model.adapters;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;

import java.util.Objects;

public class UserAdapter implements it.polito.ezshop.data.User {

    private final User user;

    public UserAdapter(User user) {
        Objects.requireNonNull(user);
        this.user = user;
    }

    @Override
    public Integer getId() {
        return user.getId();
    }

    @Override
    public void setId(Integer id) {
        throw new UnsupportedOperationException("Changing the user ID is forbidden.");
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        try {
            user.setUsername(username);
        } catch (InvalidUsernameException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public void setPassword(String password) {
        try {
            user.setPassword(password);
        } catch (InvalidPasswordException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getRole() {
        return user.getRole().getValue();
    }

    @Override
    public void setRole(String role) {
        try {
            user.setRole(Role.fromString(role));
        } catch (InvalidRoleException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.User;

public class UserAdapter implements it.polito.ezshop.data.User {

    private final User user;

    public UserAdapter(User user) {
        this.user = user;
    }

    @Override
    public Integer getId() {
        return user.getId();
    }

    @Override
    public void setId(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public void setPassword(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRole() {
        return user.getRole().getValue();
    }

    @Override
    public void setRole(String role) {
        throw new UnsupportedOperationException();
    }
}

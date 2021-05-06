package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;

public abstract class EZShopTestBase {

    protected final User cashier = new User(1, "cashier", "cashier", Role.CASHIER);
    protected final User shopManager = new User(2, "shopManager", "shopManager", Role.SHOP_MANAGER);
    protected final User admin = new User(3, "administrator", "administrator", Role.ADMINISTRATOR);
    protected final EZShop shop = new EZShop();

    /**
     * Create three users, one for each possible role.
     */
    protected void initializeUsers() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        shop.createUser(cashier.getUsername(), cashier.getPassword(), cashier.getRole());
        shop.createUser(shopManager.getUsername(), shopManager.getPassword(), shopManager.getRole());
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
    }

    /**
     * Shortcut for the EZShop.login method
     */
    protected void loginAs(User user) throws InvalidPasswordException, InvalidUsernameException {
        shop.login(user.getUsername(), user.getPassword());
    }

    /**
     * Generate a new valid product type.
     * TODO: generate a product randomly
     *
     * @return a new product type
     */
    protected ProductType generateValidProductType() {
        String description = "desc";
        String barcode = "12345678901231";
        double price = 10.0;
        String note = "note";

        return new it.polito.ezshop.model.ProductType(note, description, barcode, price, 1);
    }

}

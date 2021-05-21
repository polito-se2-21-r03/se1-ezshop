package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestUpdateQuantity {
    private static final EZShop shop = new EZShop();
    private static  User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProductType target;
    @Before
    public void beforeEach() throws
            InvalidProductCodeException, InvalidProductDescriptionException, InvalidQuantityException, InvalidPricePerUnitException,
            InvalidProductIdException, InvalidOrderIdException, UnauthorizedException, InvalidUsernameException, InvalidPasswordException,
            InvalidLocationException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a new product that will be updated later in the tests
        String barcode = "12345678901231";
        shop.createProductType("desc", "12345678901231", 10.0, "note");
        target = shop.getProductTypeByBarCode(barcode);
        // set initial position
        shop.updatePosition(target.getId(), "1-2-3");
        // set initial quantity
        target.setQuantity(100);

    }
    /**
     * Tests that access rights are handled correctly by updateQuantity.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("updateQuantity", Integer.class, int.class);
        Object[] params = {target.getId(), 100};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }
    /**
     * If the id is null|negative|zero, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidId() {
        // boundary values for the id parameter
        Arrays.asList(null, -1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductIdException.class, () -> {
                // try to update a product with the boundary value
                shop.updateQuantity(value, 100);
            });
        });
    }
    /**
     * Test valid updateQuantity
     */
    @Test()
    public void testValidUpdateQuantity() throws InvalidProductIdException, UnauthorizedException, InvalidProductCodeException {
        ProductType updatedProduct;

        Integer newQuantity = 200;
        Integer oldQuantity = target.getQuantity();
        // update the quantity of the product
        assertTrue(shop.updateQuantity(target.getId(), newQuantity));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(target.getBarCode());
        assertNotNull(updatedProduct);
        Integer totalQuantity = newQuantity+oldQuantity;
        assertEquals(totalQuantity,
                target.getQuantity());

    }

    /**
     * If no products with given product id exists, the method should return false.
     */
    @Test()
    public void testNonExistingId() throws InvalidProductIdException, UnauthorizedException {
        assertFalse(shop.updateQuantity(target.getId() + 1, target.getQuantity())
        );
    }
}

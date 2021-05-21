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

public class EZShopTestUpdatePosition {
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
    private ProductType target2;

    @Before
    public void beforeEach() throws
            InvalidProductCodeException, InvalidProductDescriptionException, InvalidQuantityException, InvalidPricePerUnitException,
            InvalidProductIdException, InvalidOrderIdException, UnauthorizedException,
            InvalidUsernameException, InvalidPasswordException, InvalidLocationException, InvalidRoleException {
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

        // insert another new product that will be updated later in the tests
        String barcode2 = "9788879924337";
        shop.createProductType("desc2", "9788879924337", 20.0, "note2");
        target2 = shop.getProductTypeByBarCode(barcode2);

    }
    /**
     * Tests that access rights are handled correctly by updatePosition.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("updatePosition", Integer.class, String.class);
        Object[] params = {target.getId(), "1-2-3"};
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
                shop.updatePosition(value, "1-2-3");
            });
        });
    }
    /**
     * If the position has not this format: <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>,
     * the method should throw InvalidLocationException
     */
    @Test()
    public void testLocationException() {
        // boundary values for the Position parameter
        Arrays.asList("-", "trial", "1-2-", "-1-1", "hello-1-2").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidLocationException.class, () -> {
                // try to update a product with the boundary value
                shop.updatePosition(target.getId(), value);
            });
        });
    }
    /**
     * Test valid updatePosition
     */
    @Test()
    public void testValidUpdatePosition() throws InvalidProductIdException, UnauthorizedException, InvalidProductCodeException, InvalidLocationException {
        ProductType updatedProduct;
        String newPosition = "1-2-3";
        // update the position of the product
        assertTrue(shop.updatePosition(target.getId(), newPosition));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(target.getBarCode());
        assertNotNull(updatedProduct);
        assertEquals(newPosition, updatedProduct.getLocation());
    }
    /**
     * Test null or empty updatePosition
     */
    @Test()
    public void testNullUpdatePosition() throws InvalidProductIdException, UnauthorizedException, InvalidProductCodeException, InvalidLocationException {
        ProductType updatedProduct;
        String newPosition = "1-2-3";
        // update the position of the product
        assertTrue(shop.updatePosition(target.getId(), newPosition));

        // unassign the position of the product
        assertTrue(shop.updatePosition(target.getId(), ""));
        updatedProduct = shop.getProductTypeByBarCode(target.getBarCode());
        assertNotNull(updatedProduct);
        assertNull(updatedProduct.getLocation());

        assertTrue(shop.updatePosition(target.getId(), newPosition));
        assertTrue(shop.updatePosition(target.getId(), null));
        assertNotNull(updatedProduct);
        assertNull(updatedProduct.getLocation());
    }

    /**
     * If the location is already assigned, the method should return false
     */
    @Test()
    public void testAlreadyAssignedLocation() throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        shop.updatePosition(target2.getId(), "1-2-3");
        assertFalse(shop.updatePosition(target.getId(), "1-2-3"));
    }
    /**
     * If no products with given product id exists, the method should return false.
     */
    @Test()
    public void testNonExistingId() throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        assertFalse(shop.updatePosition(target.getId() + 1, "1-2-3"));
    }
}
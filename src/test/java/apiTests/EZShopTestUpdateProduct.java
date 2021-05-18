package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static unitTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.updateProduct() method.
 */
public class EZShopTestUpdateProduct {

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    private ProductType target;

    @Before
    public void beforeEach() throws
            InvalidPricePerUnitException, InvalidProductDescriptionException, InvalidProductCodeException,
            UnauthorizedException, InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
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
    }

    /**
     * Tests that access rights are handled correctly by updateProduct.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("updateProduct",
                Integer.class, String.class, String.class, double.class, String.class);
        Object[] params = {target.getId(), "desc", "12345678901231", 10.0, "note"};
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
                shop.updateProduct(value, "desc", "12345678901231", 10.0, "note");
            });
        });
    }

    /**
     * If the description is null|empty, the method should throw InvalidProductDescriptionException
     */
    @Test()
    public void testInvalidDescription() {
        // boundary values for the description parameter
        Arrays.asList(null, "").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductDescriptionException.class, () -> {
                // try to update a product with the boundary value
                shop.updateProduct(target.getId(), value, "12345678901231", 10.0, "note");
            });
        });
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // boundary values for the barcode parameter
        Arrays.asList(null, "", "123456789B123A", "12345678901232").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.updateProduct(target.getId(), "desc", value, 10.0, "note");
            });
        });
    }

    /**
     * If the price is negative|zero, the method should throw InvalidPricePerUnitException
     */
    @Test()
    public void testInvalidPrice() {
        // boundary values for the price parameter
        Arrays.asList(-10.0, 0.0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidPricePerUnitException.class, () -> {
                // try to update a product with the boundary value
                shop.updateProduct(target.getId(), "desc", "12345678901231", value, "note");
            });
        });
    }

    /**
     * Test valid updates
     */
    @Test()
    public void testValidUpdates() throws InvalidProductIdException, InvalidPricePerUnitException,
            InvalidProductDescriptionException, InvalidProductCodeException, UnauthorizedException {
        ProductType updatedProduct;

        String newDescription = "new description";
        double newPrice = 20.0;
        String newNote = "new note";

        // update the properties of the product, except the barcode
        assertTrue(shop.updateProduct(target.getId(), newDescription, target.getBarCode(), newPrice, newNote));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(target.getBarCode());
        assertNotNull(updatedProduct);
        assertEquals(newDescription, updatedProduct.getProductDescription());
        assertEquals(newPrice, updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(newNote, updatedProduct.getNote());

        String newBarcode = "123456789012";

        // update the properties of the product, except the barcode
        assertTrue(shop.updateProduct(target.getId(), newDescription, newBarcode, newPrice, newNote));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(newBarcode);
        assertNotNull(updatedProduct);
        assertEquals(newBarcode, updatedProduct.getBarCode());
        assertEquals(newDescription, updatedProduct.getProductDescription());
        assertEquals(newPrice, updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(newNote, updatedProduct.getNote());
    }

    /**
     * If a product update generate a collision the method should return false
     */
    @Test()
    public void testBarcodesCollision() throws InvalidProductIdException, InvalidPricePerUnitException,
            InvalidProductDescriptionException, InvalidProductCodeException, UnauthorizedException {
        ProductType updatedProduct;

        String barcode = "123456789012";

        // insert a new product
        shop.createProductType("desc", "123456789012", 10.0, "note");

        String newDescription = "new description";
        double newPrice = 20.0;
        String newNote = "new note";

        // try to modify a product and generate a collision
        assertFalse(shop.updateProduct(target.getId(), newDescription, barcode, newPrice, newNote));

        // verify that the product was not updated
        updatedProduct = shop.getProductTypeByBarCode(target.getBarCode());
        assertNotNull(updatedProduct);
        assertEquals(target.getProductDescription(), updatedProduct.getProductDescription());
        assertEquals(target.getPricePerUnit(), updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(target.getNote(), updatedProduct.getNote());
    }

    /**
     * If no products with given product id exists, the method should return false.
     */
    @Test()
    public void testNonExistingId() throws InvalidProductIdException, InvalidPricePerUnitException,
            InvalidProductDescriptionException, InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.updateProduct(target.getId() + 1,
                target.getProductDescription(),
                target.getBarCode(),
                target.getPricePerUnit(),
                target.getNote()
        ));
    }

}

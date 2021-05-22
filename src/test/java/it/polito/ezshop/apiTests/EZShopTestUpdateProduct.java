package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.invalidPricesPerUnit;
import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.updateProduct() method.
 */
public class EZShopTestUpdateProduct {

    private static final String PRODUCT_CODE = "12345678901231";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final double PRODUCT_PRICE = 1.0;
    private static final String PRODUCT_NOTE = "note";
    private static final Integer PRODUCT_QTY = 0;
    private static final String PRODUCT_LOCATION = null;

    private static final String PRODUCT_CODE_NEW = "12345678901217";
    private static final String PRODUCT_DESCRIPTION_NEW = "new description";
    private static final double PRODUCT_PRICE_NEW = 10.0;
    private static final String PRODUCT_NOTE_NEW = "new note";

    private final EZShopInterface shop = new EZShop();
    private final User admin;

    /**
     * Id of the product to be updated
     */
    private Integer productId;

    public EZShopTestUpdateProduct() throws Exception {
        admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
    }

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a new product that will be updated later in the tests
        productId = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
    }

    /**
     * Tests that access rights are handled correctly by updateProduct.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("updateProduct",
                Integer.class, String.class, String.class, double.class, String.class);
        Object[] params = {productId, PRODUCT_DESCRIPTION_NEW, PRODUCT_CODE_NEW, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidProductIdException
     */
    @Test()
    public void testInvalidId() {
        // invalid values for the id parameter
        // for each invalid value check that the correct exception is thrown
        for (Integer value : TestHelpers.invalidProductIDs) {
            assertThrows(InvalidProductIdException.class, () -> {
                // try to update a product with the boundary value
                shop.updateProduct(value, PRODUCT_DESCRIPTION_NEW, PRODUCT_CODE_NEW, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW);
            });
        }
    }

    /**
     * If the description is null|empty, the method should throw InvalidProductDescriptionException
     */
    @Test()
    public void testInvalidDescription() throws Exception {
        // boundary values for the description parameter
        // for each boundary value check that the correct exception is thrown
        for (String value : TestHelpers.invalidProductDescriptions) {
            assertThrows(InvalidProductDescriptionException.class, () -> {
                // try to update a product with the invalid value
                shop.updateProduct(productId, value, PRODUCT_CODE_NEW, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW);
            });

            // verify the product has not been changed
            ProductType p = shop.getProductTypeByBarCode(PRODUCT_CODE);
            assertEquals(PRODUCT_DESCRIPTION, p.getProductDescription());
            assertEquals(PRODUCT_CODE, p.getBarCode());
            assertEquals(PRODUCT_PRICE, p.getPricePerUnit(), 0.01);
            assertEquals(PRODUCT_NOTE, p.getNote());
            assertEquals(PRODUCT_QTY, p.getQuantity());
            assertEquals(PRODUCT_LOCATION, p.getLocation());

            // verify that a product with the new (invalid) barcode does not exist
            assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_NEW));
        }
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() throws Exception {
        // invalid values for the barcode parameter
        // for each invalid value check that the correct exception is thrown
        for (String value : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.updateProduct(productId, PRODUCT_DESCRIPTION_NEW, value, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW);
            });

            // verify the product has not been changed
            ProductType p = shop.getProductTypeByBarCode(PRODUCT_CODE);
            assertEquals(PRODUCT_DESCRIPTION, p.getProductDescription());
            assertEquals(PRODUCT_CODE, p.getBarCode());
            assertEquals(PRODUCT_PRICE, p.getPricePerUnit(), 0.01);
            assertEquals(PRODUCT_NOTE, p.getNote());
            assertEquals(PRODUCT_QTY, p.getQuantity());
            assertEquals(PRODUCT_LOCATION, p.getLocation());

            // verify that a product with the new (invalid) barcode does not exist
            assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_NEW));
        }
    }

    /**
     * If the price is negative|zero, the method should throw InvalidPricePerUnitException
     */
    @Test()
    public void testInvalidPrice() throws Exception {
        // invalid values for the price parameter
        // for each invalid value check that the correct exception is thrown
        for (double value : invalidPricesPerUnit) {
            assertThrows(InvalidPricePerUnitException.class, () -> {
                // try to update a product with the invalid value
                shop.updateProduct(productId, PRODUCT_DESCRIPTION_NEW, PRODUCT_CODE_NEW, value, PRODUCT_NOTE_NEW);
            });

            // verify the product has not been changed
            ProductType p = shop.getProductTypeByBarCode(PRODUCT_CODE);
            assertEquals(PRODUCT_DESCRIPTION, p.getProductDescription());
            assertEquals(PRODUCT_CODE, p.getBarCode());
            assertEquals(PRODUCT_PRICE, p.getPricePerUnit(), 0.01);
            assertEquals(PRODUCT_NOTE, p.getNote());
            assertEquals(PRODUCT_QTY, p.getQuantity());
            assertEquals(PRODUCT_LOCATION, p.getLocation());

            // verify that a product with the new (invalid) barcode does not exist
            assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE_NEW));
        }
    }

    /**
     * Test valid updates
     */
    @Test()
    public void testValidUpdates() throws Exception {
        ProductType updatedProduct;

        // update the properties of the product, except the barcode
        assertTrue(shop.updateProduct(productId, PRODUCT_DESCRIPTION_NEW, PRODUCT_CODE, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(PRODUCT_CODE);
        assertNotNull(updatedProduct);
        assertEquals(PRODUCT_DESCRIPTION_NEW, updatedProduct.getProductDescription());
        assertEquals(PRODUCT_PRICE_NEW, updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(PRODUCT_NOTE_NEW, updatedProduct.getNote());

        // update the properties of the product, except the barcode
        assertTrue(shop.updateProduct(productId, PRODUCT_DESCRIPTION_NEW, PRODUCT_CODE_NEW, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW));

        // check the old product was removed
        assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE));

        // retrieve the updated product
        updatedProduct = shop.getProductTypeByBarCode(PRODUCT_CODE_NEW);
        assertNotNull(updatedProduct);
        assertEquals(PRODUCT_CODE_NEW, updatedProduct.getBarCode());
        assertEquals(PRODUCT_DESCRIPTION_NEW, updatedProduct.getProductDescription());
        assertEquals(PRODUCT_PRICE_NEW, updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(PRODUCT_NOTE_NEW, updatedProduct.getNote());
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
        Integer productId2 = shop.createProductType("desc", barcode, 10.0, "note");

        // try to modify the first product and generate a collision
        assertFalse(shop.updateProduct(productId, PRODUCT_DESCRIPTION_NEW, barcode, PRODUCT_PRICE_NEW, PRODUCT_NOTE_NEW));

        // verify that the product was not updated
        updatedProduct = shop.getProductTypeByBarCode(PRODUCT_CODE);
        assertNotNull(updatedProduct);
        assertEquals(PRODUCT_DESCRIPTION, updatedProduct.getProductDescription());
        assertEquals(PRODUCT_PRICE, updatedProduct.getPricePerUnit(), 0.01);
        assertEquals(PRODUCT_NOTE, updatedProduct.getNote());

        // verify that "123456789012" still corresponds to the previously inserted product
        assertEquals(productId2, shop.getProductTypeByBarCode(barcode).getId());
    }

    /**
     * If no products with given product id exists, the method should return false.
     */
    @Test()
    public void testNonExistingId() throws InvalidProductIdException, InvalidPricePerUnitException,
            InvalidProductDescriptionException, InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.updateProduct(productId + 1,
                PRODUCT_DESCRIPTION_NEW,
                PRODUCT_CODE_NEW,
                PRODUCT_PRICE_NEW,
                PRODUCT_NOTE_NEW
        ));
    }

}

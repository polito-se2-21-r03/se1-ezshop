package it.polito.ezshop.apiTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.createProductType() method.
 */
public class EZShopTestCreateProductType {

    private static final String PRODUCT_CODE = "12345678901231";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final double PRODUCT_PRICE = 1.0;
    private static final String PRODUCT_NOTE = "note";

    private final EZShopInterface shop = new EZShop();
    private final User admin;

    public EZShopTestCreateProductType() throws Exception {
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
    }

    /**
     * Tests that access rights are handled correctly by createProductType.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("createProductType", String.class, String.class,
                double.class, String.class);
        Object[] params = {PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the description is null|empty, the method should throw InvalidProductDescriptionException
     */
    @Test()
    public void testInvalidDescription() throws Exception {
        // invalid values for the description parameter
        // for each invalid description check that the correct exception is thrown
        for (String description : TestHelpers.invalidProductDescriptions) {
            assertThrows(InvalidProductDescriptionException.class, () -> {
                // try to update a product with the boundary value
                shop.createProductType(description, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
            });

            // verify no product has been added
            assertNull(shop.getProductTypeByBarCode(PRODUCT_CODE));
        }
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() throws Exception {
        // invalid values for the barcode parameter
        // for each invalid value check that the correct exception is thrown
        for (String code : TestHelpers.invalidProductCodes) {
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.createProductType(PRODUCT_DESCRIPTION, code, PRODUCT_PRICE, PRODUCT_NOTE);
            });

            // verify no product has been added
            assertEquals(0, shop.getAllProductTypes().size());
        }
    }

    /**
     * If the price is negative|zero, the method should throw InvalidPricePerUnitException
     */
    @Test()
    public void testInvalidPrice() throws Exception {
        // invalid values for the price parameter
        // for each invalid value check that the correct exception is thrown
        for (double value : TestHelpers.invalidPricesPerUnit) {
            assertThrows(InvalidPricePerUnitException.class, () -> {
                // try to update a product with the boundary value
                shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, value, PRODUCT_NOTE);
            });

            // verify no product has been added
            assertEquals(0, shop.getAllProductTypes().size());
        }
    }

    /**
     * Nominal case (authorized user, valid parameters)
     */
    @Test()
    public void testValid() throws UnauthorizedException,
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException {
        Integer id = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
        assertNotNull(id);
        assertTrue(id > 0);

        // verify the created product
        ProductType p = shop.getProductTypeByBarCode(PRODUCT_CODE);
        assertNotNull(p);
        assertEquals(id, p.getId());
        assertEquals(PRODUCT_DESCRIPTION, p.getProductDescription());
        assertEquals(PRODUCT_CODE, p.getBarCode());
        assertEquals(PRODUCT_PRICE, p.getPricePerUnit(), 0.001);
        assertEquals(PRODUCT_NOTE, p.getNote());
        assertNull(p.getLocation());
        assertEquals((Integer) 0, p.getQuantity());
    }

    /**
     * If a product with the same barcode already exists, the method should return -1
     */
    @Test()
    public void testDuplicatedBarcode() throws Exception {
        Integer id = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
        assertNotNull(id);
        assertTrue(id > 0);

        id = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, PRODUCT_NOTE);
        assertNotNull(id);
        assertEquals(new Integer(-1), id);
    }

    /**
     * If the note is null, an empty string should be saved.
     */
    @Test()
    public void testNullNote() throws Exception {
        Integer id = shop.createProductType(PRODUCT_DESCRIPTION, PRODUCT_CODE, PRODUCT_PRICE, null);
        assertNotNull(id);
        assertTrue(id > 0);

        ProductType p = shop.getProductTypeByBarCode(PRODUCT_CODE);
        assertNotNull(p);
        assertEquals(id, p.getId());
        assertEquals(PRODUCT_DESCRIPTION, p.getProductDescription());
        assertEquals(PRODUCT_CODE, p.getBarCode());
        assertEquals(PRODUCT_PRICE, p.getPricePerUnit(), 0.001);
        assertEquals("", p.getNote());
        assertNull(p.getLocation());
        assertEquals((Integer) 0, p.getQuantity());
    }

}

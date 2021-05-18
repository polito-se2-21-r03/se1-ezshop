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
import java.util.List;
import java.util.stream.Collectors;

import static unitTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.getProductTypesByDescription() method.
 */
public class EZShopTestGetProductTypesByDescription {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final String PRODUCT_DESCRIPTION_1 = "Dune is set in the distant future amidst a feudal " +
            "interstellar society in which various noble houses control planetary fiefs";

    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final String PRODUCT_DESCRIPTION_2 = "The Hitchhiker's Guide to the Galaxy is a comedy science " +
            "fiction franchise created by Douglas Adams.";

    private static final String PRODUCT_CODE_3 = "123456789012";
    private static final String PRODUCT_DESCRIPTION_3 = "Foundation is a science fiction novel by American " +
            "writer Isaac Asimov.";

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        // define a few products
        shop.createProductType(PRODUCT_DESCRIPTION_1, PRODUCT_CODE_1, 10.0, "note");
        shop.createProductType(PRODUCT_DESCRIPTION_2, PRODUCT_CODE_2, 42.0, "note");
        shop.createProductType(PRODUCT_DESCRIPTION_3, PRODUCT_CODE_3, 20.0, "note");
    }

    /**
     * Tests that access rights are handled correctly by getProductTypesByDescription.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("getProductTypesByDescription", String.class);
        Object[] params = {PRODUCT_CODE_1};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * Null should be considered as the empty string
     */
    @Test()
    public void testNullOrEmptyDescription() throws UnauthorizedException {
        for (String value : Arrays.asList(null, "")) {
            List<ProductType> products = shop.getProductTypesByDescription(value);
            assertNotNull(products);
            assertEquals(3, products.size());

            // check if the list contains the expected products
            List<String> barcodes = products.stream().map(ProductType::getBarCode).collect(Collectors.toList());
            assertTrue(barcodes.contains(PRODUCT_CODE_1));
            assertTrue(barcodes.contains(PRODUCT_CODE_2));
            assertTrue(barcodes.contains(PRODUCT_CODE_3));
        }
    }

    /**
     * Test valid values for description
     */
    @Test()
    public void testValidDescriptions() throws UnauthorizedException {
        List<ProductType> products = shop.getProductTypesByDescription("Asimov");
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(PRODUCT_CODE_3, products.get(0).getBarCode());

        products = shop.getProductTypesByDescription("Dune");
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals(PRODUCT_CODE_1, products.get(0).getBarCode());

        products = shop.getProductTypesByDescription("science");
        assertNotNull(products);
        assertEquals(2, products.size());
        // check if the list contains the expected products
        List<String> barcodes = products.stream().map(ProductType::getBarCode).collect(Collectors.toList());
        assertTrue(barcodes.contains(PRODUCT_CODE_2));
        assertTrue(barcodes.contains(PRODUCT_CODE_3));

        products = shop.getProductTypesByDescription("math");
        assertNotNull(products);
        assertEquals(0, products.size());
    }

}

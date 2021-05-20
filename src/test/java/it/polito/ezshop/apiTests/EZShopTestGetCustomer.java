package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;

public class EZShopTestGetCustomer {

    private static final EZShop shop = new EZShop();
    private static User admin;
    private static final String customerName = "Pietro";
    private static Integer customerID;
    private static String card;

    public EZShopTestGetCustomer() throws Exception {
        admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
    }

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws Exception {
        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());

        // add two customers and a card to the shop
        customerID = shop.defineCustomer(customerName);
        card = shop.createCard();

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("getCustomer", Integer.class);
        testAccessRights(defineCustomer, new Object[]{1},
                new Role[]{Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that  an InvalidCustomerIdException is thrown when ID is null, 0 or negative
     */
    @Test
    public void testInvalidCustomerIdException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCustomerIdException.class, invalidCustomerIDs, shop::getCustomer);
    }

    /**
     * Tests that null is returned if no customer with the given ID exists
     */
    @Test
    public void testNullIfCustomerNotExists() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any customer
        int nonExistentId = generateId(
                shop.getAllCustomers().stream()
                        .map(it.polito.ezshop.data.Customer::getId)
                        .collect(Collectors.toList()));

        // return false when customer does not exist
        assertNull(shop.getCustomer(nonExistentId));
    }

    /**
     * Tests that customer is returned correctly
     */
    @Test
    public void testCustomerReturnedSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(EZShopTestGetCustomer.customerID);

        // verify that customer data is correct
        assertEquals(EZShopTestGetCustomer.customerID, customer.getId());
        assertEquals(EZShopTestGetCustomer.customerName, customer.getCustomerName());
    }

    /**
     * Tests that attached card and points are returned correctly with customer
     */
    @Test
    public void testCardReturnedSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // attach card to customer and add some points
        int pointsOnCard = 100;
        assertTrue(shop.attachCardToCustomer(card, customerID));
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(EZShopTestGetCustomer.customerID);

        // verify that card data is correct
        assertEquals(card, customer.getCustomerCard());
        assertEquals(new Integer(pointsOnCard), customer.getPoints());
    }
}

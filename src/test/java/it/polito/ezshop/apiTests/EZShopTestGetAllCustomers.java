package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.testAccessRights;

public class EZShopTestGetAllCustomers {

    private static final EZShop shop = new EZShop();
    private final User admin;
    private static final String customer1Name = "Pietro";
    private static final String customer2Name = "Andrea";
    private static Integer customer1ID;
    private static Integer customer2ID;
    public EZShopTestGetAllCustomers() throws Exception {
        admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
    }

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            UnauthorizedException, InvalidCustomerNameException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("getAllCustomers");
        testAccessRights(defineCustomer, new Object[]{},
                new Role[]{Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that an empty list is returned when no customers are present in the shop
     */
    @Test
    public void testGetEmptyList() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // throw an InvalidCustomerIdException when ID is null
        assertEquals(0, shop.getAllCustomers().size());
    }

    /**
     * Tests that a list of customers is returned successfully
     */
    @Test
    public void testGetCustomersSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add two customers to the shop
        customer1ID = shop.defineCustomer(customer1Name);
        customer2ID = shop.defineCustomer(customer2Name);

        // verify that correct amount of customers have been returned
        List<it.polito.ezshop.data.Customer> customers = shop.getAllCustomers();
        assertEquals(2, customers.size());

        // get returned customers
        it.polito.ezshop.data.Customer returnedCustomer1 = customers.stream()
                .filter(c -> c.getId().equals(customer1ID))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Customer returnedCustomer2 =  customers.stream()
                .filter(c -> c.getId().equals(customer2ID))
                .findAny()
                .orElse(null);

        assertNotNull(returnedCustomer1);
        assertEquals(customer1ID, returnedCustomer1.getId());
        assertEquals(customer1Name, returnedCustomer1.getCustomerName());

        assertNotNull(returnedCustomer2);
        assertEquals(customer2ID, returnedCustomer2.getId());
        assertEquals(customer2Name, returnedCustomer2.getCustomerName());
    }

    /**
     * Tests that cards attached to customers are returned successfully
     */
    @Test
    public void testGetCustomersAndCardsSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add two customers to the shop
        customer1ID = shop.defineCustomer(customer1Name);
        customer2ID = shop.defineCustomer(customer2Name);

        // attach cards to customers
        String card1 = shop.createCard();
        String card2 = shop.createCard();
        assertTrue(shop.attachCardToCustomer(card1, customer1ID));
        assertTrue(shop.attachCardToCustomer(card2, customer2ID));

        // add points to cards
        int points1 = 50;
        int points2 = 100;
        assertTrue(shop.modifyPointsOnCard(card1, points1));
        assertTrue(shop.modifyPointsOnCard(card2, points2));

        // get customers
        List<it.polito.ezshop.data.Customer> customers = shop.getAllCustomers();

        // extract returned customers from list
        it.polito.ezshop.data.Customer returnedCustomer1 = customers.stream()
                .filter(c -> c.getId().equals(customer1ID))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Customer returnedCustomer2 =  customers.stream()
                .filter(c -> c.getId().equals(customer2ID))
                .findAny()
                .orElse(null);

        // verify that both customers were returned successfully
        assertNotNull(returnedCustomer1);
        assertNotNull(returnedCustomer2);

        // verify customer cards were returned correctly
        assertEquals(card1, returnedCustomer1.getCustomerCard());
        assertEquals(card2, returnedCustomer2.getCustomerCard());

        // verify that points were returned correctly
        assertEquals((Integer) points1, returnedCustomer1.getPoints());
        assertEquals((Integer) points2, returnedCustomer2.getPoints());
    }
}

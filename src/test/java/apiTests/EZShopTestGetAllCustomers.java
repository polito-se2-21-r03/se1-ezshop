package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

import static unitTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestGetAllCustomers {

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Customer customer1 = new Customer("Pietro", "1234567890", 0, 0);
    private static final Customer customer2 = new Customer("Maria", "2345678901", 0, 0);

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
        testAccessRights(defineCustomer, new Object[] {},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
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
    public void testGetCustomersSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerNameException, UnauthorizedException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add two customers to the shop
        customer1.setId(shop.defineCustomer(customer1.getCustomerName()));
        customer2.setId(shop.defineCustomer(customer2.getCustomerName()));

        // verify that customers are returned correctly
        List<it.polito.ezshop.data.Customer> customers = shop.getAllCustomers();
        assertEquals(2, customers.size());
        assertTrue(customers.contains(customer1));
        assertTrue(customers.contains(customer2));
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
        customer1.setId(shop.defineCustomer(customer1.getCustomerName()));
        customer2.setId(shop.defineCustomer(customer2.getCustomerName()));

        // attach cards to customers
        String card1 = shop.createCard();
        String card2 = shop.createCard();
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));
        assertTrue(shop.attachCardToCustomer(card2, customer2.getId()));

        // add points to cards
        int points1 = 50;
        int points2 = 100;
        assertTrue(shop.modifyPointsOnCard(card1, points1));
        assertTrue(shop.modifyPointsOnCard(card2, points2));

        // get customers
        List<it.polito.ezshop.data.Customer> customers = shop.getAllCustomers();

        // extract returned customers from list
        it.polito.ezshop.data.Customer returnedCustomer1 = customers.stream()
                .filter(c -> c.getId().equals(customer1.getId()))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Customer returnedCustomer2 =  customers.stream()
                .filter(c -> c.getId().equals(customer2.getId()))
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

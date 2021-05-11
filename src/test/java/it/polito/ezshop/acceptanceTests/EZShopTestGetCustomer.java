package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static it.polito.ezshop.acceptanceTests.TestHelpers.assertThrows;
import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static it.polito.ezshop.model.Utils.generateId;
import static org.junit.Assert.*;

public class EZShopTestGetCustomer {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.CASHIER);
    private static final Customer customer1 = new Customer("Pietro", "1234567890", 0, 0);
    private static final Customer customer2 = new Customer("Maria", "2345678901", 0, 0);
    private static String card;

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            UnauthorizedException, InvalidCustomerNameException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());

        // login to setup shop instance
        shop.login(user.getUsername(), user.getPassword());

        // add two customers and a card to the shop
        customer1.setId(shop.defineCustomer(customer1.getCustomerName()));
        customer2.setId(shop.defineCustomer(customer2.getCustomerName()));
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
        testAccessRights(defineCustomer, new Object[] {1},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that a null ID throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionNull() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is null
        assertThrows(InvalidCustomerIdException.class, () -> shop.getCustomer(null));
    }

    /**
     * Tests that a negative ID throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionNegative() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is negative
        assertThrows(InvalidCustomerIdException.class, () -> shop.getCustomer(-1));
    }

    /**
     * Tests that ID 0 throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionZero() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is 0
        assertThrows(InvalidCustomerIdException.class, () -> shop.getCustomer(0));
    }

    /**
     * Tests that null is returned if no customer with the given ID exists
     */
    @Test
    public void testNullIfCustomerNotExists() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

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
    public void testCustomerReturnedSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException, InvalidCustomerNameException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(customer1.getId());

        // verify that customer data is correct
        assertEquals(customer1.getId(), customer.getId());
        assertEquals(customer1.getCustomerName(), customer.getCustomerName());
    }

    /**
     * Tests that attached card and points are returned correctly with customer
     */
    @Test
    public void testCardReturnedSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException, InvalidCustomerNameException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach card to customer and add some points
        int pointsOnCard = 100;
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card));
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(customer1.getId());

        // verify that card data is correct
        assertEquals(card, customer.getCustomerCard());
        assertEquals(new Integer(pointsOnCard), customer.getPoints());
    }
}

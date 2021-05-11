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
import static org.junit.Assert.assertEquals;

public class EZShopTestAttachCardToCustomer {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.CASHIER);
    private static final Customer customer1 = new Customer("Pietro", "1234567890", 0, 0);
    private static final Customer customer2 = new Customer("Maria", "2345678901", 0, 0);
    private static String card1;
    private static String card2;

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            InvalidCustomerNameException, UnauthorizedException {

        // reset shop to clean state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());

        // login to setup shop instance
        shop.login(user.getUsername(), user.getPassword());

        // add two customers to shop
        customer1.setId(shop.defineCustomer(customer1.getCustomerName()));
        customer2.setId(shop.defineCustomer(customer2.getCustomerName()));

        // generate two cards for shop
        card1 = shop.createCard();
        card2 = shop.createCard();

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method modifyCustomer = EZShop.class.getMethod("attachCardToCustomer", String.class, Integer.class);
        testAccessRights(modifyCustomer, new Object[] {"1234567890", 1},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * If the new customer card is provided in an invalid format an InvalidCustomerCardException is thrown
     */
    @Test
    public void testInvalidCustomerCardException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // card number less than 10 digits
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.attachCardToCustomer("123456789", customer1.getId()));

        // card number more than 10 digits
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.attachCardToCustomer("12345678901", customer1.getId()));

        // card number non-numeric characters
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.attachCardToCustomer("123456789a", customer1.getId()));
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.attachCardToCustomer("123456789A", customer1.getId()));
    }

    /**
     * Tests that a null ID throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionNull() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is null
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card1, null));
    }

    /**
     * Tests that a negative ID throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionNegative() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is negative
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card1, -1));
    }

    /**
     * Tests that ID 0 throws an InvalidCustomerIdException
     */
    @Test
    public void testInvalidCustomerIdExceptionZero() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // throw an InvalidCustomerIdException when ID is 0
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card1, 0));
    }

    /**
     * Tests that false is returned if the customer with the given ID does not exist
     */
    @Test
    public void testFalseCustomerNotExists() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerCardException, InvalidCustomerIdException, InvalidCustomerNameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // generate an ID which isn't taken by any customer
        int nonExistentId = generateId(
                shop.getAllCustomers().stream()
                        .map(it.polito.ezshop.data.Customer::getId)
                        .collect(Collectors.toList()));

        // changing name to null throws exception
        assertFalse(shop.attachCardToCustomer(card1, nonExistentId));
    }

    /**
     * Test whether an existing card can be attached to a customer successfully
     */
    @Test
    public void testAttachCardToCustomer() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // card is successfully attached to customer
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));

        // verify that customer's card is indeed updated correctly
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
    }

    /**
     * Test whether two different cards can be attached to two different customers successfully
     */
    @Test
    public void testAttachCardsToCustomers() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // cards are successfully attached to customers
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));
        assertTrue(shop.attachCardToCustomer(card2, customer2.getId()));

        // verify that customer cards are indeed updated correctly
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
        assertEquals(card2, shop.getCustomer(customer2.getId()).getCustomerCard());
    }

    /**
     * Test that each card can only be attached to a single customer
     */
    @Test
    public void testOneCustomerPerCard() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach different a card to a customer
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));

        // try to attach the same card to a different customer
        assertFalse(shop.attachCardToCustomer(card1, customer2.getId()));

        // verify that the first customer's card is still attached to first customer
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());

        // verify that the second customer has no attached card
        assertNull(shop.getCustomer(customer2.getId()).getCustomerCard());
    }

    /**
     * Test that each customer can only have one attached card
     */
    @Test
    public void testOneCardPerCustomer() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach a card to a customer
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));

        // try to attach a different card to the same customer
        assertFalse(shop.attachCardToCustomer(card2, customer1.getId()));

        // verify that the first customer's card is still attached to first customer
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
    }
}

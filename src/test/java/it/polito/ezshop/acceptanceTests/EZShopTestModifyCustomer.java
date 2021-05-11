package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.acceptanceTests.TestHelpers.assertThrows;
import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestModifyCustomer {

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
        Method modifyCustomer = EZShop.class.getMethod("modifyCustomer", Integer.class, String.class, String.class);
        testAccessRights(modifyCustomer, new Object[] {"Pietro"},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * If an empty customerName is provided an InvalidCustomerNameException should be thrown
     */
    @Test
    public void testInvalidCustomerNameExceptionIfNameEmpty() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // changing name to empty name throws exception
        assertThrows(InvalidCustomerNameException.class, () -> shop.modifyCustomer(customer1.getId(), "", card1));
    }

    /**
     * If null is passed as an argument for customerName an InvalidCustomerNameException should be thrown
     */
    @Test
    public void testInvalidCustomerNameExceptionIfNameNull() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // changing name to null throws exception
        assertThrows(InvalidCustomerNameException.class,
                () -> shop.modifyCustomer(customer1.getId(), null, card1));
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
                () -> shop.modifyCustomer(customer1.getId(), "Diogo", "123456789"));

        // card number more than 10 digits
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyCustomer(customer1.getId(), "Diogo", "12345678901"));

        // card number non-numeric characters
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyCustomer(customer1.getId(), "Diogo", "123456789a"));
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyCustomer(customer1.getId(), "Diogo", "123456789A"));
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
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

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
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));
        assertTrue(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card2));

        // verify that customer cards are indeed updated correctly
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
        assertEquals(card2, shop.getCustomer(customer2.getId()).getCustomerCard());
    }

    /**
     * Test whether a customer card can be detached from a customer by providing empty string
     */
    @Test
    public void testDetachCardFromCustomer() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach card to customer successfully
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

        // detach card from customer by specifying empty string as new card
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), ""));

        // verify that customer has no attached card
        assertNull(shop.getCustomer(customer1.getId()).getCustomerCard());
    }

    /**
     * Test whether the customers card remains unchanged if null is provided as new card argument
     */
    @Test
    public void testLeaveCardUnchanged() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // card is successfully attached to customer
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

        // change customer name without changing card
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), null));

        // verify that customer still has previous card
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
    }

    /**
     * Test whether the customer name can be changed
     */
    @Test
    public void testChangeCustomerName() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // change customer name
        String newCustomerName = customer1.getCustomerName() + "123";
        assertTrue(shop.modifyCustomer(customer1.getId(), newCustomerName, card1));

        // verify that customer name was updated correctly
        assertEquals(newCustomerName, shop.getCustomer(customer1.getId()).getCustomerName());
    }

    /**
     * Test whether changing the customer's name to an already existing name returns an error value
     */
    @Test
    public void testChangedNameStillUnique() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // change customer name
        assertFalse(shop.modifyCustomer(customer1.getId(), customer2.getCustomerName(), card1));

        // verify that customer name was not changed
        assertEquals(customer1.getCustomerName(), shop.getCustomer(customer1.getId()).getCustomerName());
    }

    /**
     * Test that each card can only be attached to a single customer
     */
    @Test
    public void testOneCustomerPerCard() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach different cards to different customers
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));
        assertTrue(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card2));

        // try to attach modify the second customer to have the same card as the first customer
        assertFalse(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card1));

        // verify that the first customer's card is still attached to first customer
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());

        // verify that the second customer still hase the second card attached
        assertEquals(card2, shop.getCustomer(customer2.getId()).getCustomerCard());
    }

    /**
     * Test whether changing the customer a card is attached to doesn't change the card's points
     */
    @Test
    public void testCardPointsArePersistent() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // attach card to customer
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

        // add points to card
        int pointsOnCard = 100;
        assertTrue(shop.modifyPointsOnCard(card1, pointsOnCard));

        // detach card from customer
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), ""));

        // attach card to different customer
        assertTrue(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card1));
        
        // verify that points on card still remains the same
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customer2.getId()).getPoints());
    }
}

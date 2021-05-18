package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.stream.Collectors;

import static unitTests.TestHelpers.*;
import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class EZShopTestAttachCardToCustomer {

    private static final EZShop shop = new EZShop();
    private static  User admin;

    static {
        try {
            admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());

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
        testAccessRights(modifyCustomer, new Object[] {card1, 1},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that an InvalidCustomerCardException is thrown if the card is in an invalid format. Cards should be a
     * 10-digit string
     */
    @Test
    public void testInvalidCustomerCardException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown for string null, empty, to short, too long or contains alphabetic characters
        testInvalidValues(InvalidCustomerCardException.class, invalidCustomerCards,
                (card) -> shop.attachCardToCustomer(card, customer1.getId()));
    }

    /**
     * Tests that  an InvalidCustomerIdException is thrown when ID is null, 0 or negative
     */
    @Test
    public void testInvalidCustomerIdException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCustomerIdException.class, invalidCustomerIDs,
                (id) -> shop.attachCardToCustomer(card1, id));
    }

    /**
     * Tests that false is returned if the customer with the given ID does not exist
     */
    @Test
    public void testFalseIfCustomerNotExists() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerCardException, InvalidCustomerIdException, InvalidCustomerNameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any customer
        int nonExistentId = generateId(
                shop.getAllCustomers().stream()
                        .map(it.polito.ezshop.data.Customer::getId)
                        .collect(Collectors.toList()));

        // attaching to non-existing customer returns false
        assertFalse(shop.attachCardToCustomer(card1, nonExistentId));
    }

    /**
     * Tests that false is returned if the card does not exist
     */
    @Test
    public void testFalseIfCardNotExists() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerCardException, InvalidCustomerIdException, InvalidCustomerNameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate a valid card which doesn't exist in the shop
        String nonExistentCard = "123456789";
        char lastDigit = '0';
        while (card1.equals(nonExistentCard + lastDigit) || card2.equals(nonExistentCard + lastDigit)) {lastDigit++;}
        nonExistentCard += lastDigit;

        // attaching a non-existing card returns false
        assertFalse(shop.attachCardToCustomer(nonExistentCard, customer1.getId()));
    }

    /**
     * Test whether an existing card can be attached to a customer successfully
     */
    @Test
    public void testAttachCardToCustomer() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

        // attach a card to a customer
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));

        // try to attach a different card to the same customer
        assertFalse(shop.attachCardToCustomer(card2, customer1.getId()));

        // verify that the first customer's card is still attached to first customer
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
    }

    /**
     * Test that card still has same amount of points after being attached to a customer
     */
    @Test
    public void testCardPointsArePersistent() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add points to card
        int pointsOnCard = 100;
        assertTrue(shop.modifyPointsOnCard(card1, pointsOnCard));

        // attach card to customer
        assertTrue(shop.attachCardToCustomer(card1, customer1.getId()));

        // verify that points on card still remains the same
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customer2.getId()).getPoints());
    }
}

package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static unitTests.TestHelpers.*;
import static unitTests.TestHelpers.invalidCustomerNames;
import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.*;

public class EZShopTestModifyCustomer {

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
        Method modifyCustomer = EZShop.class.getMethod("modifyCustomer", Integer.class, String.class, String.class);
        testAccessRights(modifyCustomer, new Object[] {1, "Pietro", null},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that an InvalidCustomerNameException is thrown if the customer name is null or empty
     */
    @Test
    public void testInvalidCustomerNameException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCustomerNameException.class, invalidCustomerNames,
                (name) -> shop.modifyCustomer(customer1.getId(), name, card1));
    }

    /**
     * Tests that an InvalidCustomerCardException is thrown if the card is in an invalid format, but does not have a
     * special meaning value. Cards should be a 10 digit string, null or the empty string have special meaning and
     * should not throw an exception.
     */
    @Test
    public void testInvalidCustomerCardException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown for string to short, too long or contains alphabetic characters
        testInvalidValues(InvalidCustomerCardException.class,
                Arrays.asList("123456789", "12345678901", "123456789a", "123456789A"),
                (card) -> shop.modifyCustomer(customer1.getId(), "Diogo", card));
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

        // accessing a non-existing id returns false
        assertFalse(shop.modifyCustomer(nonExistentId, "Diogo", "1234567890"));
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

        // assigning a non-existing card returns false
        assertFalse(shop.modifyCustomer(customer1.getId(), "Diogo", nonExistentCard));
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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

        // try to change to already taken name
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
        shop.login(admin.getUsername(), admin.getPassword());

        // attach a card to a customer
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

        // try to modify a second customer to have the same card as the first customer
        assertFalse(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card1));

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
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card1));

        // try attach a second card to the customer
        assertFalse(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card2));

        // verify that the first customer's card is still attached to first customer
        assertEquals(card1, shop.getCustomer(customer1.getId()).getCustomerCard());
    }

    /**
     * Test that changing the customer a card is attached to doesn't change the card's points
     */
    @Test
    public void testCardPointsArePersistent() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

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

        // verify that first customer no longer has points
        assertEquals(new Integer(0), shop.getCustomer(customer1.getId()).getPoints());
    }
}

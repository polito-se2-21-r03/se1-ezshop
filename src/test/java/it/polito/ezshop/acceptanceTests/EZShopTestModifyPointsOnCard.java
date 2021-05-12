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

public class EZShopTestModifyPointsOnCard {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.CASHIER);
    private static final Customer customer = new Customer("Maria", "2345678901", 0, 0);
    private static String card;

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            UnauthorizedException, InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());

        // login to setup shop instance
        shop.login(user.getUsername(), user.getPassword());

        // add a customer and attach a new card
        customer.setId(shop.defineCustomer(customer.getCustomerName()));
        card = shop.createCard();
        shop.attachCardToCustomer(card, customer.getId());

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("modifyPointsOnCard", String.class, int.class);
        testAccessRights(defineCustomer, new Object[] {card, 10},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * If the new customer card is empty, null or provided in an invalid format an InvalidCustomerCardException is thrown
     */
    @Test
    public void testInvalidCustomerCardException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // card is null
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard(null, 10));

        // card is empty
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard("", 10));

        // card number less than 10 digits
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard("123456789", 10));

        // card number more than 10 digits
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard("12345678901", 10));

        // card number contains non-numeric characters
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard("123456789a", 10));
        assertThrows(InvalidCustomerCardException.class,
                () -> shop.modifyPointsOnCard("123456789A", 10));
    }

    /**
     * Test that false is returned if the provided card number is valid but no card with that number exists
     */
    @Test
    public void testFalseCardDoesNotExist() throws InvalidCustomerCardException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // generate a card number that is not held by any card in the shop
        String cardNotInShop = "0000000000";
        if (cardNotInShop.equals(card)) {
            cardNotInShop = "1111111111";
        }

        assertFalse(shop.modifyPointsOnCard(cardNotInShop, 10));
    }

    /**
     * Test that points can be added to the card
     */
    @Test
    public void testAddPointsToCard() throws InvalidCustomerCardException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // can not add negative points to card
        int pointsOnCard = 10;
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // verify that points are indeed on card
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customer.getId()).getPoints());
    }

    /**
     * Test that any operation resulting in negative points on a card is not carried out and returns false
     */
    @Test
    public void testFalseNegativePointsIllegal() throws InvalidCustomerCardException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // add some points to card
        int pointsOnCard = 5;
        assertTrue(shop.modifyPointsOnCard(card, 5));

        // can not add negative points to card
        assertFalse(shop.modifyPointsOnCard(card, -10));

        // verify that points on card have not been changed
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customer.getId()).getPoints());
    }

    /**
     * Test that removing points from the card is possible iff the resulting amount of points remains positive
     */
    @Test
    public void testRemovePoints() throws InvalidCustomerCardException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // variables to keep track of modifications
        int expectedPointsOnCard = 0;
        int modifyPoints;

        // add some points to card
        modifyPoints = 15;
        assertTrue(shop.modifyPointsOnCard(card, modifyPoints));
        expectedPointsOnCard += modifyPoints;

        // remove some points from card
        modifyPoints = -10;
        assertTrue(shop.modifyPointsOnCard(card, modifyPoints));
        expectedPointsOnCard += modifyPoints;

        // fail to remove too many points from card
        modifyPoints = -10;
        assertFalse(shop.modifyPointsOnCard(card, modifyPoints));

        // remove remaining points from card
        modifyPoints = -5;
        assertFalse(shop.modifyPointsOnCard(card, modifyPoints));
        expectedPointsOnCard += modifyPoints;

        // verify that points on card are as expected
        assertEquals(new Integer(expectedPointsOnCard), shop.getCustomer(customer.getId()).getPoints());
    }
}

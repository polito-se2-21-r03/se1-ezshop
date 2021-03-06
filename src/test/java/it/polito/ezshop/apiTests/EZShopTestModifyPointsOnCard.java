package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static it.polito.ezshop.TestHelpers.*;

public class EZShopTestModifyPointsOnCard {

    private static final EZShop shop = new EZShop();
    private static  User admin;
    private static final String customerName = "Pietro";
    private static Integer customerID;
    private static String card;

    public EZShopTestModifyPointsOnCard() throws Exception {
        admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
    }

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException,
            UnauthorizedException, InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());

        // add a customer and attach a new card
        customerID = shop.defineCustomer(customerName);
        card = shop.createCard();
        shop.attachCardToCustomer(card, customerID);

        // logout after setup
        shop.logout();
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("modifyPointsOnCard", String.class, int.class);
        testAccessRights(defineCustomer, new Object[]{card, 10},
                new Role[]{Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
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
                (card) -> shop.modifyPointsOnCard(card, 0));
    }

    /**
     * Test that false is returned if the provided card number is valid but no card with that number exists
     */
    @Test
    public void testFalseIfCardDoesNotExist() throws InvalidCustomerCardException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

        // can not add negative points to card
        int pointsOnCard = 10;
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // verify that points are indeed on card
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customerID).getPoints());
    }

    /**
     * Test that any operation resulting in negative points on a card is not carried out and returns false
     */
    @Test
    public void testFalseNegativePointsIllegal() throws InvalidCustomerCardException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // add some points to card
        int pointsOnCard = 5;
        assertTrue(shop.modifyPointsOnCard(card, 5));

        // can not add negative points to card
        assertFalse(shop.modifyPointsOnCard(card, -10));

        // verify that points on card have not been changed
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customerID).getPoints());
    }

    /**
     * Test that removing points from the card is possible iff the resulting amount of points remains positive
     */
    @Test
    public void testRemovePoints() throws InvalidCustomerCardException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

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
        assertTrue(shop.modifyPointsOnCard(card, modifyPoints));
        expectedPointsOnCard += modifyPoints;

        // verify that points on card are as expected
        assertEquals(new Integer(expectedPointsOnCard), shop.getCustomer(customerID).getPoints());
    }
}

package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EZShopTestCreateCard {

    private static final EZShop shop = new EZShop();
    private static User admin;

    static {
        try {
            admin = new User(1, "Andrea", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Method defineCustomer = EZShop.class.getMethod("createCard");
        testAccessRights(defineCustomer, new Object[] {},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Tests that createCard returns a valid card number
     */
    @Test
    public void testValidCardNumberReturned() throws InvalidPasswordException, InvalidUsernameException, UnauthorizedException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate card
        String generatedCard = shop.createCard();

        // verify card number consists of 10 digits
        assertEquals(10, generatedCard.length());
        for (char c : generatedCard.toCharArray()) {
            assertTrue(Character.isDigit(c));
        }
    }

    /**
     * Tests that a created card can be attached to a customer
     */
    @Test
    public void testCardCanBeAttached() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException, InvalidCustomerCardException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // create user
        int customerId = shop.defineCustomer("Alessio");

        // generate card
        String card = shop.createCard();

        // attach card to customer
        assertTrue(shop.attachCardToCustomer(card, customerId));

        // verify card was attached correctly
        assertEquals(card, shop.getCustomer(customerId).getCustomerCard());
    }
}

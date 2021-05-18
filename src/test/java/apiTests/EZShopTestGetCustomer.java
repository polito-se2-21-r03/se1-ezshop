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
import static unitTests.TestHelpers.invalidCustomerIDs;
import static it.polito.ezshop.utils.Utils.generateId;
import static org.junit.Assert.*;

public class EZShopTestGetCustomer {

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Andrea", "123", Role.ADMINISTRATOR);
    private static final Customer customer = new Customer("Pietro", "1234567890", 0, 0);
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
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());

        // login to setup shop instance
        shop.login(admin.getUsername(), admin.getPassword());

        // add two customers and a card to the shop
        customer.setId(shop.defineCustomer(customer.getCustomerName()));
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
     * Tests that  an InvalidCustomerIdException is thrown when ID is null, 0 or negative
     */
    @Test
    public void testInvalidCustomerIdException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCustomerIdException.class, invalidCustomerIDs, shop::getCustomer);
    }

    /**
     * Tests that null is returned if no customer with the given ID exists
     */
    @Test
    public void testNullIfCustomerNotExists() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

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
        shop.login(admin.getUsername(), admin.getPassword());

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(EZShopTestGetCustomer.customer.getId());

        // verify that customer data is correct
        assertEquals(EZShopTestGetCustomer.customer.getId(), customer.getId());
        assertEquals(EZShopTestGetCustomer.customer.getCustomerName(), customer.getCustomerName());
    }

    /**
     * Tests that attached card and points are returned correctly with customer
     */
    @Test
    public void testCardReturnedSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException, InvalidCustomerNameException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // attach card to customer and add some points
        int pointsOnCard = 100;
        assertTrue(shop.attachCardToCustomer(card, customer.getId()));
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // get customer
        it.polito.ezshop.data.Customer customer = shop.getCustomer(EZShopTestGetCustomer.customer.getId());

        // verify that card data is correct
        assertEquals(card, customer.getCustomerCard());
        assertEquals(new Integer(pointsOnCard), customer.getPoints());
    }
}

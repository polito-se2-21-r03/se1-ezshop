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

public class EZShopTestDeleteCustomer {

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
        Method defineCustomer = EZShop.class.getMethod("deleteCustomer", Integer.class);
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
        testInvalidValues(InvalidCustomerIdException.class, invalidCustomerIDs, shop::deleteCustomer);
    }

    /**
     * Tests that false is returned if no customer with that ID exists
     */
    @Test
    public void testFalseIfCustomerDoesNotExist() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // generate an ID which isn't taken by any customer
        int nonExistentId = generateId(
                shop.getAllCustomers().stream()
                        .map(it.polito.ezshop.data.Customer::getId)
                        .collect(Collectors.toList()));

        // return false when customer does not exist
        assertFalse(shop.deleteCustomer(nonExistentId));

        // verify no customers have been deleted
        assertEquals(2, shop.getAllCustomers().size());
    }

    /**
     * Tests deleting a customer successfully
     */
    @Test
    public void testDeleteCustomerSuccessfully() throws InvalidPasswordException, InvalidUsernameException,
            UnauthorizedException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // delete customer successfully
        assertTrue(shop.deleteCustomer(customer1.getId()));

        // verify customer does not exist anymore
        assertEquals(1, shop.getAllCustomers().size());
        assertNull(shop.getCustomer(customer1.getId()));

        // verify other customer still exist
        assertEquals(customer2.getId(), shop.getCustomer(customer2.getId()).getId());
        assertEquals(customer2.getCustomerName(), shop.getCustomer(customer2.getId()).getCustomerName());
    }

    /**
     * Tests that a card still exists after the customer it was attached to has been deleted and the amount of points on
     * the card remains unchanged
     */
    @Test
    public void testCardNotDeletedWithCustomer() throws InvalidPasswordException, InvalidUsernameException,
            InvalidCustomerIdException, InvalidCustomerNameException, UnauthorizedException, InvalidCustomerCardException {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // attach card to customer and add some points
        int pointsOnCard = 100;
        assertTrue(shop.modifyCustomer(customer1.getId(), customer1.getCustomerName(), card));
        assertTrue(shop.modifyPointsOnCard(card, pointsOnCard));

        // delete customer
        assertTrue(shop.deleteCustomer(customer1.getId()));

        // attach card to different customer
        assertTrue(shop.modifyCustomer(customer2.getId(), customer2.getCustomerName(), card));

        // verify points on card hasn't changed
        assertEquals(new Integer(pointsOnCard), shop.getCustomer(customer2.getId()).getPoints());
    }
}

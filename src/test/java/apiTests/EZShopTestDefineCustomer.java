package apiTests;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static unitTests.TestHelpers.*;
import static org.junit.Assert.*;

/**
 * Tests for the EZShop.defineCustomer(String) method.
 */
public class EZShopTestDefineCustomer {

    private static final EZShop shop = new EZShop();
    private static final User user = new User(0, "Andrea", "123", Role.ADMINISTRATOR);

    /**
     * Creates a clean shop instance for each test
     */
    @Before
    public void beforeEach() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        // reset shop to blanc state
        shop.reset();

        // setup authorized user
        shop.createUser(user.getUsername(), user.getPassword(), user.getRole());
    }

    /**
     * Tests that access rights are handled correctly by defineCustomer.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method defineCustomer = EZShop.class.getMethod("defineCustomer", String.class);
        testAccessRights(defineCustomer, new Object[] {"Pietro"},
                new Role[] {Role.SHOP_MANAGER, Role.ADMINISTRATOR, Role.CASHIER});
    }

    /**
     * Test that an InvalidCustomerNameException is thrown when customer name is null or empty
     */
    @Test
    public void testInvalidCustomerNameException() throws InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // verify correct exception is thrown
        testInvalidValues(InvalidCustomerNameException.class, invalidCustomerNames, shop::defineCustomer);
    }

    /**
     * Define a single customer successfully
     */
    @Test
    public void testDefineSingleCustomerSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        // login and create customer
        shop.login(user.getUsername(), user.getPassword());
        String customerName = "Pietro";
        Integer customerId = shop.defineCustomer(customerName);

        // definition of customer was successful
        assertTrue(customerId > 0);

        // only one customer was defined
        List<Customer> customerList = shop.getAllCustomers();
        assertEquals(1, customerList.size());

        // created customer has expected ID and name and no attached card or points
        Customer customer = customerList.get(0);
        assertEquals(customerId, customer.getId());
        assertEquals(customerName, customer.getCustomerName());
        assertNull(customer.getCustomerCard());
        assertEquals(new Integer(0), customer.getPoints());
    }

    /**
     * If a customer name is provided, that is not unique within the system an error value should be returned
     */
    @Test
    public void testOnlyUniqueCustomerNamesAllowed() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // define first customer with unique name successfully
        assertTrue(shop.defineCustomer("Pietro") > 0);

        // return error value on definition of customer with duplicate customer name
        assertEquals(new Integer(-1), shop.defineCustomer("Pietro"));

        // verify that only one customer was created
        assertEquals(1, shop.getAllCustomers().size());
    }

    /**
     * Define a set of customers successfully
     */
    @Test
    public void testDefineManyCustomersSuccessfully() throws InvalidCustomerNameException, UnauthorizedException,
            InvalidPasswordException, InvalidUsernameException, InvalidCustomerIdException {

        // login with sufficient rights
        shop.login(user.getUsername(), user.getPassword());

        // define names and IDs
        String[] customerNames = new String[] {"Pietro", "Andrea", "Sarah", "Maria"};
        int[] customerIDs = new int[customerNames.length];

        // insert customers
        for (int i=0; i<customerNames.length; i++) {
            customerIDs[i] = shop.defineCustomer(customerNames[i]);
        }

        // all customers have been created successfully and have unique positive IDs and no other customers have been created
        assertEquals(customerNames.length, Arrays.stream(customerIDs).filter(id -> id != 0).distinct().count());

        // all customers have been created with the expected ID, name and no card or points
        for (int i=0; i<customerNames.length; i++) {
            Customer customer = shop.getCustomer(customerIDs[i]);
            assertEquals(new Integer(customerIDs[i]), customer.getId());
            assertEquals(customerNames[i], customer.getCustomerName());
            assertNull(customer.getCustomerCard());
            assertEquals(new Integer(0), customer.getPoints());
        }
    }
}

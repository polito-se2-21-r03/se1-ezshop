package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import it.polito.ezshop.model.adapters.CustomerAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCustomerAdapter {

    public static final String name = "Simone";
    public static final String cardCode = "1234567890";
    public static final int points = 10;
    private static final int id = 1;

    Customer customer1, customer2;
    CustomerAdapter customerAdapter1;
    CustomerAdapter customerAdapter2;

    @Before
    public void beforeEach() throws Exception {
        customer1 = new Customer(id, name);
        customer2 = new Customer(id, name);
        customer1.setCard(new LoyaltyCard(cardCode));

        customerAdapter1 = new CustomerAdapter(customer1);
        customerAdapter2 = new CustomerAdapter(customer2);
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new CustomerAdapter(null));
    }

    @Test
    public void testSetters() {
        // test set id illegal
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter1.setId(id + 1));

        // test set illegal names
        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(IllegalArgumentException.class, () -> customerAdapter1.setCustomerName(name));
        }

        // test set card illegal
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter1.setCustomerCard(LoyaltyCard.generateNewCode()));

        // test set negative points
        assertThrows(IllegalArgumentException.class, () -> customerAdapter1.setPoints(-1));

        // test set/get name
        customerAdapter1.setCustomerName("John");
        assertEquals("John", customerAdapter1.getCustomerName());

        // test set/get points
        customerAdapter1.setPoints(points + 1);
        assertEquals((Integer) (points + 1), customerAdapter1.getPoints());
    }

    @Test
    public void testGetters() {
        assertEquals(customer1.getId(), customerAdapter1.getId());
        assertEquals(customer1.getCustomerName(), customerAdapter1.getCustomerName());

        assertEquals(customer1.getCard().getCode(), customerAdapter1.getCustomerCard());
        assertNull(customerAdapter2.getCustomerCard());

        assertEquals((Integer) customer1.getCard().getPoints(), customerAdapter1.getPoints());
        assertEquals((Integer) 0, customerAdapter2.getPoints());
    }
}

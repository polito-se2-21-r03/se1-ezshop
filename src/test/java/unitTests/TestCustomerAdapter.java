package unitTests;

import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import it.polito.ezshop.model.adapters.CustomerAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestCustomerAdapter {

    public static final String name = "Simone";
    public static final String cardCode = "1234567890";
    public static final int points = 10;
    private static final int id = 1;

    CustomerAdapter customerAdapter1;
    CustomerAdapter customerAdapter2;

    @Before
    public void beforeEach() throws Exception {

        Customer customer1 = new Customer(id, name);
        Customer customer2 = new Customer(id, name);
        customer2.setCard(new LoyaltyCard(cardCode));

        customerAdapter1 = new CustomerAdapter(customer1);
        customerAdapter2 = new CustomerAdapter(customer2);
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new CustomerAdapter(null));
    }

    @Test
    public void testSetters() {

        // test get/set id
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter1.setId(id + 1));

        // test get/set name
        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(IllegalArgumentException.class, () -> customerAdapter1.setCustomerName(name));
        }

        customerAdapter1.setCustomerName("John");
        assertEquals("John", customerAdapter1.getCustomerName());

        // test get/set points
        customerAdapter1.setPoints(points + 1);
        assertEquals((Integer) (points + 1), customerAdapter1.getPoints());
    }
}

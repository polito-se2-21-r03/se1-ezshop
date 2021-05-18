package unitTests;

import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import it.polito.ezshop.model.adapters.CustomerAdapter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestCustomerAdapter {

    public static final String name = "Simone";
    public static final String cardCode = "1234567890";
    public static final int points = 10;
    private static final int id = 1;

    private final Customer customer;

    public TestCustomerAdapter() throws Exception {
        LoyaltyCard card = new LoyaltyCard(cardCode, points);
        customer = new Customer(id, name, card);
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new CustomerAdapter(null));
    }

    @Test
    public void testSetters() {
        CustomerAdapter customerAdapter = new CustomerAdapter(customer);

        // test get/set id
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter.setId(id + 1));

        // test get/set name
        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(IllegalArgumentException.class, () -> customerAdapter.setCustomerName(name));
        }

        customerAdapter.setCustomerName("John");
        assertEquals("John", customerAdapter.getCustomerName());

        // test get/set points
        int current = customer.getCard().getPoints();
        customerAdapter.setPoints(current + 1);
        assertEquals((Integer) (current + 1), customerAdapter.getPoints());
    }
}

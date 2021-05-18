package unitTests;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import it.polito.ezshop.model.adapters.CustomerAdapter;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCustomerAdapter {

    public static final String name = "Simone";
    public static final String cardCode = "1234567890";
    public static final int points = 10;
    private static final int id = 1;
    LoyaltyCard card;

    {
        try {
            card = new LoyaltyCard(cardCode, points);
        } catch (InvalidCustomerCardException e) {
            e.printStackTrace();
        }
    }

    Customer customer1 = new Customer(name, id, null);
    Customer customer2 = new Customer(name, id, card);

    CustomerAdapter customerAdapter1 = new CustomerAdapter(customer1);
    CustomerAdapter customerAdapter2 = new CustomerAdapter(customer2);

    @Test
    public void testSetters() {
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter2.setId(id));
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter2.setCustomerName(name));
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter2.setCustomerCard(cardCode));
        assertThrows(UnsupportedOperationException.class, () -> customerAdapter2.setPoints(points));
    }

    @Test
    public void testGetters() {
        assertEquals((Integer) id, customerAdapter1.getId());
        assertEquals(name, customerAdapter1.getCustomerName());
        assertNull(customerAdapter1.getCustomerCard());
        assertEquals((Integer) 0, customerAdapter1.getPoints());

        assertEquals(cardCode, customerAdapter2.getCustomerCard());
        assertEquals((Integer) points, customerAdapter2.getPoints());
    }

}

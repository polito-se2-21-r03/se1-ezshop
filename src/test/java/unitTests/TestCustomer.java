package unitTests;

import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCustomer {

    public static final int id = 1;
    public static final String name = "Simone";

    final LoyaltyCard card = new LoyaltyCard("1234567890", 10);
    final LoyaltyCard card2 = new LoyaltyCard("1234567891", 2);

    @Test
    public void testConstructor() {
        Customer customer = new Customer(name, id, null);

        assertEquals((Integer) id, customer.getId());
        assertEquals(name, customer.getCustomerName());
        assertNull(customer.getCard());

        customer = new Customer(name, id, card);

        assertEquals((Integer) id, customer.getId());
        assertEquals(name, customer.getCustomerName());
        assertEquals(card, customer.getCard());
    }

    @Test
    public void testSetId() {
        Customer customer = new Customer(name, id, null);

        customer.setId(2);
        assertEquals((Integer) 2, customer.getId());
    }

    @Test
    public void testSetName() {
        Customer customer = new Customer(name, id, null);

        customer.setCustomerName("Pietro");
        assertEquals("Pietro", customer.getCustomerName());
    }

    @Test
    public void testSetCard() {
        Customer customer = new Customer(name, id, null);

        customer.setCard(card2);
        assertEquals(card2, customer.getCard());
    }

    @Test
    public void testEqualsHashCode() {
        Customer customer = new Customer(name, id, null);
        Customer customerSame = new Customer(name, id, null);
        Customer customerDifferent = new Customer("Pietro", id + 1, card2);

        assertEquals(customer, customerSame);
        assertNotEquals(customer, customerDifferent);

        assertEquals(customer.hashCode(), customerSame.hashCode());
        assertNotEquals(customer.hashCode(), customerDifferent.hashCode());
    }
}

package unitTests;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.model.Customer;
import it.polito.ezshop.model.LoyaltyCard;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCustomer {

    public static final int id = 1;
    public static final String name = "Simone";

    final LoyaltyCard card;
    final LoyaltyCard card2;

    public TestCustomer() throws InvalidCustomerCardException {
        card = new LoyaltyCard("1234567890", 10);
        card2 = new LoyaltyCard("1234567891", 2);
    }

    @Test
    public void testConstructor() throws Exception {
        for (Integer id : TestHelpers.invalidCustomerIDs) {
            assertThrows(InvalidCustomerIdException.class, () -> new Customer(id, name, null));
        }

        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> new Customer(id, name, null));
        }

        Customer customer = new Customer(id, name, null);
        assertEquals((Integer) id, customer.getId());
        assertEquals(name, customer.getCustomerName());
        assertNull(customer.getCard());

        customer = new Customer(id, name, card);

        assertEquals((Integer) id, customer.getId());
        assertEquals(name, customer.getCustomerName());
        assertEquals(card, customer.getCard());
    }

    @Test
    public void testSetName() throws Exception {
        Customer customer = new Customer(id, name, null);

        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> customer.setCustomerName(name));
        }

        customer.setCustomerName("Pietro");
        assertEquals("Pietro", customer.getCustomerName());
    }

    @Test
    public void testSetCard() throws Exception {
        Customer customer = new Customer(id, name, null);

        customer.setCard(card2);
        assertEquals(card2, customer.getCard());

        customer.setCard(null);
        assertNull(customer.getCard());
    }

    @Test
    public void testValidateUsername() throws InvalidCustomerNameException {
        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> Customer.validateName(name));
        }

        Customer.validateName("Frank");
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        Customer customer = new Customer(id, name, null);
        Customer customerSame = new Customer(id, name, null);
        Customer customerDifferent = new Customer(id + 1, "Pietro", card2);

        assertEquals(customer, customerSame);
        assertNotEquals(customer, customerDifferent);

        assertEquals(customer.hashCode(), customerSame.hashCode());
        assertNotEquals(customer.hashCode(), customerDifferent.hashCode());
    }
}

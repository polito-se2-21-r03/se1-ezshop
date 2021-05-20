package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
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
        card = new LoyaltyCard("1234567890");
        card.setPoints(10);
        card2 = new LoyaltyCard("1234567891");
        card2.setPoints(2);
    }

    @Test
    public void testConstructor() throws Exception {
        for (Integer id : TestHelpers.invalidCustomerIDs) {
            assertThrows(InvalidCustomerIdException.class, () -> new Customer(id, name));
        }

        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> new Customer(id, name));
        }

        Customer customer = new Customer(id, name);
        assertEquals((Integer) id, customer.getId());
        assertEquals(name, customer.getCustomerName());
        assertNull(customer.getCard());
    }

    @Test
    public void testSetName() throws Exception {
        Customer customer = new Customer(id, name);

        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> customer.setCustomerName(name));
        }

        customer.setCustomerName("Pietro");
        assertEquals("Pietro", customer.getCustomerName());
    }

    @Test
    public void testSetCard() throws Exception {
        Customer customer = new Customer(id, name);

        customer.setCard(card2);
        assertEquals(card2, customer.getCard());

        customer.setCard(null);
        assertNull(customer.getCard());
    }

    @Test
    public void testIsValidUsername() {
        for (String name : TestHelpers.invalidCustomerNames) {
            assertFalse(Customer.isValidName(name));
        }

        assertTrue(Customer.isValidName("Frank"));
    }

    @Test
    public void testValidateUsername() throws Exception {
        for (String name : TestHelpers.invalidCustomerNames) {
            assertThrows(InvalidCustomerNameException.class, () -> Customer.validateName(name));
        }

        Customer.validateName("Frank");
    }

    @Test
    public void testIsValidID() {
        for (Integer id : TestHelpers.invalidCustomerIDs) {
            assertFalse(Customer.isValidID(id));
        }

        assertTrue(Customer.isValidID(3));
    }

    @Test
    public void testValidateID() throws Exception {
        for (Integer id : TestHelpers.invalidCustomerIDs) {
            assertThrows(InvalidCustomerIdException.class, () -> Customer.validateID(id));
        }

        Customer.validateID(3);
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        Customer customer = new Customer(id, name);
        Customer customerSame = new Customer(id, name);
        Customer customerDifferent = new Customer(id + 1, "Pietro");

        assertEquals(customer, customerSame);
        assertNotEquals(customer, customerDifferent);

        assertEquals(customer.hashCode(), customerSame.hashCode());
        assertNotEquals(customer.hashCode(), customerDifferent.hashCode());
    }
}

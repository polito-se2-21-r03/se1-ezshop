package it.polito.ezshop.integrationTests;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class TestCustomerList {
    private CustomerList customerList;
    private Integer id1;
    private Integer id2;
    private Integer id3;
    private String l1;
    private String l2;
    private String l3;


    public TestCustomerList() throws InvalidCustomerIdException, InvalidCustomerCardException, InvalidCustomerNameException {
        customerList = new CustomerList();

    }

    @Before
    public void beforeEach() throws Exception {
        customerList.reset();

        id1 = customerList.addCustomer("Mario Rossi");
        id2 = customerList.addCustomer("Luigi Bianchi");
        id3 = customerList.addCustomer("Letizia Bruni");
        l1 = customerList.generateNewLoyaltyCard();
        l2 = customerList.generateNewLoyaltyCard();
        l3 = customerList.generateNewLoyaltyCard();
        customerList.attachCardToCustomer(id1, l1);
        customerList.attachCardToCustomer(id3, l2);
        customerList.attachCardToCustomer(id3, l3);

    }

    /**
     * Test that GetCustomer returns the correct customer
     */
    @Test
    public void testGetCustomer() throws InvalidCustomerIdException {

        assertEquals("Mario Rossi", customerList.getCustomer(id1).getCustomerName());

        assertEquals("Luigi Bianchi", customerList.getCustomer(id2).getCustomerName());

        assertEquals("Letizia Bruni", customerList.getCustomer(id3).getCustomerName());
    }

    /**
     * Test that GetAllCustomers returns all the customers
     */
    @Test
    public void testGetAllCustomers() {

        assertEquals(3, customerList.getAllCustomers().stream().distinct().count());

    }

    /**
     * Test that AddCustomer creates a new customer
     */
    @Test
    public void testAddCustomer() throws InvalidCustomerNameException, InvalidCustomerIdException {

        String customerName1 = "Mario Rossi";
        String customerName4 = "Antonio Felice";

        // check if already taken names return -1
        assertEquals(-1, customerList.addCustomer(customerName1));

        int id4 = customerList.addCustomer(customerName4);

        assertEquals("Antonio Felice", customerList.getCustomer(id4).getCustomerName());

        // check if the list has 4 entries
        assertEquals(4, customerList.getAllCustomers().stream().distinct().count());
    }


    /**
     * Test that AddCustomer creates a new customer
     */
    @Test
    public void testModifyCustomer() throws InvalidCustomerNameException, InvalidCustomerIdException, InvalidCustomerCardException {

        // Customer 1 I'm going to update his name only
        // Customer 2 I'm going to update his card only
        // Customer 3 update both

        String newName1 = "Maria Rossi";
        String newLoyalty2 = customerList.generateNewLoyaltyCard();
        String newName3 = "Letizia Bruno";

        // Customer 1 //
        assertTrue(customerList.modifyCustomer(id1, newName1, ""));
        // check that only the name was changed
        assertEquals(newName1, customerList.getCustomer(id1).getCustomerName());
        assertEquals(l1, customerList.getCustomer(id1).getCard().getCode());

        // Customer 2 //
        assertTrue(customerList.modifyCustomer(id2, "Luigi Bianchi", newLoyalty2));
        // check that only the card was changed
        assertEquals("Luigi Bianchi", customerList.getCustomer(id2).getCustomerName());
        assertEquals(newLoyalty2, customerList.getCustomer(id2).getCard().getCode());

        // Customer 3 //
        assertTrue(customerList.modifyCustomer(id3, newName3, null));
        // check that both changed
        assertEquals(newName3, customerList.getCustomer(id3).getCustomerName());
        // in particular, check that the card is deleted
        assertNull(customerList.getCustomer(id3).getCard());

        // Now, if I edit the customer with a name that has been taken it should return me FALSE
        assertFalse(customerList.modifyCustomer(id3, "Luigi Bianchi", ""));

        // Now, if I edit the customer with a card that has been taken it should return me FALSE
        assertFalse(customerList.modifyCustomer(id3, "Carla Bruni", newLoyalty2));

    }

    /**
     * Test that ModifyPointsOnCard edits the points inside the cards
     */
    @Test
    public void testModifyPointsOnCard() throws InvalidCustomerIdException, InvalidCustomerCardException{
        // check if points at the beginning is actually 0
        int points = customerList.getCustomer(id1).getCard().getPoints();
        assertEquals(0, points);

        // check if I add 150 points
        assertTrue(customerList.modifyPointsOnCard(l1, 150));
        assertEquals(150, customerList.getCustomer(id1).getCard().getPoints());
        // check if I add 150 other points
        assertTrue(customerList.modifyPointsOnCard(l1, 150));
        assertEquals(300, customerList.getCustomer(id1).getCard().getPoints());

        // check that it returns false if the sum of points is a negative value
        assertFalse(customerList.modifyPointsOnCard(l1, -500));

        // In case of unassigned card it returns true
        String newl1 = customerList.generateNewLoyaltyCard();
        assertTrue(customerList.modifyPointsOnCard(newl1, 500));

        // In case of inserting a card which is not inside the database it returns false
        assertFalse(customerList.modifyPointsOnCard("1234567890", 500));
    }

    /**
     * Test that RemoveCustomer properly removes the customer
     */
    @Test
    public void testRemoveCustomer() throws InvalidCustomerIdException{
        // Trying to remove Customer 1
        assertTrue(customerList.removeCustomer(id1));
        assertEquals(2, customerList.getAllCustomers().stream().distinct().count());
    }

    /**
     * Test that reset method works
     */
    @Test
    public void testReset() {
        customerList.reset();
        assertEquals(0, customerList.getAllCustomers().stream().distinct().count());
    }

    @Test
    public void testEqualsHashCode() throws Exception {   // UNCOMPLETED
        CustomerList obj = new CustomerList();

        // add a few transactions
        int obj_id1 = customerList.addCustomer("Alberto Carlos Riva");
        String obj_l1 = customerList.generateNewLoyaltyCard();
        int obj_id2 = customerList.addCustomer("Juan Garcia");
        String obj_l2 = customerList.generateNewLoyaltyCard();
        customerList.attachCardToCustomer(obj_id1, obj_l1);
        customerList.attachCardToCustomer(obj_id2, obj_l2);

        CustomerList same = new CustomerList();

        // add a few transactions
        same.attachCardToCustomer(obj_id1, obj_l1);
        same.attachCardToCustomer(obj_id2, obj_l2);

        AccountBook different = new AccountBook();

        // add a transaction
        different.addTransaction(new Debit(same.generateNewId(), LocalDate.now(), 20.0, OperationStatus.PAID));

        assertNotEquals(obj, null);
        assertNotEquals(obj, "boost coverage");

        assertEquals(obj, obj);

        assertEquals(obj, same);
        assertNotEquals(obj, different);

        assertEquals(obj.hashCode(), same.hashCode());
        assertNotEquals(obj.hashCode(), different.hashCode());
    }
}


package it.polito.ezshop.integrationTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.model.*;
import it.polito.ezshop.model.persistence.JsonInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TestJsonInterface {

    public static ProductType product;

    private static final String dataDirectory = "tmp/";

    private JsonInterface ji;

    @Before
    public void clean() throws Exception {
        product = new ProductType(1, "xx", "213124134135", 10.0, "xx");

        ji = JsonInterface.create(dataDirectory);
        ji.reset();
    }

    @After
    public void afterEach() throws IOException {
        ji.reset();
    }

    /**
     * Test reading and writing of a list of users.
     */
    @Test
    public void testReadWriteUsers() throws Exception {
        // write a null list
        ji.writeUsers(null);
        List<User> readData = ji.readUsers();
        assertEquals(0, readData.size());

        // write a list of users
        List<User> writeData = Arrays.asList(
                new User(1, "Marco", "abcd", Role.ADMINISTRATOR),
                new User(2, "Luca", "xyz", Role.SHOP_MANAGER),
                new User(3, "Pietro", "123", Role.CASHIER)
        );
        ji.writeUsers(writeData);

        // read a list of users
        readData = ji.readUsers();
        assertNotNull(readData);
        assertEquals(writeData.size(), readData.size());
        assertTrue(readData.containsAll(writeData));
    }

    /**
     * Test reading and writing of a list of products.
     */
    @Test
    public void testReadWriteProducts() throws Exception {
        // write a null list
        ji.writeProducts(null);
        List<ProductType> readData = ji.readProducts();
        assertEquals(0, readData.size());

        // write a list of products
        List<ProductType> writeData = Arrays.asList(
                new ProductType(1, "description1", "213124134135",
                        20.0, "note1", 0, new Position("1-1-1")),
                new ProductType(2, "description2", "213125134134",
                        10.0, "note2", 1, new Position("1-1-2")),
                new ProductType(3, "description3", "213125134196",
                        15.0, "note3", 0, new Position("1-1-3")),
                new ProductType(4, "description4", "2131251334199",
                        20.0, "note4", 1, new Position("1-1-4"))
        );
        ji.writeProducts(writeData);

        // read a list of products
        readData = ji.readProducts();
        assertNotNull(readData);
        assertEquals(writeData.size(), readData.size());
        assertTrue(readData.containsAll(writeData));
    }

    /**
     * Test reading and writing of a customer list
     */
    @Test
    public void testReadWriteCustomerList() throws Exception {
        // write a null list
        ji.writeCustomerList(null);
        CustomerList readData = ji.readCustomerList();
        assertNotNull(readData);
        assertEquals(0, readData.getAllCustomers().size());

        // create a new customer list
        CustomerList writeData = new CustomerList();

        // create customers
        int cID1 = writeData.addCustomer("Pietro");
        int cID2 = writeData.addCustomer("Sarah");
        writeData.addCustomer("Ramona");

        // create cards
        String cCard1 = writeData.generateNewLoyaltyCard();
        String cCard2 = writeData.generateNewLoyaltyCard();
        String cCard3 = writeData.generateNewLoyaltyCard();

        // attach cards to customers
        writeData.attachCardToCustomer(cID1, cCard1);
        writeData.attachCardToCustomer(cID2, cCard2);

        // add points to card
        writeData.modifyPointsOnCard(cCard1, 10);
        writeData.modifyPointsOnCard(cCard3, 20);

        ji.writeCustomerList(writeData);

        // read a list of customers
        readData = ji.readCustomerList();
        assertNotNull(readData);
        assertEquals(writeData, readData);

        // the card associated to customer 1 and the card stored in the list
        // should be the exact same object
        LoyaltyCard card1 = readData.loyaltyCards.stream()
                .filter(card -> card.getCode().equals(cCard1)).findFirst().orElse(null);
        assertSame(readData.getCustomer(cID1).getCard(), card1);
    }

    /**
     * Test reading and writing of an account book
     */
    @Test
    public void testReadWriteAccountBook() throws Exception {
        // write a null list to the persistence layer
        ji.writeAccountBook(null);
        AccountBook readData = ji.readAccountBook();
        // read the data from the persistence layer
        assertNotNull(readData);
        assertEquals(0.0, readData.getBalance(), TestHelpers.DOUBLE_COMPARISON_THRESHOLD);
        assertEquals(0, readData.getAllTransactions().size());

        // generate a new account book
        AccountBook writeData = new AccountBook();

        // add a sale transaction to the account book
        SaleTransaction s1 = new SaleTransaction(1, LocalDate.now());
        s1.addSaleTransactionItem(product, 10);
        writeData.addTransaction(s1);
        writeData.setTransactionStatus(1, OperationStatus.COMPLETED);

        // add a return transaction to the account book
        ReturnTransaction r1 = new ReturnTransaction(2, s1.getBalanceId(), LocalDate.now());
        r1.addReturnTransactionItem(product, 2, product.getPricePerUnit());
        writeData.addTransaction(r1);
        writeData.setTransactionStatus(2, OperationStatus.COMPLETED);

        // add credits and debits to the account book
        Credit credit = new Credit(3, LocalDate.now(), 10.0, OperationStatus.COMPLETED);
        Debit debit = new Debit(4, LocalDate.now(), 20.0, OperationStatus.COMPLETED);
        writeData.addTransaction(credit);
        writeData.addTransaction(debit);

        ji.writeAccountBook(writeData);

        // read an accountBook
        readData = ji.readAccountBook();
        assertNotNull(readData);
        assertEquals(writeData, readData);
    }
}

package unitTests;

import it.polito.ezshop.model.*;
import it.polito.ezshop.model.persistence.JsonInterface;
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
        product = new ProductType(1, "xx", "xx", 10.0, "xx");

        ji = JsonInterface.create(dataDirectory);
        ji.reset();
    }

    /**
     * Test reading and writing of a list of users.
     */
    @Test
    public void testReadWriteUsers() throws IOException {
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
                new ProductType(1, "description1", "123",
                        20.0, "note1", 0, null),
                new ProductType(2, "description2", "456",
                        10.0, "note2", 1, null),
                new ProductType(3, "description3", "789",
                        15.0, "note3", 0, new Position("1-1-1")),
                new ProductType(4, "description4", "012",
                        20.0, "note4", 1, new Position("1-1-1"))
        );
        ji.writeProducts(writeData);

        // read a list of products
        readData = ji.readProducts();
        assertNotNull(readData);
        assertEquals(writeData.size(), readData.size());
        assertTrue(readData.containsAll(writeData));
    }

    /**
     * Test reading and writing of a list of customers
     */
    @Test
    public void testReadWriteCustomers() throws IOException {
        // write a null list
        ji.writeCustomers(null);
        List<Customer> readData = ji.readCustomers();
        assertEquals(0, readData.size());

        // write a list of customers
        List<Customer> writeData = Arrays.asList(
                new Customer("Pietro", "123", 1, 10),
                new Customer("Sarah", "456", 2, 0),
                new Customer("Sarah", "456", 2, 0)
        );
        ji.writeCustomers(writeData);

        // read a list of customers
        readData = ji.readCustomers();
        assertNotNull(readData);
        assertEquals(writeData.size(), readData.size());
        assertTrue(readData.containsAll(writeData));
    }

    /**
     * Test reading and writing of a list of loyalty cards
     */
    @Test
    public void testReadWriteLoyaltyCards() throws IOException {
        // write a null list
        ji.writeLoyaltyCards(null);
        List<LoyaltyCard> readData = ji.readLoyaltyCards();
        assertEquals(0, readData.size());

        // write a list of loyalty cards
        List<LoyaltyCard> writeData = Arrays.asList(
                new LoyaltyCard("012356789", 10),
                new LoyaltyCard("012356788", 10),
                new LoyaltyCard("012356787", 10)
        );
        ji.writeLoyaltyCards(writeData);

        // read a list of loyalty cards
        readData = ji.readLoyaltyCards();
        assertNotNull(readData);
        assertEquals(writeData.size(), readData.size());
        assertTrue(readData.containsAll(writeData));
    }

    /**
     * Test reading and writing of an account book
     */
    @Test
    public void testReadWriteAccountBook() throws IOException {
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
        s1.addSaleTransactionItem(product, 10, product.getPricePerUnit(), 0.0);
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

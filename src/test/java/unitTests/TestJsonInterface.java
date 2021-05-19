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
        product = new ProductType(1, "xx", "1234567890128", 10.0, "xx");

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
                new ProductType(1, "description1", "1234567890128",
                        20.0, "note1", 0, null),
                new ProductType(2, "description2", "1234567890128",
                        10.0, "note2", 1, new Position("1-1-1")),
                new ProductType(3, "description3", "1234567890128",
                        15.0, "note3", 0, new Position("1-1-1")),
                new ProductType(4, "description4", "1234567890128",
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
    public void testReadWriteCustomers() throws Exception {
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

package unitTests;

import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidProductDescriptionException;
import it.polito.ezshop.model.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import static junit.framework.TestCase.*;

public class TestAccountBook {

    private static final ProductType product;

    static {
        try {
            product = new ProductType(1, "xx", "123456789012", 5.0, "xx");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static final AccountBook accountBook = new AccountBook();
    private static final SaleTransaction saleTransaction1 = new SaleTransaction(1,
            LocalDate.now(),
            Collections.singletonList(new TicketEntry(product, 20, product.getPricePerUnit(), 0.0)),
            0.0);
    private static final SaleTransaction saleTransaction2 = new SaleTransaction(2,
            LocalDate.now(),
            Collections.singletonList(new TicketEntry(product, 10, product.getPricePerUnit(), 0.0)),
            0.1);
    private static final ReturnTransaction returnTransaction1 = new ReturnTransaction(3,
            saleTransaction1.getBalanceId(),
            LocalDate.now(),
            Collections.singletonList(new ReturnTransactionItem(product, 4, product.getPricePerUnit())));
    private static final ReturnTransaction returnTransaction2 = new ReturnTransaction(4,
            saleTransaction1.getBalanceId(),
            LocalDate.now(),
            Collections.singletonList(new ReturnTransactionItem(product, 7, product.getPricePerUnit())));

    private static final Credit credit = new Credit(5, LocalDate.now(), 150, OperationStatus.COMPLETED);
    private static final Debit debit = new Debit(6, LocalDate.now(), 33, OperationStatus.COMPLETED);
    private static final Order order1 = new Order(7, LocalDate.now(), "xx", 17, 1);
    private static final Order order2 = new Order(8, LocalDate.now(), "xx", 15, 1);

    private static final double initialBalance = 175;

    @Before
    public void beforeEach() throws Exception {

        // reset account book
        accountBook.reset();

        // add some transactions
        accountBook.addTransaction(saleTransaction1);
        accountBook.addTransaction(saleTransaction2);
        accountBook.addTransaction(returnTransaction1);
        accountBook.addTransaction(returnTransaction2);
        accountBook.addTransaction(credit);
        accountBook.addTransaction(debit);
        accountBook.addTransaction(order1);
        accountBook.addTransaction(order2);

        accountBook.setTransactionStatus(saleTransaction1.getBalanceId(), OperationStatus.COMPLETED);
        accountBook.setTransactionStatus(saleTransaction2.getBalanceId(), OperationStatus.COMPLETED);
        accountBook.setTransactionStatus(returnTransaction1.getBalanceId(), OperationStatus.COMPLETED);
        accountBook.setTransactionStatus(returnTransaction2.getBalanceId(), OperationStatus.COMPLETED);
        accountBook.setTransactionStatus(order1.getBalanceId(), OperationStatus.COMPLETED);
        accountBook.setTransactionStatus(order2.getBalanceId(), OperationStatus.COMPLETED);
    }

    /**
     * Test that getBalance returns correct balance
     */
    @Test
    public void testGetBalance() {
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that checkAvailability returns true iff the requested amount of funds is available
     */
    @Test
    public void testCheckAvailability() {
        assertTrue(accountBook.checkAvailability(initialBalance - 20));
        assertFalse(accountBook.checkAvailability(initialBalance + 20));
    }

    /**
     * Test that computeBalance recomputes the balance correctly
     */
    @Test
    public void testComputeBalance() {
        // add a completed credit transaction (does count towards balance)
        int transactionId = accountBook.generateNewId();
        double value = 20;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.COMPLETED));

        // balance did change
        assertEquals(initialBalance + value, accountBook.getBalance());

        // change credit's value even though it is completed, breaking the value
        double newValue = 30;
        accountBook.getTransaction(transactionId).setMoney(newValue);

        // balance didn't update
        assertEquals(initialBalance + value, accountBook.getBalance());

        // recompute balance
        accountBook.computeBalance();

        // balance is now correct again
        assertEquals(initialBalance + newValue, accountBook.getBalance());
    }

    /**
     * Test that any transaction can be returned given their ID
     */
    @Test
    public void testGetTransaction() {
        assertEquals(order1, accountBook.getTransaction(order1.getBalanceId()));
        assertEquals(credit, accountBook.getTransaction(credit.getBalanceId()));
    }

    /**
     * Test that null is returned if no transaction with the given transaction ID exists
     */
    @Test
    public void testGetTransactionNullIfDoesNotExist() {
        assertNull(accountBook.getTransaction(-1));
        assertNull(accountBook.getTransaction(2000));
    }

    /**
     * Test that all transactions are returned
     */
    @Test
    public void testGetAllTransactions() {
        assertEquals(8, accountBook.getAllTransactions().stream().distinct().count());
    }

    /**
     * Test that all credits are returned
     */
    @Test
    public void testGetCreditTransactions() {
        assertEquals(3, accountBook.getCreditTransactions().stream().distinct().count());
    }

    /**
     * Test that all debits are returned
     */
    @Test
    public void testGetDebitTransactions() {
        assertEquals(5, accountBook.getDebitTransactions().stream().distinct().count());
    }

    /**
     * Test that all sales are returned
     */
    @Test
    public void testGetSaleTransactions() {
        assertEquals(2, accountBook.getSaleTransactions().stream().distinct().count());
    }

    /**
     * Test that all return transactions are returned
     */
    @Test
    public void testGetReturnTransactions() {
        assertEquals(2, accountBook.getReturnTransactions().stream().distinct().count());
    }

    /**
     * Test that all orders are returned
     */
    @Test
    public void testGetOrders() {
        assertEquals(2, accountBook.getOrders().stream().distinct().count());
    }

    /**
     * Test that adding an open transaction does not increase balance
     */
    @Test
    public void testAddOpenTransactionDoesNotAffectBalance() {

        // add an open credit (does not count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 23, OperationStatus.OPEN));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance didn't change
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that adding a closed transaction does increase balance
     */
    @Test
    public void testAddClosedTransactionDoesNotAffectBalance() {

        // add a closed credit (does not count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 24, OperationStatus.CLOSED));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance didn't change
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that adding a paid transaction does increase balance
     */
    @Test
    public void testAddPaidTransactionDoesAffectBalance() {

        // add a paid credit (does count towards balance)
        int transactionId = accountBook.generateNewId();
        int value = 23;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.PAID));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance did change
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that adding a completed transaction does increase balance
     */
    @Test
    public void testAddCompletedTransactionDoesAffectBalance() {

        // add a completed credit (does count towards balance)
        int transactionId = accountBook.generateNewId();
        int value = 8;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.COMPLETED));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance did change
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that adding a sale transaction increases the balance
     */
    @Test
    public void testAddSaleTransactionIncreasesBalance() {

        // add a sale transaction (increases balance)
        int transactionId = accountBook.generateNewId();
        SaleTransaction sale = new SaleTransaction(transactionId,
                LocalDate.now(),
                Collections.singletonList(new TicketEntry(product, 1, product.getPricePerUnit(), 0.0)),
                0.0);
        accountBook.addTransaction(sale);
        accountBook.setTransactionStatus(sale.getBalanceId(), OperationStatus.COMPLETED);

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance was increased
        assertEquals(initialBalance + product.getPricePerUnit(), accountBook.getBalance());
    }

    /**
     * Test that adding a return transaction decreases the balance
     */
    @Test
    public void testAddReturnTransactionDecreasesBalance() {

        // add a return transaction (decreases balance)
        int transactionId = accountBook.generateNewId();
        ReturnTransaction returnTransaction = new ReturnTransaction(transactionId,
                saleTransaction1.getBalanceId(),
                LocalDate.now(),
                Collections.singletonList(new ReturnTransactionItem(product, 2, product.getPricePerUnit())));
        accountBook.addTransaction(returnTransaction);
        accountBook.setTransactionStatus(returnTransaction.getBalanceId(), OperationStatus.COMPLETED);

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance was decreased
        assertEquals(initialBalance - 2 * product.getPricePerUnit(), accountBook.getBalance());
    }

    /**
     * Test that adding an order decreases the balance
     */
    @Test
    public void testAddOrderDecreasesBalance() {

        // add an Order (decreases balance)
        int transactionId = accountBook.generateNewId();
        Order order = new Order(transactionId, LocalDate.now(), product.getBarCode(), product.getPricePerUnit(), 1);
        accountBook.addTransaction(order);
        accountBook.setTransactionStatus(order.getBalanceId(), OperationStatus.COMPLETED);

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance was decreased
        assertEquals(initialBalance - product.getPricePerUnit(), accountBook.getBalance());
    }

    /**
     * Test that adding a credit increases the balance
     */
    @Test
    public void testAddCreditIncreasesBalance() {

        // add a Debit (decreases balance)
        int transactionId = accountBook.generateNewId();
        double value = 13;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.COMPLETED));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance was decreased
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that adding a debit decreases the balance
     */
    @Test
    public void testAddDebitDecreasesBalance() {

        // add a Debit (decreases balance)
        int transactionId = accountBook.generateNewId();
        double value = 13;
        accountBook.addTransaction(new Debit(transactionId, LocalDate.now(), value, OperationStatus.COMPLETED));

        // transaction was added
        assertEquals(transactionId, accountBook.getTransaction(transactionId).getBalanceId());

        // balance was decreased
        assertEquals(initialBalance - value, accountBook.getBalance());
    }

    /**
     * Test that deleting an open transaction does not change the balance
     */
    @Test
    public void testDeleteOpenTransactionDoesNotAffectBalance() {

        // add an open credit (does not count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 12, OperationStatus.OPEN));

        // delete transaction
        accountBook.removeTransaction(transactionId);

        // transaction no longer exists
        assertNull(accountBook.getTransaction(transactionId));

        // balance didn't change
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that deleting a closed transaction does not change the balance
     */
    @Test
    public void testDeleteClosedTransactionDoesNotAffectBalance() {

        // add a closed credit (does not count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 67, OperationStatus.CLOSED));

        // delete transaction
        accountBook.removeTransaction(transactionId);

        // transaction no longer exists
        assertNull(accountBook.getTransaction(transactionId));

        // balance didn't change
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that deleting a paid transaction does change the balance
     */
    @Test
    public void testDeletePaidTransactionDoesAffectBalance() {

        // add a paid credit (does count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 23, OperationStatus.PAID));

        // delete transaction
        accountBook.removeTransaction(transactionId);

        // transaction no longer exists
        assertNull(accountBook.getTransaction(transactionId));

        // balance went back
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that deleting a completed transaction does change the balance
     */
    @Test
    public void testDeleteCompletedTransactionDoesAffectBalance() {

        // add a completed credit (does count towards balance)
        int transactionId = accountBook.generateNewId();
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), 1, OperationStatus.COMPLETED));

        // delete transaction
        accountBook.removeTransaction(transactionId);

        // transaction no longer exists
        assertNull(accountBook.getTransaction(transactionId));

        // balance went back
        assertEquals(initialBalance, accountBook.getBalance());
    }

    /**
     * Test that changing a transaction state from closed to paid affects the balance
     */
    @Test
    public void testChangeStateToPaidDoesAffectBalance() {

        // add a closed credit transaction (does not count towards balance)
        int transactionId = accountBook.generateNewId();
        int value = 10;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.CLOSED));

        // set status to paid
        accountBook.setTransactionStatus(transactionId, OperationStatus.PAID);

        // balance did change
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that changing a transaction state from paid to completed does not affect the balance
     */
    @Test
    public void testChangeStateToCompletedDoesNotAffectBalance() {

        // add a paid credit transaction (does count towards balance)
        int transactionId = accountBook.generateNewId();
        int value = 234;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.PAID));

        // set status to completed
        accountBook.setTransactionStatus(transactionId, OperationStatus.COMPLETED);

        // balance didn't change
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that changing a transaction state from paid to closed does affect the balance
     */
    @Test
    public void testChangeStateToClosedDoesAffectBalance() {

        // add a paid credit transaction (does count towards balance)
        int transactionId = accountBook.generateNewId();
        double value = 20;
        accountBook.addTransaction(new Credit(transactionId, LocalDate.now(), value, OperationStatus.PAID));

        // set status to completed
        accountBook.setTransactionStatus(transactionId, OperationStatus.COMPLETED);

        // balance didn't change
        assertEquals(initialBalance + value, accountBook.getBalance());
    }

    /**
     * Test that the generated ID is not assigned to any transaction yet
     */
    @Test
    public void testGenerateIdIsUnique() {
        assertNull(accountBook.getTransaction(accountBook.generateNewId()));
    }
}

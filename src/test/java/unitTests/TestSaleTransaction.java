package unitTests;

import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.model.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.*;

public class TestSaleTransaction {

    private final int id = 1;
    private final LocalDate date = LocalDate.now();
    private final double saleDiscountRate = 0.2;

    private final ProductType product1, product2;
    private final TicketEntry ticketEntry1;

    public TestSaleTransaction() throws Exception {
        product1 = new ProductType(1, "Coca Cola 1.5L", "123456789012", 1.5,
                "Lorem ipsum", 2, new Position("1-1-1"));
        product2 = new ProductType(1, "Coca Cola 1L", "12345678901231", 1.0,
                "Lorem ipsum", 2, new Position("1-1-1"));

        ticketEntry1 = new TicketEntry(product1, 1);
    }

    @Test
    public void testConstructor() throws InvalidDiscountRateException {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now());

        assertEquals(id, sale.getBalanceId());
        assertEquals(date, sale.getDate());
        assertNotNull(sale.getTransactionItems());
        assertEquals(0, sale.getTransactionItems().size());
        assertEquals(0.0, sale.getMoney(), 0.01);
        assertEquals(OperationStatus.OPEN, sale.getStatus());

        for (double discountRate : TestHelpers.invalidDiscountRates) {
            assertThrows(InvalidDiscountRateException.class, () -> new SaleTransaction(1,
                    LocalDate.now(), Collections.singletonList(ticketEntry1), discountRate));
        }

        sale = new SaleTransaction(1, LocalDate.now(), Collections.singletonList(ticketEntry1), saleDiscountRate);
        assertEquals(Collections.singletonList(ticketEntry1), sale.getTransactionItems());
        assertEquals(saleDiscountRate, sale.getDiscountRate(), 0.01);
        assertEquals((1 - saleDiscountRate) * ticketEntry1.computeTotal(), sale.computeTotal(), 0.01);
    }

    @Test
    public void testAddSaleTransactionItem() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, saleDiscountRate);

        // add a product
        sale.addSaleTransactionItem(product1, 1);
        assertEquals(1, sale.getTransactionItems().size());
        assertEquals(product1, sale.getTransactionItems().get(0).getProductType());
        assertEquals(1, sale.getTransactionItems().get(0).getAmount());
        assertEquals(product1.getPricePerUnit(), sale.getTransactionItems().get(0).getPricePerUnit(), 0.01);
        assertEquals(0.0, sale.getTransactionItems().get(0).getDiscountRate(), 0.01);

        // check the balance value is correct
        assertEquals((1 - saleDiscountRate) * product1.getPricePerUnit(), sale.getMoney(), 0.01);
        assertEquals(sale.getMoney(), sale.computeTotal(), 0.01);

        // add a product again
        sale.addSaleTransactionItem(product1, 2);
        assertEquals(1, sale.getTransactionItems().size());
        assertEquals(product1, sale.getTransactionItems().get(0).getProductType());
        assertEquals(3, sale.getTransactionItems().get(0).getAmount());
        assertEquals(product1.getPricePerUnit(), sale.getTransactionItems().get(0).getPricePerUnit(), 0.01);
        assertEquals(0.0, sale.getTransactionItems().get(0).getDiscountRate(), 0.01);

        sale.setStatus(OperationStatus.COMPLETED);

        assertThrows(IllegalStateException.class, () -> sale.addSaleTransactionItem(product1, 1));
    }

    @Test
    public void testRemoveSaleTransactionItem() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, saleDiscountRate);

        // add 10 units
        sale.addSaleTransactionItem(product1, 10);

        // remove 5 units
        assertTrue(sale.removeSaleTransactionItem(product1, 5));
        assertEquals(1, sale.getTransactionItems().size());
        assertEquals(5, sale.getTransactionItems().get(0).getAmount());

        // try to remove six units
        assertFalse(sale.removeSaleTransactionItem(product1, 6));

        // remove the last 5 units
        assertTrue(sale.removeSaleTransactionItem(product1, 5));
        assertEquals(0, sale.getTransactionItems().size());

        // add again 5 units
        sale.addSaleTransactionItem(product1, 1);
        assertEquals(1, sale.getTransactionItems().size());

        double total = sale.computeTotal();

        sale.setStatus(OperationStatus.COMPLETED);

        assertTrue(sale.removeSaleTransactionItem(product1, 1));
        assertEquals(0, sale.getTransactionItems().size());
        assertEquals(total, sale.getMoney(), 0.01);
        assertEquals(0.0, sale.computeTotal(), 0.01);

        // try to remove a non existing product
        assertFalse(sale.removeSaleTransactionItem(product2, 1));
    }

    @Test
    public void testApplyDiscountToProduct() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, saleDiscountRate);

        // add 10 units
        sale.addSaleTransactionItem(product1, 10);

        double total = sale.computeTotal();

        // apply a discount to a product in the sale
        assertTrue(sale.applyDiscountToProduct(product1.getBarCode(), 0.5));
        // try to apply a discount to a product not in the sale
        assertFalse(sale.applyDiscountToProduct(product2.getBarCode(), 0.5));

        // verify new total
        assertEquals((1 - 0.5) * total, sale.computeTotal(), 0.01);

        sale.setStatus(OperationStatus.COMPLETED);
        assertThrows(IllegalStateException.class, () -> sale.applyDiscountToProduct(product1.getBarCode(), 0.5));
    }

    @Test
    public void testSetDiscountRate() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, 0.0);

        // add 10 units
        sale.addSaleTransactionItem(product1, 10);

        for (double discountRate : TestHelpers.invalidDiscountRates) {
            assertThrows(InvalidDiscountRateException.class, () -> sale.setDiscountRate(discountRate));
        }

        // initial total
        double total = sale.computeTotal();

        sale.setDiscountRate(saleDiscountRate);
        assertEquals((1 - saleDiscountRate) * total, sale.computeTotal(), 0.01);

        // close the transaction
        sale.setStatus(OperationStatus.CLOSED);
        sale.setDiscountRate(0.5);
        assertEquals((1 - 0.5) * total, sale.computeTotal(), 0.01);

        sale.setStatus(OperationStatus.PAID);
        assertThrows(IllegalStateException.class, () -> sale.setDiscountRate(saleDiscountRate));
    }

    @Test
    public void testComputePoints() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, 0.0);

        // add 10 units
        sale.addSaleTransactionItem(product1, 10);

        assertEquals((int) ((10 * product1.getPricePerUnit()) / 10), sale.computePoints());
    }

    @Test
    public void testAddReturnTransaction() throws Exception {
        SaleTransaction sale = new SaleTransaction(1, LocalDate.now(), null, 0.0);

        sale.addReturnTransaction(new ReturnTransaction(sale.getBalanceId() + 1, sale.getBalanceId(), LocalDate.now()));

        // todo
    }

    @Test
    public void testValidateId() throws Exception {
        for (Integer id : TestHelpers.invalidTransactionIDs) {
            assertThrows(InvalidTransactionIdException.class, () -> SaleTransaction.validateId(id));
        }

        SaleTransaction.validateId(1);
    }

    @Test
    public void testEqualsHashCode() throws Exception {
        SaleTransaction obj = new SaleTransaction(1, LocalDate.now(), Collections.singletonList(ticketEntry1), 0.0);
        SaleTransaction same = new SaleTransaction(1, LocalDate.now(), Collections.singletonList(ticketEntry1), 0.0);
        SaleTransaction different = new SaleTransaction(1, LocalDate.now(), null, 0.5);

        assertNotEquals(obj, null);
        assertNotEquals(obj, "boost coverage");

        assertEquals(obj, obj);

        assertEquals(obj, same);
        assertNotEquals(obj, different);

        assertEquals(obj.hashCode(), same.hashCode());
        assertNotEquals(obj.hashCode(), different.hashCode());
    }

}

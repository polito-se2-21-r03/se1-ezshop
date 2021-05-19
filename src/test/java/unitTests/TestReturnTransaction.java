package unitTests;

import it.polito.ezshop.model.*;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.Assert.*;

public class TestReturnTransaction {

    private final ProductType product;
    private final ReturnTransactionItem returnTransactionItem;
    private final int returnId = 2;
    private final int saleId = 1;
    private final LocalDate date = LocalDate.now();

    public TestReturnTransaction() throws Exception {
        product = new ProductType(1, "Coca Cola 1.5L",
                "123456789012", 1.5, "Lorem ipsum", 2, new Position("1-1-1"));

        returnTransactionItem = new ReturnTransactionItem(product, 1, product.getPricePerUnit());
    }

    @Test
    public void testConstructor() {
        // invalid date
        assertThrows(NullPointerException.class, () -> new ReturnTransaction(returnId, saleId, null));

        // valid return transaction
        ReturnTransaction rt = new ReturnTransaction(returnId, saleId, date, Collections.singletonList(returnTransactionItem));

        assertEquals(returnId, rt.getBalanceId());
        assertEquals(saleId, rt.getSaleTransactionId());
        assertEquals(OperationStatus.OPEN, rt.getStatus());
        assertEquals(-returnTransactionItem.computeValue(), rt.getMoney(), 0.01);
        assertEquals(Collections.singletonList(returnTransactionItem), rt.getTransactionItems());

        // valid return transaction
        rt = new ReturnTransaction(returnId, saleId, date, null);

        assertEquals(0.0, rt.getMoney(), 0.01);
        assertEquals(Collections.emptyList(), rt.getTransactionItems());
    }

    @Test
    public void testAddReturnTransactionItem() {
        ReturnTransaction rt = new ReturnTransaction(returnId, saleId, date, null);

        rt.addReturnTransactionItem(product, 1, 1.0);
        assertEquals(1, rt.getTransactionItems().size());
        assertEquals(
                Collections.singletonList(new ReturnTransactionItem(product, 1, 1.0)),
                rt.getTransactionItems()
        );

        // complete the transaction
        rt.setStatus(OperationStatus.COMPLETED);
        rt.addReturnTransactionItem(product, 1, 1.0);
        // check that the products in the return transaction didn't change
        assertEquals(1, rt.getTransactionItems().size());
        assertEquals(
                Collections.singletonList(new ReturnTransactionItem(product, 1, 1.0)),
                rt.getTransactionItems()
        );
    }

    @Test
    public void testEqualsAndHashCode() {
        ReturnTransaction obj = new ReturnTransaction(returnId, saleId, date, Collections.singletonList(returnTransactionItem));
        ReturnTransaction same = new ReturnTransaction(returnId, saleId, date, Collections.singletonList(returnTransactionItem));
        ReturnTransaction different = new ReturnTransaction(returnId, saleId, date, null);

        assertEquals(obj, same);
        assertEquals(obj.hashCode(), same.hashCode());

        assertNotEquals(obj, different);
    }
}

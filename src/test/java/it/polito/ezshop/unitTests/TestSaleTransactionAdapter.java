package it.polito.ezshop.unitTests;

import it.polito.ezshop.TestHelpers;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.SaleTransaction;
import it.polito.ezshop.model.TicketEntry;
import it.polito.ezshop.model.adapters.SaleTransactionAdapter;
import it.polito.ezshop.model.adapters.TicketEntryAdapter;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestSaleTransactionAdapter {

    private final SaleTransaction sale;

    public TestSaleTransactionAdapter() throws Exception {
        ProductType product = new ProductType(1, "Coca Cola 1.5L", "123456789012", 1.5,
                "Lorem ipsum", 2, new Position("1-1-1"));

        TicketEntry ticketEntry = new TicketEntry(product, 1);
        double saleDiscountRate = 0.2;
        sale = new SaleTransaction(1, LocalDate.now(), Collections.singletonList(ticketEntry), saleDiscountRate);
    }

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new SaleTransactionAdapter(null));
    }

    @Test
    public void testSetters() {
        SaleTransactionAdapter adapter = new SaleTransactionAdapter(sale);

        // test get/set ticket number
        assertThrows(UnsupportedOperationException.class, () -> adapter.setTicketNumber(42));

        // test get/set entries
        assertThrows(UnsupportedOperationException.class, () -> adapter.setEntries(null));

        // test get/set discount rate
        for (double value : TestHelpers.invalidDiscountRates) {
            assertThrows(IllegalArgumentException.class, () -> adapter.setDiscountRate(value));
        }

        adapter.setDiscountRate(.2);
        assertEquals(.2, adapter.getDiscountRate(), 0.01);

        // test get/set price
        assertThrows(UnsupportedOperationException.class, () -> adapter.setPrice(42.0));
    }

    @Test
    public void testGetters() {
        SaleTransactionAdapter adapter = new SaleTransactionAdapter(sale);

        assertEquals((Integer) sale.getBalanceId(), adapter.getTicketNumber());
        assertEquals(
                sale.getTransactionItems().stream()
                        .map(TicketEntryAdapter::new)
                        .collect(Collectors.toList()),
                adapter.getEntries()
        );
        assertEquals(sale.getDiscountRate(), adapter.getDiscountRate(), 0.01);
        assertEquals(sale.computeTotal(), adapter.getPrice(), 0.01);
    }
}

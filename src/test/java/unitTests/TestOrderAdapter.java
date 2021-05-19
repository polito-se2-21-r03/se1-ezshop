package unitTests;

import it.polito.ezshop.model.OperationStatus;
import it.polito.ezshop.model.Order;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import it.polito.ezshop.model.adapters.OrderAdapter;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestOrderAdapter {

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new BalanceOperationAdapter(null));
    }

    @Test
    public void testSetters() {
        Order order = new Order(1, LocalDate.now(), "123456789012", 1.0, 1);
        OrderAdapter orderAdapter = new OrderAdapter(order);

        // try to update the balance id or the order id
        assertThrows(UnsupportedOperationException.class, () -> orderAdapter.setBalanceId(2));
        assertThrows(UnsupportedOperationException.class, () -> orderAdapter.setOrderId(2));

        // try to update the status
        assertThrows(UnsupportedOperationException.class, () -> orderAdapter.setStatus("PAYED"));

        // try to update the product code
        assertThrows(UnsupportedOperationException.class, () -> orderAdapter.setProductCode("12345678901231"));

        // update the price per unit
        orderAdapter.setPricePerUnit(42.0);
        assertEquals(42.0, orderAdapter.getPricePerUnit(), 0.01);

        // update the quantity
        orderAdapter.setQuantity(11);
        assertEquals(11, orderAdapter.getQuantity(), 0.01);
    }

    @Test
    public void testGetters () {
        Order order = new Order(1, LocalDate.now(), "123456789012", 1.0, 1);
        OrderAdapter orderAdapter = new OrderAdapter(order);

        assertEquals((Integer) order.getBalanceId(), orderAdapter.getBalanceId());
        assertEquals((Integer) order.getBalanceId(), orderAdapter.getOrderId());
        assertEquals(order.getQuantity(), orderAdapter.getQuantity());
        assertEquals(order.getPricePerUnit(), orderAdapter.getPricePerUnit(), 0.01);
        assertEquals(order.getProductCode(), orderAdapter.getProductCode());

        // check correct conversion of status
        assertEquals("ISSUED", orderAdapter.getStatus());
        order.setStatus(OperationStatus.COMPLETED);
        assertEquals("PAYED", orderAdapter.getStatus());
        order.setStatus(OperationStatus.OPEN);
        assertEquals("OPEN", orderAdapter.getStatus());
    }

}

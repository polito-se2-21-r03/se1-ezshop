package it.polito.ezshop.unitTests;

import it.polito.ezshop.model.*;
import it.polito.ezshop.model.adapters.BalanceOperationAdapter;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestBalanceOperationAdapter {

    @Test
    public void testConstructor() {
        assertThrows(NullPointerException.class, () -> new BalanceOperationAdapter(null));
    }

    @Test
    public void testSetters() {
        BalanceOperation credit = new Credit(1, LocalDate.now(), 10.0, OperationStatus.COMPLETED);
        BalanceOperationAdapter creditAdapter = new BalanceOperationAdapter(credit);

        // try to update the id
        assertThrows(UnsupportedOperationException.class, () -> creditAdapter.setBalanceId(2));

        // update the date
        creditAdapter.setDate(LocalDate.parse("2021-12-21"));
        assertEquals(LocalDate.parse("2021-12-21"), creditAdapter.getDate());

        // try to update the money
        assertThrows(UnsupportedOperationException.class, () -> creditAdapter.setMoney(20.0));

        // try to update the type
        assertThrows(UnsupportedOperationException.class, () -> creditAdapter.setType("DEBIT"));
    }

    @Test
    public void testGetters() {
        BalanceOperation credit = new Credit(1, LocalDate.now(), 10.0, OperationStatus.COMPLETED);
        BalanceOperationAdapter creditAdapter = new BalanceOperationAdapter(credit);

        assertEquals(credit.getBalanceId(), creditAdapter.getBalanceId());
        assertEquals(credit.getDate(), creditAdapter.getDate());
        assertEquals(credit.getMoney(), creditAdapter.getMoney(), 0.01);
        assertEquals("CREDIT", creditAdapter.getType());

        BalanceOperation debit = new Debit(1, LocalDate.now(), 10.0, OperationStatus.COMPLETED);
        BalanceOperationAdapter debitAdapter = new BalanceOperationAdapter(debit);
        assertEquals("DEBIT", debitAdapter.getType());

        BalanceOperation sale = new SaleTransaction(1, LocalDate.now());
        BalanceOperationAdapter saleAdapter = new BalanceOperationAdapter(sale);
        assertEquals("SALE", saleAdapter.getType());

        BalanceOperation order = new Order(1, LocalDate.now(), "123456789012", 1.0, 1);
        BalanceOperationAdapter orderAdapter = new BalanceOperationAdapter(order);
        assertEquals("ORDER", orderAdapter.getType());

        BalanceOperation returnTransaction = new ReturnTransaction(1, 2, LocalDate.now());
        BalanceOperationAdapter returnAdapter = new BalanceOperationAdapter(returnTransaction);
        assertEquals("RETURN", returnAdapter.getType());
    }
}

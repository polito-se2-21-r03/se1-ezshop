package it.polito.ezshop.unitTests;

import it.polito.ezshop.credit_card_circuit.CreditCard;
import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.Assert.*;

public class TestCreditCardCircuit {

    private static final String path = "CreditCards-tests.txt";

    private static final List<CreditCard> cards = Arrays.asList(
            new CreditCard("4485370086510891", 150.0),
            new CreditCard("5100293991053009", 10.0),
            new CreditCard("4716258050958645", 0.0)
    );

    @Before
    public void beforeEach() throws IOException {
        Files.copy(Paths.get(TextualCreditCardCircuit.CLEAN_TEXT_FILE), Paths.get(path), REPLACE_EXISTING);
    }

    @Before
    public void afterEach() throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }

    @Test
    public void testLoad () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        assertEquals(cards, circuit.getCreditCards());
    }

    @Test
    public void testCheckAvailability () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        // negative amount -> return false
        assertFalse(circuit.checkAvailability("4485370086510891", -1));

        // "4485370086510891" has initial balance = 150.0
        assertTrue(circuit.checkAvailability("4485370086510891", 0));
        assertTrue(circuit.checkAvailability("4485370086510891", 100));
        assertTrue(circuit.checkAvailability("4485370086510891", 150));
        assertFalse(circuit.checkAvailability("4485370086510891", 150.01));

        // "4716258050958645" has initial balance = 0.0
        assertFalse(circuit.checkAvailability("4716258050958645", 0.01));

        // non existing credit card
        assertFalse(circuit.checkAvailability("123", 0.01));
    }

    @Test
    public void testAddCredit () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        // add negative amount to "4485370086510891" -> return false
        assertFalse(circuit.addCredit("4485370086510891", -1));
        assertEquals(150.0, circuit.getBalance("4485370086510891"), 0.01);

        // add 10.0 to "4485370086510891" -> new balance is 160.0
        assertTrue(circuit.addCredit("4485370086510891", 10.0));
        assertEquals(160.0, circuit.getBalance("4485370086510891"), 0.01);

        // add 10.0 to non existing card
        assertFalse(circuit.addCredit(null, 10.0));
        assertFalse(circuit.addCredit("123", 10.0));
    }

    @Test
    public void testAddDebit () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        // remove negative amount to "4485370086510891" -> return false
        assertFalse(circuit.addDebit("4485370086510891", -0.01));
        assertEquals(150.0, circuit.getBalance("4485370086510891"), 0.01);

        // remove 10.0 from "4485370086510891" -> new balance is 140.0
        assertTrue(circuit.addDebit("4485370086510891", 10.0));
        assertEquals(140.0, circuit.getBalance("4485370086510891"), 0.01);

        // try to remove 0.01 from "4716258050958645" (initial balance 0.0)
        assertFalse(circuit.addDebit("4716258050958645", 0.01));
        assertEquals(0.0, circuit.getBalance("4716258050958645"), 0.01);

        // remove 10.0 from a non existing card
        assertFalse(circuit.addDebit(null, 10.0));
        assertFalse(circuit.addDebit("123", 10.0));
    }

    @Test
    public void testValidateCode () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        assertTrue(circuit.validateCode("4716258050958645"));
        assertFalse(circuit.validateCode("4716258050958641"));
    }

    @Test
    public void testReset () {
        TextualCreditCardCircuit circuit = new TextualCreditCardCircuit(path);

        circuit.addDebit("4485370086510891", 10.0);
        circuit.addCredit("5100293991053009", 10.0);
        circuit.addCredit("4716258050958645", 2.0);

        assertEquals(140.0, circuit.getBalance("4485370086510891"), 0.01);
        assertEquals(20.0, circuit.getBalance("5100293991053009"), 0.01);
        assertEquals(2.0, circuit.getBalance("4716258050958645"), 0.01);

        circuit.reset();

        assertEquals(150.0, circuit.getBalance("4485370086510891"), 0.01);
        assertEquals(10.0, circuit.getBalance("5100293991053009"), 0.01);
        assertEquals(0.0, circuit.getBalance("4716258050958645"), 0.01);
    }

}

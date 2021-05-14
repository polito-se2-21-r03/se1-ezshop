package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.utils.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestUtils {

    //<editor-fold desc="Tests on Utils.generateId">

    /**
     * If the list parameter is null, the method should
     * return a valid positive integer.
     */
    @Test
    public void testNullIDsList() {
        Integer id = Utils.generateId(null);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    /**
     * If the list parameter is an empty list, the method should
     * return a valid positive integer.
     */
    @Test
    public void testEmptyList() {
        Integer id = Utils.generateId(new ArrayList<>());
        assertNotNull(id);
        assertTrue(id > 0);
    }

    /**
     * If the list parameter is a non empty list, the method should
     * return a valid positive integer. The value should not be
     * already present in the list.
     */
    @Test
    public void test() {
        List<Integer> IDs = new ArrayList<>();

        Integer id = Utils.generateId(IDs);
        assertNotNull(id);
        assertTrue(id > 0);

        IDs.add(id);
        id = Utils.generateId(IDs);
        assertNotNull(id);
        assertTrue(id > 0);
        assertFalse(IDs.contains(id));
    }

    //</editor-fold>

    //<editor-fold desc="Tests on Utils.isValidBarcode">

    /**
     * If the barcode parameter is null, the method should return false.
     */
    @Test
    public void testNullBarcode() {
        assertFalse(Utils.isValidBarcode(null));
    }

    /**
     * If the length of the barcode parameter is less than 12,
     * the method should return false.
     */
    @Test
    public void testBarcodeTooShort() {
        assertFalse(Utils.isValidBarcode(""));
        // 11 characters long string
        assertFalse(Utils.isValidBarcode("99999999999"));
    }

    /**
     * If the length of the barcode parameter is bigger than 14,
     * the method should return false.
     */
    @Test
    public void testBarcodeTooLong() {
        // 15 characters long string
        assertFalse(Utils.isValidBarcode("000000000000000"));
        assertFalse(Utils.isValidBarcode("999999999999999"));
    }

    /**
     * If the barcode parameter is not numeric,
     * the method should return false.
     */
    @Test
    public void testNonNumericBarcode() {
        assertFalse(Utils.isValidBarcode(""));
        assertFalse(Utils.isValidBarcode("A00000000000"));
        assertFalse(Utils.isValidBarcode("00000000000A"));
    }

    /**
     * If the check digit of the barcode is wrong,
     * the method should return false.
     */
    @Test
    public void testWrongCheckDigit() {
        assertFalse(Utils.isValidBarcode(""));

        // GTIN12: the last digit should be 2
        assertFalse(Utils.isValidBarcode("123456789011"));
        assertFalse(Utils.isValidBarcode("123456789013"));

        // GTIN13: the last digit should be 8
        assertFalse(Utils.isValidBarcode("1234567890125"));
        assertFalse(Utils.isValidBarcode("1234567890127"));

        // GTIN14: the last digit should be 1
        assertFalse(Utils.isValidBarcode("12345678901234"));
        assertFalse(Utils.isValidBarcode("12345678901239"));
    }

    /**
     * If the last digit is correct,
     * the method should return false.
     */
    @Test
    public void testValidBarcodes() {
        assertFalse(Utils.isValidBarcode(""));

        // GTIN12: the last digit should be 2
        assertTrue(Utils.isValidBarcode("123456789012"));

        // GTIN13: the last digit should be 8
        assertTrue(Utils.isValidBarcode("1234567890128"));

        // GTIN14: the last digit should be 1
        assertTrue(Utils.isValidBarcode("12345678901231"));
    }

    /**
     * White box test
     * Check barcodes with non alphanumeric characters.
     */
    @Test
    public void testNonAlphanumericBarcode () {
        assertFalse(Utils.isValidBarcode("$2345678901231"));
        assertFalse(Utils.isValidBarcode("1234567890123$"));
    }

    //</editor-fold>
}

package unitTests;

import it.polito.ezshop.utils.Utils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestUtilsIsValidBarcode {

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
        assertFalse(Utils.isValidBarcode("0A0000000000"));
        assertFalse(Utils.isValidBarcode("00A000000000"));
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

}

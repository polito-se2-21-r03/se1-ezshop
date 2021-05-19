package unitTests;

import org.junit.Test;

import static it.polito.ezshop.utils.Utils.isValidCreditCardNumber;
import static it.polito.ezshop.utils.Utils.luhnValidate;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class TestUtilsIsValidCreditCardNumber {

    /**
     * Tests that passing null as the credit card number returns false
     */
    @Test
    public void testCardNumberNullReturnsFalse() {
        assertFalse(isValidCreditCardNumber(null));
    }

    /**
     * Tests that passing an empty string as the card number returns false
     */
    @Test
    public void testCardNumberEmptyStringReturnsFalse() {
        assertFalse(isValidCreditCardNumber(""));
    }

    /**
     * Tests that a card number containing non-numeric characters returns false
     */
    @Test
    public void testCardNumberContainsNonNumericCharReturnsFalse() {
        assertFalse(isValidCreditCardNumber("135895499391449a"));
    }

    /**
     * Tests that a card number with a correct checksum but less than 16 digits returns false
     */
    @Test
    public void testCardNumberTooShortReturnsFalse() {
        // 14 digits
        // 1  4  5  8  9  5  4  9  9  3  9  1  4  4
        // 2  4  10 8  18 5  8  9  18 3  18 1  8  4
        // 2  4  1  8  9  5  8  9  9  3  9  1  8  4
        // 80 % 10 = 0
        assertFalse(isValidCreditCardNumber("13589549939144"));
    }

    /**
     * Tests that a card number with a correct checksum but more than 16 digits returns false
     */
    @Test
    public void testCardNumberTooLongReturnsFalse() {
        // 14 digits
        // 1  4  5  8  9  5  4  9  9  3  9  1  4  4  3  5  2  5
        // 2  4  10 8  18 5  8  9  18 3  18 1  8  4  6  5  4  5
        // 2  4  1  8  9  5  8  9  9  3  9  1  8  4  6  5  4  5
        // 100 % 10 = 0
        assertFalse(isValidCreditCardNumber("135895499391443525"));
    }

    /**
     * Tests that a card number of correct format but wrong checksum returns false
     */
    @Test
    public void testCardNumberWrongChecksumReturnsFalse() {
        assertFalse(isValidCreditCardNumber("1358954993914436"));
    }

    /**
     * Tests that a valid card number returns true
     */
    @Test
    public void testValidCardNumberReturnsTrue() {
        // 1  3  5  8  9  5  4  9  9  3  9  1  4  4  3  5
        // 2  3  10 8  18 5  8  9  18 3  18 1  8  4  6  5
        // 2  3  1  8  9  5  8  9  9  3  9  1  8  4  6  5
        // 90 % 10 = 0
        assertTrue(isValidCreditCardNumber("1358954993914435"));
    }

    @Test
    public void testLuhnValidateZeroIterations() {
        assertTrue(luhnValidate(""));
    }

    @Test
    public void testLuhnValidateOneIteration() {
        assertTrue(luhnValidate("0"));
    }

    @Test
    public void testLuhnValidateMultipleIterations() {
        assertTrue(luhnValidate("18"));
        assertTrue(luhnValidate("182"));
        assertTrue(luhnValidate("1826"));
    }
}

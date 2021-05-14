# Unit Testing Documentation

Authors:

Date:

Version:

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)




- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

 ### **Class *Utils* - method *isValidBarcode***

**Criteria for method *isValidBarcode*:**
	
 - Validity of the barcode parameter
 - Length of the barcode parameter
 - Format of the barcode parameter
 - Check digit

**Predicates for method *isValidBarcode*:**

| Criteria                           | Predicate      |
| ---------------------------------- | -------------- |
| Validity of the barcode parameter  | Valid          |
|                                    | null           |
| Length of the barcode parameter    | [0, 11]        |
|                                    | [12, 14]       |
|                                    | [15, MAX_INT)  |
| Format of the barcode parameter    | Numeric        |
|                                    | Non-numeric    |
| Check digit                        | Correct        |
|                                    | Incorrect      |

**Boundaries**:

| Criteria                                 | Boundary values    |
| ---------------------------------------- | ------------------ |
| Length of the barcode parameter          | 0, 11, 12, 14, 15  |

**Combination of predicates**:


| Validity of the barcode parameter | Length of the barcode parameter | Format of the barcode parameter | Check digit | Valid / Invalid | Description of the test case                                                                | JUnit test case                               |
|-----------------------------------|---------------------------------|---------------------------------|-------------|-----------------|---------------------------------------------------------------------------------------------|-----------------------------------------------|
| null                              | *                               | *                               | *           | Invalid         | isValidBarcode(null) -> false                                                               | TestUtilsIsValidBarcode.testNullBarcode       |
| Valid                             | [0, 11]                         | *                               | *           | Invalid         | isValidBarcode("") -> false <br> isValidBarcode("00000000000") -> false                     | TestUtilsIsValidBarcode.testBarcodeTooShort   |
| "                                 | [15, MAX_INT]                   | *                               | *           | Invalid         | isValidBarcode("000000000000000") -> false <br> isValidBarcode("999999999999999") -> false  | TestUtilsIsValidBarcode.testBarcodeTooLong    |
| "                                 | [12, 14]                        | Non-numeric                     | *           | Invalid         | isValidBarcode("A00000000000") -> false <br> isValidBarcode("00000000000A") -> false        | TestUtilsIsValidBarcode.testNonNumericBarcode |
| "                                 | "                               | Numeric                         | Incorrect   | Valid           | isValidBarcode("123456789011") -> false <br> isValidBarcode("123456789013") -> false        | TestUtilsIsValidBarcode.testWrongCheckDigit   |
| "                                 | "                               | Numeric                         | Correct     | Valid           | isValidBarcode("123456789012") -> true <br> isValidBarcode("1234567890128") -> true         | TestUtilsIsValidBarcode.testValidBarcodes     |


### **Class *Utils* - method *isValidCreditCard***

**Criteria for method *isValidCreditCard*:**

- Validity of the credit card parameter
- Length of the credit card parameter
- Format of the credit card parameter
- Checksum

**Predicates for method *isValidCreditCard*:**

| Criteria                           | Predicate      |
| ---------------------------------- | -------------- |
| Validity of the credit card parameter | Valid          |
|                                    | null           |
| Length of the credit card parameter| [0, 15]        |
|                                    | [16]       |
|                                    | [17, MAX_INT)  |
| Format of the credit card parameter| Numeric        |
|                                    | Non-numeric    |
| Checksum                           | Correct        |
|                                    | Incorrect      |

**Boundaries**:

| Criteria                                 | Boundary values    |
| ---------------------------------------- | ------------------ |
| Length of the credit card parameter      | 0, 15, 16  |

**Combination of predicates**:


| Validity of the credit card parameter | Length of the credit card parameter | Format of the credit card parameter | Checksum | Valid / Invalid | Description of the test case                                                                | JUnit test case                               |
|---------------------------------------|-------------------------------------|-------------------------------------|----------|-----------------|---------------------------------------------------------------------------------------------|-----------------------------------------------|
| null                                  | *                                   | *                                   | *        | Invalid         | isValidCreditCard(null) -> false                                                               | TestIsValidCreditCardNumber.testCardNumberNullReturnsFalse       |
| Valid                                 | [0]                                 | *                                   | *        | Invalid         | isValidCreditCard("") -> false <br> isValidCreditCard("13589549939144") -> false               | TestIsValidCreditCardNumber.testCardNumberEmptyStringReturnsFalse   |
| Valid                                 | [1, 15]                             | *                                   | *        | Invalid         | isValidCreditCard("") -> false <br> isValidCreditCard("13589549939144") -> false               | TestIsValidCreditCardNumber.testCardNumberTooShortReturnsFalse   |
| "                                     | [17, MAX_INT]                       | *                                   | *        | Invalid         | isValidCreditCard("135895499391443525") -> false                                               | TestIsValidCreditCardNumber.testCardNumberTooLongReturnsFalse    |
| "                                     | [16]                                | Non-numeric                         | *        | Invalid         | isValidCreditCard("135895499391449a") -> false                                                 | TestIsValidCreditCardNumber.testCardNumberContainsNonNumericCharReturnsFalse |
| "                                     | "                                   | Numeric                             | Incorrect| Valid           | isValidCreditCard("1358954993914492") -> false                                                 | TestIsValidCreditCardNumber.testCardNumberWrongChecksumReturnsFalse   |
| "                                     | "                                   | Numeric                             | Correct  | Valid           | isValidCreditCard("1358954993914491") -> true                                                  | TestIsValidCreditCardNumber.testValidCardNumberReturnsTrue     |



# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name            | JUnit test case |
|----------------------|-----------------|
| Utils.isValidBarcode | TestUtils.testNonAlphanumericBarcode |
|||
||||

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis

    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|||||
|||||
||||||




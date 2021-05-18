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



### **Class *User* - method *Constructor***

**Criteria for method *Constructor*:**

- Id parameter
- Username parameter
- Password parameter
- Role parameter

**Predicates for method *Constructor*:**

| Criteria           | Predicate                                                      |
|--------------------|----------------------------------------------------------------|
| Id parameter       | Null                                                           |
|                    | (-MIN_INT, 0]                                                  |
|                    | [1, MAX_INT)                                                   |
| Username parameter | Null or empty string                                           |
|                    | Valid                                                          |
| Password parameter | Null or empty string                                           |
|                    | Valid                                                          |
| Role parameter     | Null or empty string                                           |
|                    | Invalid                                                        |
|                    | Valid (one of "Administrator", "ShopManager", "Cashier")       |

**Boundaries**:

| Criteria           | Boundary values                                                |
|--------------------|----------------------------------------------------------------|
| Id parameter       | -1, 0, 1                                                       |

**Combination of predicates**:

| Id parameter  | Username parameter   | Password parameter   | Role parameter       | Valid/Invalid | Description of the test case                                                      | JUnit test case          |
|---------------|----------------------|----------------------|----------------------|---------------|-----------------------------------------------------------------------------------|--------------------------|
| Null          | *                    | *                    | *                    | Invalid       | new User(null, "username", "password", "Administrator") -> InvalidUserIdException | TestUser.testConstructor |
| (-MIN_INT, 0] | *                    | *                    | *                    | Invalid       | new User(-1, "username", "password", "Administrator") -> InvalidUserIdException   | TestUser.testConstructor |
| [1, MAX_INT)  | Null or empty string | *                    | *                    | Invalid       | new User(1, null, "password", "Administrator") -> InvalidUsernameException        | TestUser.testConstructor |
| "             | Valid                | Null or empty string | *                    | Invalid       | new User(1, "username", null, "Administrator") -> InvalidPasswordException        | TestUser.testConstructor |
| "             | "                    | Valid                | Null or empty string | Invalid       | new User(1, "username", "password", null) -> InvalidRoleException<br>new User(1, "username", "password", "") -> InvalidRoleException              | TestUser.testConstructor |
| "             | "                    | "                    | Invalid              | Invalid       | new User(1, "username", "password", "Admin") -> InvalidRoleException              | TestUser.testConstructor |
| "             | "                    | "                    | Valid                | Valid         | u = new User(1, "username", "password", "Administrator") <br>assert u.getId() == 1<br>assert u.getUsername() == "username"<br>assert u.getPassword() == "password"<br>assert u.getRole == "Administrator" | TestUser.testConstructor |


### **Class *LoyaltyCard* - method *Constructor***

**Criteria for method *Constructor*:**

- Code parameter
- Number of points

**Predicates for method *Constructor*:**

| Criteria           | Predicate                                                      |
|--------------------|----------------------------------------------------------------|
| Code parameter     | Invalid (according to LoyaltyCard.validateCode)                |
|                    | Valid                                                          |
| Number of points   | (-MIN_INT, 0)                                                  |
|                    | [0, MAX_INT)                                                   |

**Boundaries**:

| Criteria           | Boundary values                                                |
|--------------------|----------------------------------------------------------------|
| Number of points   | -1, 0, 1                                                       |

**Combination of predicates**:

| Code parameter          | Number of points     | Valid/Invalid | Description of the test case                                                                                                | JUnit test case                 |
|-------------------------|----------------------|---------------|-----------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| Invalid                 | *                    | Invalid       | new LoyaltyCard(null, 10.0) -> InvalidCustomerCardException<br>new LoyaltyCard("123", 10.0) -> InvalidCustomerCardException | TestLoyaltyCard.testConstructor |
| Valid                   | (-MIN_INT, 0)        | Invalid       | new LoyaltyCard("1234567890", -1) -> IllegalArgumentException                                                               | TestLoyaltyCard.testConstructor |
| "                       | [0, MAX_INT)         | Valid         | new LoyaltyCard("1234567890", 10) -> ok                                                                                     | TestLoyaltyCard.testConstructor |

*Similar tests are performed on each setter method of **LoyaltyCard***


### **Class *LoyaltyCard* - method *validateCode***

**Criteria for method *validateCode*:**

- Code parameter

**Predicates for method *validateCode*:**

| Criteria           | Predicate                                                      |
|--------------------|----------------------------------------------------------------|
| Code parameter     | Null or empty string                                           |
|                    | Invalid                                                        |
|                    | Valid (10 characters long numeric value)                       |

**Boundaries**:

*none*


**Combination of predicates**:

| Code parameter          | Valid/Invalid | Description of the test case                                                                                                   | JUnit test case                 |
|-------------------------|---------------|--------------------------------------------------------------------------------------------------------------------------------|---------------------------------|
| Null or empty string    | Invalid       | LoyaltyCard.validateCode(null) -> InvalidCustomerCardException<br>LoyaltyCard.validateCode("") -> InvalidCustomerCardException | TestLoyaltyCard.testValidateCode |
| Invalid                 | Invalid       | LoyaltyCard.validateCode("123") -> InvalidCustomerCardException<br>LoyaltyCard.validateCode("123456789A") -> InvalidCustomerCardException<br>LoyaltyCard.validateCode("123456789123") -> InvalidCustomerCardException | TestLoyaltyCard.testValidateCode |
| Valid                   | Valid         | LoyaltyCard.validateCode("1234567890") -> ok                                                                                   | TestLoyaltyCard.testValidateCode |


# White Box Unit Tests

### Test cases definition
    
    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name            | JUnit test case |
|----------------------|-----------------|
| User                 | TestUser.testConstructor |
|                      | TestUser.testSetId |
|                      | TestUser.testSetUsername |
|                      | TestUser.testSetPassword |
|                      | TestUser.testValidateId |
|                      | TestUser.testValidateUsername |
|                      | TestUser.testValidatePassword |
|                      | TestUser.testValidateRole |
|                      | TestUser.testEqualsHashCode |
| UserAdapter          | TestUserAdapter.testConstructor |
|                      | TestUserAdapter.testSetters |

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




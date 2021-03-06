# Integration and API Test Documentation

Authors: Can Karacomak (s287864), Alessandro Loconsolo (s244961), Julian Neubert (s288423), Simone Alberto Peirone (s286886)

Date: 26/05/2021

Version: 1.0

# Contents

- [Dependency graph](#dependency graph)

- [Integration approach](#integration)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph

![Dependency graph](images/dep_graph.png)

# Integration approach

The integration tests are performed using a bottom up approach. Starting from the leaf classes tested in the previous
development phase, the first step tests the intermediate classes AccountBook and CustomerList (**step 1**). Then, the class
responsible for data persistence is tested (**step 2**). As a last step, all the methods implemented in EZShop are tested
(**step 3**). Since these methods are highly dependent one to the other, this last phase follows a mixed approach: all the
methods are tested at the same time.

#  Tests

## Step 0: Unit tests

| Classes  | JUnit test cases |
|----------|------------------|
| it.polito.ezshop.model.adapters.BalanceOperationAdapter | it.polito.ezshop.unitTests.TestBalanceOperationAdapter.* |
| it.polito.ezshop.model.CreditCard | it.polito.ezshop.unitTests.TestCreditCard.* |
| it.polito.ezshop.model.CreditCardCircuit | it.polito.ezshop.unitTests.TestCreditCardCircuit.* |
| it.polito.ezshop.model.Customer | it.polito.ezshop.unitTests.TestCustomer.* |
| it.polito.ezshop.model.adapters.CustomerAdapter | it.polito.ezshop.unitTests.TestCustomerAdapter.* |
| it.polito.ezshop.model.LoyaltyCard | it.polito.ezshop.unitTests.TestLoyaltyCard.* |
| it.polito.ezshop.model.OperationStatus | it.polito.ezshop.unitTests.TestOperationStatus.* |
| it.polito.ezshop.model.Order | it.polito.ezshop.unitTests.TestOrder.* |
| it.polito.ezshop.model.adapters.OrderAdapter | it.polito.ezshop.unitTests.TestOrderAdapter.* |
| it.polito.ezshop.model.Position | it.polito.ezshop.unitTests.TestPosition.* |
| it.polito.ezshop.model.ProductType | it.polito.ezshop.unitTests.TestProductType.* |
| it.polito.ezshop.model.adapters.ProductTypeAdapter | it.polito.ezshop.unitTests.TestProductTypeAdapter.* |
| it.polito.ezshop.model.ReturnTransaction | it.polito.ezshop.unitTests.TestReturnTransaction.* |
| it.polito.ezshop.model.ReturnTransactionItem | it.polito.ezshop.unitTests.TestReturnTransactionItem.* |
| it.polito.ezshop.model.SaleTransaction | it.polito.ezshop.unitTests.TestSaleTransaction.* |
| it.polito.ezshop.model.adapters.SaleTransactionAdapter | it.polito.ezshop.unitTests.TestSaleTransactionAdapter.* |
| it.polito.ezshop.model.TicketEntry |  it.polito.ezshop.unitTests.TestTicketEntry.* |
| it.polito.ezshop.model.adapters.TicketEntryAdapter | it.polito.ezshop.unitTests.TestTicketEntryAdapter.* |
| it.polito.ezshop.model.User | it.polito.ezshop.unitTests.TestUser.* |
| it.polito.ezshop.model.adapters.UserAdapter | it.polito.ezshop.unitTests.TestUserAdapter.* |
| it.polito.ezshop.model.Utils |  it.polito.ezshop.unitTests.TestUtilsIsValidBarcode.* |
| |  it.polito.ezshop.unitTests.TestUtilsIsValidCreditCardNumber.* |

## Step 1: AccountBook + CustomerList

| Classes  | JUnit test cases |
|----------|------------------|
| it.polito.ezshop.model.AccountBook | it.polito.ezshop.integrationTests.TestAccountBook.* |
| it.polito.ezshop.model.CustomerList | it.polito.ezshop.integrationTests.testCustomerList.* |


## Step 2: JsonInterface

| Classes  | JUnit test cases |
|----------|------------------|
| it.polito.ezshop.model.persistence.JsonInterface | it.polito.ezshop.integrationTests.TestJsonInterface.* |


## Step 3: API testing


| Classes  | JUnit test cases |
|----------|------------------|
| it.polito.ezshop.data.EZShop | it.polito.ezshop.apiTests.EZShopTestCreateProductType.* |
| | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.* |
| | it.polito.ezshop.apiTests.EZShopTestGetAllProductTypes.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteProductType.* |
| | it.polito.ezshop.apiTests.EZShopTestGetProductTypeByBarCode.* |
| | it.polito.ezshop.apiTests.EZShopTestGetProductTypesByDescription.* |
| | it.polito.ezshop.apiTests.EZShopTestUpdateQuantity.* |
| | it.polito.ezshop.apiTests.EZShopTestUpdatePosition.* |
| | it.polito.ezshop.apiTests.EZShopTestIssueOrder.* |
| | it.polito.ezshop.apiTests.EZShopTestPayOrderFor.* |
| | it.polito.ezshop.apiTests.EZShopTestPayOrder.* |
| | it.polito.ezshop.apiTests.EZShopTestRecordOrderArrival.* |
| | it.polito.ezshop.apiTests.EZShopTestGetAllOrders.* |
| | it.polito.ezshop.apiTests.EZShopTestDefineCustomer.* |
| | it.polito.ezshop.apiTests.EZShopTestModifyCustomer.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteCustomer.* |
| | it.polito.ezshop.apiTests.EZShopTestGetCustomer.* |
| | it.polito.ezshop.apiTests.EZShopTestGetAllCustomers.* |
| | it.polito.ezshop.apiTests.EZShopTestCreateCard.* |
| | it.polito.ezshop.apiTests.EZShopTestAttachCardToCustomer.* |
| | it.polito.ezshop.apiTests.EZShopTestModifyPointsOnCard.* |
| | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteProductFromSale.* |
| | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToProduct.* |
| | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToSale.* |
| | it.polito.ezshop.apiTests.EZShopTestComputePointsForSale.* |
| | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteSaleTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestGetSaleTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestStartReturnTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestReturnProduct.* |
| | it.polito.ezshop.apiTests.EZShopTestEndReturnTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteReturnTransaction.* |
| | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.* |
| | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.* |
| | it.polito.ezshop.apiTests.EZShopTestReturnCashPayment.* |
| | it.polito.ezshop.apiTests.EZShopTestReturnCreditCardPayment.* |
| | it.polito.ezshop.apiTests.EZShopTestRecordBalanceUpdate.* |
| | it.polito.ezshop.apiTests.EZShopTestGetCreditsAndDebits.* |
| | it.polito.ezshop.apiTests.EZShopTestComputeBalance.* |
| | it.polito.ezshop.apiTests.EZShopTestCreateUser.* |
| | it.polito.ezshop.apiTests.EZShopTestDeleteUser.* |
| | it.polito.ezshop.apiTests.EZShopTestGetUser.* |
| | it.polito.ezshop.apiTests.EZShopTestGetAllUsers.* |
| | it.polito.ezshop.apiTests.EZShopTestUpdateUserRights.* |
| | it.polito.ezshop.apiTests.EZShopTestLogin.* |
| | it.polito.ezshop.apiTests.EZShopTestLogout.* |
| | it.polito.ezshop.apiTests.EZShopTestReset.* |




# Scenarios

*No additional scenarios added*


# Coverage of Scenarios and FR

| Scenario ID | Functional Requirements covered | JUnit  Test(s) |
| ----------- | ------------------------------- | ----------- |
| 1.1         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestCreateProductType.testCreateProductTypeSuccessfully |             
| 1.2         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
| 1.3         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
| 2.1         | FR1.1                           | it.polito.ezshop.apiTests.EZShopTestCreateUser.testCreateUserSuccesfully |
| 2.2         | FR1.3                           | it.polito.ezshop.apiTests.EZShopTestGetAllUsers.testGetUsersSuccessfully |
|             | FR1.4                           | it.polito.ezshop.apiTests.EZShopTestGetUser.testUserReturnedSuccessfully |
|             | FR1.2                           | it.polito.ezshop.apiTests.EZShopTestDeleteUser.testDeleteUserSuccessfully |
| 2.3         | FR1.3                           | it.polito.ezshop.apiTests.EZShopTestGetAllUsers.testGetUsersSuccessfully |
|             | FR1.4                           | it.polito.ezshop.apiTests.EZShopTestGetUser.testUserReturnedSuccessfully |
|             | FR1.1                           | it.polito.ezshop.apiTests.EZShopTestUpdateUserRights.testUpdateUserRightsFinishSuccessfully |
| 3.1         | FR4.3                           | it.polito.ezshop.apiTests.EZShopTestIssueOrder.testValidIssueOrder |
| 3.2         | FR4.4                           | it.polito.ezshop.apiTests.EZShopTestPayOrderFor.testValidPayOrderFor |
| 3.3         | FR4.7                           | it.polito.ezshop.apiTests.EZShopTestRecordOrderArrival.testValidRecordOrderArrival |
| 4.1         | FR5.1                           | it.polito.ezshop.apiTests.EZShopTestDefineCustomer.testDefineManyCustomersSuccessfully |
| 4.2         | FR5.5                           | it.polito.ezshop.apiTests.EZShopTestCreateCard.testValidCardNumberReturned |
|             | FR5.6                           | it.polito.ezshop.apiTests.EZShopTestAttachCardToCustomer.testAttachCardsToCustomers |
| 4.3         | FR5.1/FR5.6                     | it.polito.ezshop.apiTests.EZShopTestModifyCustomer.testDetachCardFromCustomer |
| 4.4         | FR5.1                           | it.polito.ezshop.apiTests.EZShopTestModifyCustomer.* |
| 5.1         | FR1.5                           | it.polito.ezshop.apiTests.EZShopTestLogin.testLoginFinishSuccessfully |
| 5.2         | FR1.5                           | it.polito.ezshop.apiTests.EZShopTestLogin.testLoginFailed |
| 6.1         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR7.1, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             | FR7.2, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.2         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.5                           | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToProduct.testApplyDiscountRateSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR7.1, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             | FR7.2, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.3         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.4                           | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToSale.testApplyDiscountRateSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR7.2, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.4         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR6.6                           | it.polito.ezshop.apiTests.EZShopTestComputePointsForSale.testComputePointsForSaleSuccessfully |
|             | FR7.1, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             | FR7.2, FR6.11                   | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
|             | FR5.7                           | it.polito.ezshop.apiTests.EZShopTestModifyPointsOnCard.testAddPointsToCard |
| 6.5         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR6.15                          | it.polito.ezshop.apiTests.EZShopTestDeleteSaleTransaction.testDeleteSaleTransactionSuccessfully |
| 6.6         | FR6.1                           | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             | FR6.2                           | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             | FR6.10                          | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             | FR6.9                           | it.polito.ezshop.apiTests.EZShopTestGetSaleTransaction.testGetSaleTransactionSuccessfully |
|             | FR7.1                           | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
| 7.1         | FR7.2                           | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 7.2         | FR7.2                           | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testInvalidCreditCardException |
| 7.3         | FR7.2                           | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testNotEnoughBalance |
| 7.4         | FR7.1                           | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully            |
| 8.1         | FR6.12                          | it.polito.ezshop.apiTests.EZShopTestStartReturnTransaction.testStartReturnTransactionSuccessfully |
|             | FR6.13                          | it.polito.ezshop.apiTests.EZShopTestReturnProduct.testReturnProductSuccessfully |
|             | FR6.14                          | it.polito.ezshop.apiTests.EZShopTestEndReturnTransaction.testEndReturnTransactionSuccessfully1 |
|             | FR6.14                          | it.polito.ezshop.apiTests.EZShopTestEndReturnTransaction.testEndReturnTransactionSuccessfully2 |
|             | FR7.3, FR6.15                   | it.polito.ezshop.apiTests.EZShopTestReturnCashPayment.testReturnCashSuccessfully |
| 8.2         | FR6.12                          | it.polito.ezshop.apiTests.EZShopTestStartReturnTransaction.testStartReturnTransactionSuccessfully |
|             | FR6.13                          | it.polito.ezshop.apiTests.EZShopTestReturnProduct.testReturnProductSuccessfully |
|             | FR6.14                          | it.polito.ezshop.apiTests.EZShopTestEndReturnTransaction.testEndReturnTransactionSuccessfully1 |
|             | FR6.14                          | it.polito.ezshop.apiTests.EZShopTestEndReturnTransaction.testEndReturnTransactionSuccessfully2 |
|             | FR7.4, FR6.15                   | it.polito.ezshop.apiTests.EZShopTestReturnCreditCardPayment.testReturnSuccessfully |
| 9.1         | FR8.3                           | it.polito.ezshop.apiTests.EZShopTestGetCreditsAndDebits.testGetAll |
| 10.1        | FR7.4                           | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully
| 10.2        | FR7.3                           | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |


# Coverage of Non Functional Requirements

###

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
| NFR2                       | According to the IntelliJ IDEA test runner all the tests complete in less than 0.5 s. |
| NFR4                       | it.polito.ezshop.unitTests.TestUtilsIsValidBarcode.* |
| NFR5                       | it.polito.ezshop.unitTests.TestUtilsIsValidCreditCardNumber.* |
| NFR6                       | it.polito.ezshop.unitTests.TestLoyaltyCard.testValidateCode<br>it.polito.ezshop.unitTests.TestLoyaltyCard.testGenerateNewCode |
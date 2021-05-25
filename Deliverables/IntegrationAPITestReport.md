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

     <report the here the dependency graph of the classes in EzShop, using plantuml>
     
# Integration approach

    <Write here the integration sequence you adopted, in general terms (top down, bottom up, mixed) and as sequence
    (ex: step1: class A, step 2: class A+B, step 3: class A+B+C, etc)> 
    <Some steps may  correspond to unit testing (ex step1 in ex above), presented in other document UnitTestReport.md>
    <One step will  correspond to API testing>

The integration tests are performed using a bottom up approach. Starting from the leaf classes tested in the previous 
development phase, the first step tests the intermediate classes AccountBook and CustomerList (step 1). Then, the class
responsible for data persistence is tested (step 2). As a last step, all the methods implemented in EZShop are tested 
(step 3). Since these methods are highly dependent one to the other, this last phase follows a mixed approach: all the 
methods are tested at the same time.
    


#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

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
| it.polito.ezshop.model.CustomerList | it.polito.ezshop.integrationTests.testCustomerList.* (**TODO**) |


## Step 2: JsonInterface

| Classes  | JUnit test cases |
|----------|------------------|
| it.polito.ezshop.model.persistence.JsonInterface | it.polito.ezshop.integrationTests.TestJsonInterface.* |


## Step 3: API testing

**TODO**: methods for FR1 are missing

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




# Scenarios


<If needed, define here additional scenarios for the application. Scenarios should be named
 referring the UC in the OfficialRequirements that they detail>

## Scenario UCx.y

| Scenario |  name |
| ------------- |:-------------:| 
|  Precondition     |  |
|  Post condition     |   |
| Step#        | Description  |
|  1     |  ... |  
|  2     |  ... |



# Coverage of Scenarios and FR


<Report in the following table the coverage of  scenarios (from official requirements and from above) vs FR. 
Report also for each of the scenarios the (one or more) API JUnit tests that cover it. >




| Scenario ID | Functional Requirements covered | JUnit  Test(s) | 
| ----------- | ------------------------------- | ----------- | 
| 1.1         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestCreateProductType.testCreateProductTypeSuccessfully |             
| 1.2         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
| 1.3         | FR3.1                           | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
| 2.1         |                                 |             |             
| 2.2         |                                 |             |             
| 3.1         |                                 |             |
| 3.2         |                                 |             |
| 3.3         |                                 |             |
| 4.1         |                                 |             |
| 4.2         |                                 |             |
| 4.3         |                                 |             |
| 4.4         |                                 |             |
| 5.1         |                                 |             |
| 5.2         |                                 |             |
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
| 7.1         |                                 |             |
| 7.2         |                                 |             |
| 7.3         |                                 |             |
| 7.4         |                                 |             |
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
| 9.1         |                                 |             |
| 10.1        | FR7.4                           | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully 
| 10.2        | FR7.3                           | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |


# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|                            |           |



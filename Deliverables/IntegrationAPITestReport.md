# Integration and API Test Documentation

Authors:

Date:

Version:

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
| 1.1         | FR3                             | it.polito.ezshop.apiTests.EZShopTestCreateProductType.testCreateProductTypeSuccessfully |             
| 1.2         | FR3                             | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
| 1.3         | FR3                             | it.polito.ezshop.apiTests.EZShopTestUpdateProduct.testUpdateProductSuccessfully |             
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
| 6.1         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.2         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToProduct.testApplyDiscountRateSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.3         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestApplyDiscountRateToSale.testApplyDiscountRateSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
| 6.4         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestComputePointsForSale.testComputePointsForSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCreditCardPayment.testPayTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestModifyPointsOnCard.testAddPointsToCard |
| 6.5         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestDeleteSaleTransaction.testDeleteSaleTransactionSuccessfully |
| 6.6         | FR6                             | it.polito.ezshop.apiTests.EZShopTestStartSaleTransaction.testStartSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestAddProductToSale.testAddProductsToSaleSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestEndSaleTransaction.testEndSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestGetSaleTransaction.testGetSaleTransactionSuccessfully |
|             |                                 | it.polito.ezshop.apiTests.EZShopTestReceiveCashPayment.testPayTransactionSuccessfully |
| 7.1         |                                 |             |
| 7.2         |                                 |             |
| 7.3         |                                 |             |
| 7.4         |                                 |             |
| 8.1         |                                 |             |
| 8.2         |                                 |             |
| 9.1         |                                 |             |
| 10.1        |                                 |             |
| 10.2        |                                 |             |


# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|                            |           |



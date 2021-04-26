# Design Document


Authors:

Date:

Version:


# Contents

- [High level design](#package-diagram)
- [Low level design](#class-diagram)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design

<discuss architectural styles used, if any>
<report package diagram>

```plantuml
@startuml

package "EZShop" {}

package "GUI" {}
package "Model" {}
package "Exceptions" {}

"EZShop" -- "GUI"
"EZShop" <|-- "Model"
"EZShop" <|-- "Exceptions"

@enduml
```

# Low level design

<for each package, report class diagram>

```plantuml
left to right direction

class "EZShop" {
    + reset()
    + createUser(String, String, String)
    + deleteUser(Integer)
    + getAllUsers()
    + getUser(Integer)
    + updateUserRights(Integer, String)
    + login(String, String)
    + logout()
    + createProductType(String, String, double, String)
    + updateProduct(Integer, String, String, double, String)
    + deleteProductType(Integer)
    + getAllProductTypes()
    + getProductTypeByBarCode(String)
    + getProductTypesByDescription(String)
    + updateQuantity(Integer, int)
    + updatePosition(Integer, String)
    + issueReorder(String, int, double)
    + payOrderFor(String, int, double)
    + payOrder(Integer)
    + recordOrderArrival(Integer)
    + getAllOrders()
    + defineCustomer(String)
    + modifyCustomer(Integer, String, String)
    + deleteCustomer(Integer)
    + getCustomer(Integer)
    + getAllCustomers()
    + createCard()
    + attachCardToCustomer(String, Integer)
    + modifyPointsOnCard(String, int)
    + startSaleTransaction()
    + addProductToSale(Integer, String, int)
    + deleteProductFromSale(Integer, String, int)
    + applyDiscountRateToProduct(Integer, String, double)
    + applyDiscountRateToSale(Integer, double)
    + computePointsForSale(Integer)
    + closeSaleTransaction(Integer)
    + deleteSaleTicket(Integer)
    + getSaleTicket(Integer)
    + getTicketByNumber(Integer)
    + startReturnTransaction(Integer)
    + returnProduct(Integer, String, int)
    + endReturnTransaction(Integer, boolean)
    + deleteReturnTransaction(Integer)
    + receiveCashPayment(Integer, double)
    + receiveCreditCardPayment(Integer, String)
    + returnCashPayment(Integer)
    + returnCreditCardPayment(Integer, String)
    + recordBalanceUpdate(double)
    + getCreditsAndDebits(LocalDate, LocalDate)
    + computeBalance()
}

class JsonInterface {
    + readUsers()
    + writeUsers(List<User>)
    + readProductTypes()
    + writeProductTypes(List<ProductType>)
    + readSaleTransactions()
    + writeSaleTransactions(List<SaleTransaction>)
    + readReturnTransactions()
    + writeReturnTransactions(List<ReturnTransaction>)
    + readAccountBook()
    + writeAccountBook(AccountBook)
}

EZShop -- JsonInterface

EZShop -- "*" User : "Map"
EZShop -- AccountBook
EZShop -- "*" SaleTransaction
EZShop -- "*" ReturnTransaction
EZShop -- "*" ProductType

class User {
    + id
    + username
    + passwordHash
    + role
    + verifyPassword(String)
}

class ProductType {
    + id
    + barCode
    + description
    + sellPrice
    + quantity
    + discountRate
    + notes
    + position
}

class Position {
    + aisleID
    + rackID
    + levelID
    + {static} parsePosition(String)
}

ProductType - "0..1" Position

class Order {
    + pricePerUnit
    + quantity
    + status
    + computeTotal()
}

Order "*" - ProductType


class LoyaltyCard {
    + ID
    + points
    + updatePoints(int)
}

class Customer {
    + id
    + name
    + surname
    + loyaltyCard
}

LoyaltyCard "0..1" - Customer


class SaleTransaction {
    + ID
    + ticket
    + date
    + time
    + cost
    + paymentType /' cash or cc '/
    + creditCard
    + discountRate
    + status /' open/close '/
    + quantities /' Set<Quantity> '/
    + getAllQuantities() /' Set<Quantity> '/
    + updateQuantity(Quantity)
    + computePoints()
}

class Quantity {
    + product
    + quantity
    + discountRate
}

SaleTransaction -- "*" Quantity
Quantity "*" -- ProductType

/' (SaleTransaction, ProductType)  .. Quantity '/

SaleTransaction "*" -- "0..1" LoyaltyCard

class ReturnTransaction {
    + id
    + commit
    + returns /' HashSet<ReturnTransactionItem> '/
    + getAllReturns() /' HashSet<ReturnTransactionItem> '/
    + updateReturn(ReturnTransactionItem)
}

class ReturnTransactionItem {
    + quantity
    + returnedValue
}

ReturnTransaction -- "*" ReturnTransactionItem

ReturnTransaction "*" - SaleTransaction
ReturnTransactionItem "*" - ProductType

class CreditCard {
    + code
    + balance
    + {static} fromCode (code)
    + checkAvailability (amount)
    + updateBalance (amount)
}

/'
CreditCard cc = CreditCard.fromCode("xxx");
if (cc != null) {
    // credit card is valid
}'/

SaleTransaction - "0..1" CreditCard


class AccountBook {
    + transactions
    + recordTransaction(double)
    + recordTransaction(BalanceOperation)
    + getCredits()
    + getSales()
    + getDebits()
    + getReturns()
    + getOrders()
    + checkAvailability(double)
    + computeBalance()
    + {static} createSale(value)
    + {static} createReturn(value)
    + {static} createOrder()
}

class BalanceOperation {
    + id
    + description
    + amount
    + date

}
AccountBook -- "*" BalanceOperation

class Credit
class Debit

Credit --|> BalanceOperation
Debit --|> BalanceOperation

class Order
class Sale
class Return

Order --|> Debit
Sale --|> Credit
Return --|> Debit

SaleTransaction "0..1" --  Sale
ReturnTransaction -- Return


```







# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>
| Function | Store Manager | Shop Worker | Customer | Anonymous Customer | Accountant | Supplier |
| ------------- |:-------------|--|--|--|--|--|
| FR1 | yes | no | no | no | no | no |
| FR2 | yes | yes | no | no | no | no |
| FR3 | yes | yes | yes | yes | no | no |
| FR4 | yes | yes | no | no | no | no |
| FR5   | yes | no | no | no | yes | no |
| FR6   | yes | no | no | no | yes | no |
| FR7.1   | yes | yes | no | no | no | yes |
| FR7.2   | no | no | no | no | no | yes |
| FR7.3   | yes | yes | no | no | no | no |
| FR7.4   | yes | yes | no | no | no | no |
| FR8   | yes | no | no | no | no | no |


# Verification sequence diagrams
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

## Scenario 1.1: Create product type X
```plantuml

StoreManager -> EZShopGUI: Create Product type
EZShopGUI -> EZShop: CreateProductType()
EZShop -> ProductType: New ProductType
ProductType-->EZShop: Save New ProductType
EZShop -->EZShopGUI: Success
EZShopGUI --> StoreManager: Created New Product Type
```
## Scenario 2.1: Create user and define rights
```plantuml
Administrator -> EZShopGUI: Create User
EZShopGUI -> EZShop: CreateUser()
EZShop -> User: New User
User-->EZShop: Save New User
EZShop -->EZShopGUI: Success
EZShopGUI --> Administrator: Created New User
```

## Scenario 3.2: Order of product type X payed

```plantuml

StoreManager -> GUI: Show all orders
GUI -> EZShop: getAllOrders()
EZShop -> AccountBook: getOrders()
AccountBook -> AccountBook: Filter orders among transactions
AccountBook --> EZShop: Return orders
EZShop --> GUI: Return orders
GUI --> StoreManager: Show orders
StoreManager -> GUI: Select an order O
GUI -> EZShop: payOrderFor()
EZShop -> Order: computeTotal()
Order --> EZShop: Total is returned
EZShop -> AccountBook: checkAvailability()
AccountBook -> AccountBook: computeBalance()
AccountBook --> EZShop: Balance is enough
EZShop -> Order: setStatus()
Order --> EZShop: Order is in PAYED state
EZShop --> GUI: Success
GUI --> StoreManager: Successful message

```

## Scenario 4.2: Attach Loyalty card to customer record

```plantuml
StoreManager -> GUI: Show all customers
GUI -> EZShop: getAllCustomers()
EZShop --> GUI: Return the list of customers
GUI --> StoreManager: Show the list of customers
StoreManager -> GUI: Select a customer Cu
GUI -> EZShop: createNewCard()
EZShop --> GUI: Return card's id
GUI -> EZShop: attachCardToCustomer()
EZShop -> Customer: setLoyaltyCard()
Customer --> EZShop: Card is assigned
EZShop --> GUI: Card is assigned
GUI --> StoreManager: Successful message

```

## Scenario 8.2: Return transaction of product type X completed, cash

```plantuml



```

## Scenario 9.1: List credits and debits

```plantuml

StoreManager -> GUI: Selects a start date
StoreManager -> GUI: Selects a end date
GUI -> EZShop: getCreditsAndDebits()
EZShop -> AccountBook: recordTransaction()
AccountBook -> AccountBook: filter transactions of selected time-span
AccountBook -> EZShop: Return transactions_list
EZShop -> GUI: Return transactions_list
GUI -> StoreManager: Shows the transactions list

```
## Scenario 10.1: Return payment by  credit card

```plantuml

Cashier -> GUI: enters credit card number
GUI -> EZShop: returnCreditCardPayment()
EZShop -> EZShop: *validateCreditCardNumber()*
EZShop -> GUI: askReturnAmount()
GUI -> Cashier: ask return amount
Cashier -> GUI: enter return amount
GUI -> EZShop: recordBalanceUpdate()
EZShop -> GUI: Return return_amount
GUI -> Cashier: shows success massage


```

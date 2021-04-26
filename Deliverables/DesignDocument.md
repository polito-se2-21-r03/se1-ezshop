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

interface EZShopInterface {
}

note "This the provided EZShopInterface interface" as n

n - EZShopInterface

class EZShop {
    + currentUser
}

EZShopInterface <|-- EZShop

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

JsonInterface ---right- EZShop

EZShop -right- "*" User : "Map"
EZShop -- AccountBook
EZShop -- "*" SaleTransaction
EZShop -- "*" ReturnTransaction
EZShop -down--- "*" ProductType

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

EZShop -down- "*" Customer

LoyaltyCard "0..1" -up- Customer


class SaleTransaction {
    + ID
    + ticket
    + date
    + time
    + cost
    + paymentType /' cash or cc '/
    + discountRate
    + status /' open/close '/
    + getAllQuantities() /' Set<Quantity> '/
    + updateQuantity(Quantity)
    + computePoints()
    + closeTransaction()
}

class Quantity {
    + product
    + quantity
    + discountRate
}

SaleTransaction -- "*" Quantity

/' (SaleTransaction, ProductType)  .. Quantity '/

SaleTransaction "*" -right- "0..1" LoyaltyCard

Quantity "*" -- ProductType

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

ReturnTransaction -down- "*" ReturnTransactionItem

ReturnTransaction "*" - SaleTransaction
ReturnTransactionItem "*" -up- ProductType

/'
CreditCard cc = CreditCard.fromCode("xxx");
if (cc != null) {
    // credit card is valid
}'/

/' SaleTransaction - "0..1" CreditCard '/


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
    + addSale(value)
    + addReturn(value)
    + addOrder()
}

class BalanceOperation {
    + id
    + description
    + amount
    + date

}
AccountBook -down- "*" BalanceOperation

class Debit
class Credit

Debit --up-|> BalanceOperation
Credit --up-|> BalanceOperation

class Order
class Sale
class Return

SaleTransaction "0..1" ---  Sale
ReturnTransaction --- Return

Order ---up-|> Debit
Sale --up-|> Credit
Return --up-|> Debit

interface CreditCardCircuit {
    + init()
    + validateCode(creditCardCode)
    + checkAvailability(creditCardCode, amount)
    + addDebit(creditCardCode, amount)
    + addCredit(creditCardCode, amount)
}

CreditCardCircuit -up-- EZShop

class TextualCreditCardCircuit {
    + readFromFile(String)
    + writeToFile(String)
}

class CreditCard {
    + code
    + balance
    + checkAvailability (amount)
    + updateBalance (amount)
}

CreditCard "*" -up- TextualCreditCardCircuit

class VisaCreditCardCircuitAdapter {}

TextualCreditCardCircuit -up-|> CreditCardCircuit
VisaCreditCardCircuitAdapter -up-|> CreditCardCircuit

class VisaCreditCardCircuitService {
    + authenticate()
    + ...
}

VisaCreditCardCircuitService -up- VisaCreditCardCircuitAdapter : "adaptees"

```







# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>
| | EZShopInterface | JsonInterface | User | ProductType | Quantity | Customer | FidelityCard | CreditCardCircuit | SaleTransaction | ReturnTransaction | Sale | Return |Credit | Debit | AccountBook |
| :--: |:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
| FR1 | X | X | X | | | |
| FR3 | X | X | X | X | | |
| FR4 | X | X | X | X | | |
| FR5 | X | X | X | | | X | X |
| FR6 | X | X | X | X | X | X | X | | X | X | | |
| FR7 | X | X | X | X | X | X | | X | | | X | X | X | X |
| FR8 | X | X | X | | | | | | | |  | |X  | X | X |


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

StoreManager -> EZShopGUI: Show all orders
EZShopGUI -> EZShop: getAllOrders()
EZShop -> AccountBook: getOrders()
AccountBook -> AccountBook: Filter orders among transactions
AccountBook --> EZShop: Return orders
EZShop --> EZShopGUI: Return orders
EZShopGUI --> StoreManager: Show orders
StoreManager -> EZShopGUI: Select an order O
EZShopGUI -> EZShop: payOrderFor()
EZShop -> Order: computeTotal()
Order --> EZShop: Total is returned
EZShop -> AccountBook: checkAvailability()
AccountBook -> AccountBook: computeBalance()
AccountBook --> EZShop: Balance is enough
EZShop -> Order: setStatus()
Order --> EZShop: Order is in PAYED state
EZShop --> EZShopGUI: Success
EZShopGUI --> StoreManager: Successful message

```

## Scenario 4.2: Attach Loyalty card to customer record

```plantuml
StoreManager -> EZShopGUI: Show all customers
EZShopGUI -> EZShop: getAllCustomers()
EZShop --> EZShopGUI: Return the list of customers
EZShopGUI --> StoreManager: Show the list of customers
StoreManager -> EZShopGUI: Select a customer Cu
EZShopGUI -> EZShop: createNewCard()
EZShop --> EZShopGUI: Return card's id
EZShopGUI -> EZShop: attachCardToCustomer()
EZShop -> Customer: setLoyaltyCard()
Customer --> EZShop: Card is assigned
EZShop --> EZShopGUI: Card is assigned
EZShopGUI --> StoreManager: Successful message

```

## Scenario 8.2: Return transaction of product type X completed, cash

```plantuml



```

## Scenario 9.1: List credits and debits

```plantuml

StoreManager -> EZShopGUI: Selects a start date
StoreManager -> EZShopGUI: Selects a end date
EZShopGUI -> EZShop: getCreditsAndDebits()
EZShop -> AccountBook: recordTransaction()
AccountBook -> AccountBook: filter transactions of selected time-span
AccountBook -> EZShop: Return transactions_list
EZShop -> EZShopGUI: Return transactions_list
EZShopGUI -> StoreManager: Shows the transactions list

```
## Scenario 10.1: Return payment by  credit card

```plantuml

Cashier -> EZShopGUI: enters credit card number
EZShopGUI -> EZShop: returnCreditCardPayment()
EZShop -> CreditCardCircuit: validateCreditCardNumber()
CreditCardCircuit -> CreditCardCircuit: validateCode()
CreditCardCircuit -> EZShop: approves
EZShop -> EZShopGUI: askReturnAmount()
EZShopGUI -> Cashier: ask return amount
Cashier -> EZShopGUI: enter return amount
EZShopGUI -> EZShop: recordBalanceUpdate()
EZShop -> EZShopGUI: Return return_amount
EZShopGUI -> Cashier: shows success massage

```

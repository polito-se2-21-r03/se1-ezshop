# Requirements Document

Authors:

Date:

Version:

# Contents

- [Essential description](#essential-description)
- [Stakeholders](#stakeholders)
- [Context Diagram and interfaces](#context-diagram-and-interfaces)
	+ [Context Diagram](#context-diagram)
	+ [Interfaces](#interfaces)

- [Stories and personas](#stories-and-personas)
- [Functional and non functional requirements](#functional-and-non-functional-requirements)
	+ [Functional Requirements](#functional-requirements)
	+ [Non functional requirements](#non-functional-requirements)
- [Use case diagram and use cases](#use-case-diagram-and-use-cases)
	+ [Use case diagram](#use-case-diagram)
	+ [Use cases](#use-cases)
    	+ [Relevant scenarios](#relevant-scenarios)
- [Glossary](#glossary)
- [System design](#system-design)
- [Deployment diagram](#deployment-diagram)

# Essential description

Small shops require a simple application to support the owner or manager. A small shop (ex a food shop) occupies 50-200 square meters, sells 500-2000 different item types, has one or a few cash registers
EZShop is a software application to:
* manage sales
* manage inventory
* manage customers
* support accounting


# Stakeholders

| Stakeholder name  | Description |
| ----------------- |:-----------:|
| Employee | A generic employee of the shop, can be store manager, accountant or cashier. |
| Store manager | Manages store, can insert, modify and delete users and products. |
| Accountant | Views expenses and income, can manage products on sale and prices. |
| Cashier | Registers sales and updates stock levels. |
| Supplier | Views reorder needs of shop and updates stock levels upon delivery. |
| Anonymous customer | Buys products from the shop. |
| Customer | Creates fidelity account to receive discounts when shopping. |
| Developer | Maintains application and receives license payments. |

# Context Diagram and interfaces

## Context Diagram
\<Define here Context diagram using UML use case diagram>

\<actors are a subset of stakeholders>

```plantuml
top to bottom direction
actor Employee as e
actor :Store Manager: as m
actor Accountant as a
actor Cashier as c
actor Supplier as s
actor Customer as rc
actor :Anonymous Customer: as ac
m -up-|> a
a -up-|> e
c -up-|> e
rc -up-|> ac
e -> (EZShop)
s -> (EZShop)
ac -> (EZShop)
```

## Interfaces
\<describe here each interface in the context diagram>

\<GUIs will be described graphically in a separate document>

| Actor | Logical Interface | Physical Interface  |
| ------------- |:-------------:| -----:|
| Manager, Accountant | Web GUI | Screen, Keyboard, Mouse on PC |
| Cashier | Web GUI | Cash Register, Barcode Scanner, Screen, Keyboard, Mouse on PC |
| Supplier | API | Supplier managment system |
| Customer | GUI + API | Automatic Cash Register, Barcode Scanner, Touchscreen Display |

# Stories and personas
\<A Persona is a realistic impersonation of an actor. Define here a few personas and describe in plain text how a persona interacts with the system>

\<Persona is-an-instance-of actor>

\<stories will be formalized later as scenarios in use cases>


# Functional and non functional requirements

## Functional Requirements

\<In the form DO SOMETHING, or VERB NOUN, describe high level capabilities of the system>

\<they match to high level use cases>

| ID        | Description  |
| ------------- |:-------------:|
|  FR1     | Manage rights. Authorize access to functions to specific actors according to access rights |
|  FR2     | Manage inventory |
|  FR2.1   | Insert a new product inside the inventory |
|  FR2.2   | Update the inventory level for a product |
|  FR2.3   | Remove a product from the inventory |
|  FR2.4   | Notify the Manager that a product needs to be reordered |
|  FR2.5   | Link the product to its product descriptor |
|  FR2.6   | Category management |
|  FR2.6.1 | Create/Update a category |
|  FR2.6.2 | Assign a product to a category |
|  FR2.6.3 | Delete a category |
|  FR2.6.4 | List categories |
|  FR2.6.4 | Show products associated with a category |
|  FR2.7   | Search a product |
|  FR3     | Sales management |
|  FR3.1   | Start a transaction |
|  FR3.2   | Add or delete products to the transaction |
|  FR3.3   | Cancel a running transaction |
|  FR3.4   | Add a payment method |
|  FR3.5   | Apply discount and special offers, possibly depending on the fidelity card |
|  FR3.6   | Complete checkout and print receipt |
|  FR3.7   | Keep transaction informations |
|  FR4     | Manage customers |
|  FR4.1   | Define a new customer, or modify an existing one |
|  FR4.2   | Create a fidelity card for the customer |
|  FR4.3   | Delete a customer |
|  FR4.4   | Search a customer |
|  FR5     | Manage catalogue (prices): |
|  FR5.1   | Insert/Update/Remove a product descriptor |
|  FR5.2   | Define a special offer for the product |
|  FR5.3   | Define special offers for fidelity plans |
|  FR6     | Manage accounting |
|  FR6.1   | Manage incomes and expenses |
|  FR6.2   | Compute a balance |
|  FR6.3   | Write a report |
|  FR7     | Manage orders |
|  FR7.1   | Issue an order |
|  FR7.2   | Keep track of orders |
|  FR7.3   | Keep track of expenses |

### Access right, actor vs function

| Function | Store Manager | Cashier | Customer | Anonymous Customer | Accountant |
| ------------- |:-------------|--|--|--|--|
| FR1 | yes | no | no | no | no |
| FR2.1 | yes | no | no | no | no |
| FR2.2 | yes | no | no | no | no |
| FR2.3 | yes | no | no | no | no |
| FR2.4 | yes | yes | no | no | no |
| FR2.5 | yes | no | no | no | no |
| FR2.7 | yes | no | no | no | no |
| FR3 | yes | yes | yes | yes | no |
| FR4.1 | yes | yes | Only Customer X for Customer X | no | no |
| FR4.2 | yes | yes | no | no | no |
| FR4.3 | yes | yes | Only Customer X for Customer X | no | no |
| FR4.4 | yes | yes | no | no | no |
| FR5   | yes | no | no | no | yes |
| FR6   | yes | no | no | no | yes |
| FR7.1   | yes | no | no | no | no |
| FR7.2   | yes | no | no | no | no |
| FR7.3   | yes | no | no | no | yes |


## Non Functional Requirements

\<Describe constraints on functional requirements>

| ID        | Type | Description  | Refers to |
| ------------- |:-------------:| :-----:| -----:|
|  NFR1     | Privacy  | Ensure customer data are stored safely and can't be accessed by non authorized users. | All FR |
|  NFR2     | Privacy | The system complies with local privacy law requirements (GDPR in Europe). | All FR |
|  NFR4     | Reliability | The software should be able to correctly update the inventory level in any situation (even if an error occurs) | All FR |
|  NFR5     | Portability | The application should be accessed by Chrome (version 81 and more recent), and Safari (version 13 and more recent) (this covers around 80% of installed browsers); and from the operating systems where these browsers are available (Windows, MacOS, Unix). | All FR |
|  NFR6     | Security | All the users of the systems should be authenticated and no information should be visible to the outside. | All FR |
|  NFR7     | Performance |  The application should complete operations in less than 1 second. | All FR |
|  NFR8     | Maintainability | The application should be always up to date with law requirements and security standards | All FR |
|  NFR3     | Usability | The application should be used with no specific training.| All FR |

# Use case diagram and use cases


## Use case diagram
\<define here UML Use case diagram UCD summarizing all use cases, and their relationships>


\<next describe here each use case in the UCD>
### Use case 1, UC1 - Manage rights
| Actors Involved    | Store Manager |
| ------------------ |:-------------:|
|  Precondition      | The new employee does not have an account |  
|  Post condition    | The new employee have an account on the system |
|  Nominal Scenario  | The Store Manager hires a new employee and creates a new account with 'accountant' or 'cashier' rights |
|  Variants          | The Store Manager updates the rights of an employee |

### Use case 2, UC2 -  Insert a new product inside the inventory
| Actors Involved    | Store Manager |
| ------------------ |:-------------:|
|  Precondition      | Product P does not exist inside the inventory |  
|  Post condition    | Manager inserts product P inside the inventory |
| | P.units is set according to the number of units of product P available |
| | P.cost is set according to the actual cost of each unit |
| | P.cost_total = P.cost*P.units |
| | P.cathegory is set |
| | P.id is set, which is the barcode of the product |
| | If the item is for sale, P.forsale = 'yes' is set and P.price is set, which is the price tag for each unit of the product |
| | If the item is part of the shop's furnitures P.forsale = 'no'  |
| | If the item is considered to be essential, P.essential = 'yes' |
|  Nominal Scenario  | A new product is supplied, so the Manager enters all the infos inside the inventory. If P.forsale = 'yes' then set P.price |
|  Variants          | If P.forsale = 'no', the new product is part of the furniture of the shop (i.e. shelves, cash registers etc.) |

### Use case 3, UC3 - Update the inventory level for a product
| Actors Involved    | Store Manager |
| ------------------ |:-------------:|
|  Precondition      | Product P already exists inside the inventory |  
|  Post condition    | Store Manager updates product P infos inside the inventory |
| | P.newunits is set according to the number of new units arrived at the shop |
| | P.units = P.units + P.newunits |
| | P.desc can be modified |
| | P.newcost is set according to the new cost of each new unit |
| | P.newcost_total = P.cost_total + P.newcost*P.newunits |
|  Nominal Scenario  | New supplies of product P arrive at the shop, the Store Manager has to set the entries P.newunits and P.newcost |
|  Variants          | - |

### Use case 4, UC4 - Remove a product from the inventory
| Actors Involved    | Store Manager |
| ------------------ |:-------------:|
|  Precondition      | Product P exists inside the inventory |  
|  Post condition    | Product P is deleted from the inventory |
|  Nominal Scenario  | The Manager deletes a product from the system, and he has to confirm his choice |
|  Variants          | - |

### Use case 5, UC5 - Notify that a product needs to be reordered
| Actors Involved    | Store Manager, Cashier |
| ------------------ |:-------------:|
|  Precondition      | Shop is running out of supplies of Product P  |  
|  Post condition    | P.supply = 'yes' flag is set |
|  Nominal Scenario  | When P.units <= 5 the Cashier or Manager can tick the square if the product is to be resupplied |
|  Variants          | If P.essential = 'yes' the product will be automatically flagged to be supplied when P.units <= 10 |

### Use case X, UCX - Insert a new category
| Actors Involved    | Store manager |
| ------------------ |:-------------:|
|  Precondition      | - |  
|  Post condition    | Category C is created. |
|  Nominal Scenario  | The store manager creates a new category by entering its properties (name, description, vat percentage and possibly a parent category). |
|  Variants          | A category with the same name already exists and the application shows an error message. |
|  		             | The assignment of the parent category generates a loop in the hierarchy and the operation is aborted with an error message. |

### Use case X, UCX - Update an existing category
| Actors Involved    | Store manager |
| ------------------ |:-------------:|
|  Precondition      | Category C exists. |  
|  Post condition    | Category C is updated. |
|  Nominal Scenario  | The store manager selects a category and changes its properties (name, description, vat percentage). |
|  Variants          | A category with the same name already exists and the operation is aborted. |
|  		             | The assignment of the parent category generates a loop in the hierarchy and the operation is aborted with an error message. |

### Use case X, UCX - List all products associated with the category
| Actors Involved    | Store manager |
| ------------------ |:-------------:|
|  Precondition      | - |
|  Post condition    | - |
|  Nominal Scenario  | The store manager enters the name of a category. If one or more categories match with the parameter, the system shows their details and the products associated with them. |
|  Variants          | - |

### Use case X, UCX - Delete a category
| Actors Involved    | Store manager |
| ------------------ |:-------------:|
|  Precondition      | Category C exists. |
|  Post condition    | Category C is removed. |
|  Nominal Scenario  | The store manager selects a category and removes it. |
|  Variants          | There is at least one product associated with category C: the operation is aborted with an error message. |

### Use case X, UCX - Creation of a new sale transaction
| Actors Involved     | Cashier, Customer |
| ------------------- |:-------------:|
|  Precondition       | The cash register CR is not processing other transactions (CR.state == 'ready'). |  
|  Post condition     | Transaction T is created. |
|                     | Transaction T is ready and associated with the cash register CR that created it (T.state == 'ready' && T.cash_register == CR) |
|                     | The cash register CR is ready to modify the list of products associated to T (CR.state == 'busy'). |
|  Nominal Scenario   | The cashier creates a new sale transaction T. |
|  Variants           | The customer shows a fidelity card FC. |

##### Scenario X.1
| Scenario X.1      | The customer shows a fidelity card. |
| ----------------- |:-------------:|
|  Precondition     | The cash register is not processing other transactions (CR.state == 'ready'). |
|  Post condition   | Transaction T is created. |
|                   | Transaction T is ready and associated with the cash register CR that created it (T.state == 'ready' && T.cash_register == CR) |
|                   | The fidelity card FC is attached to the transaction T. |
|                   | The cash register CR is ready to modify the list of products associated to T (CR.state == 'busy'). |
| Step#  | Description  |
|  1     | The cashier starts a new transaction T. |
|  2     | The cashier scans the fidelity card FC of the customer. |
|  3     | The fidelity card FC is attached to the transaction. |

### Use case X, UCX - Attach a product to a transaction
| Actors Involved     | Cashier |
| ------------------- |:-------------:|
|  Precondition       | Transaction T exists. |
|                     | Product P exists and its inventory level is at least n (P.units >= n) |  
|                     | Cash register is ready to modify transaction T (T.cash_register == CR). |  
|  Post condition     | CR is ready to further modify the list of products associated to T. |
|                     | CR is ready to complete check-out for transaction T. |
|                     | Product P is added to the products list of transaction T with quantity n. |
|                     | The inventory level for product P is updated (P.units -= n) |
|  Nominal Scenario   | The cashier adds product P to the products list of transaction T with quantity n; the application updates the inventory level for product P. |
|  Variants           | - |


### Use case X, UCX - Remove a product from a transaction
| Actors Involved     | Cashier |
| ------------------- |:-------------:|
|  Precondition       | Transaction T exists. |
|                     | Product P is attached to the transaction with quantity n (P in T.products && T.products[P] == n) |  
|                     | Cash register is ready to modify transaction T (T.cash_register == CR && CR.state == 'busy'). |
|  Post condition     | CR is ready to further modify the list of products associated to T. |
|                     | CR is ready to complete check-out for transaction T. |
|                     | Product P is removed from the products list of transaction T. |
|                     | The inventory level for product P is restored (P.units += n) |
|  Nominal Scenario   | The cashier removes product P from the transaction; the application updates the inventory level for product P. |
|  Variants           | - |


### Use case X, UCX - Payment of a transaction
| Actors Involved     | Cashier, Customer |
| ------------------- |:-------------:|
|  Precondition       | Transaction T exists. |
|                     | At least one product is attached to transaction T (T.products.length > 0). |  
|                     | Cash register is ready to modify transaction T (T.cash_register == CR). |
|  Post condition     | Transaction T is completed, either successfully or with an exception. |
|  Nominal Scenario   | The customer pays in cash and the transaction is completed successfully. |
|  Variants           | The customer pays in cash but he has not enough money. |


##### Scenario X.1
| Scenario X.1      | The customer pays in cash and the transaction is completed successfully. |
| ----------------- |:-------------:|
|  Precondition     | Transaction T exists. |
|                   | At least one product is attached to transaction T (T.products.length > 0). |  
|                   | Cash register is ready to modify transaction T (T.cash_register == CR). |
|  Post condition   | CR is ready for processing another transaction (CR.state == 'ready'). |
|                   | The sale transaction is recorded in the the transaction register. |
| Step#  | Description  |
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the fidelity program benefits. |
|  2     | The cashier selects the 'cash' payment method and types the cash amount given by the customer. |
|  3     | The cash register CR computes the change. |
|  4     | The checkout is completed successfully and a receipt is printed. |
|  5     | T is recorded in the transaction register. |


##### Scenario X.2
| Scenario X.2      | The customer pays in cash but he has not enough money. |
| ----------------- |:-------------:|
|  Precondition     | Transaction T exists. |
|                   | At least one product is attached to transaction T (T.products.length > 0). |  
|                   | Cash register is ready to modify transaction T (T.cash_register == CR). |
|  Post condition   | CR is ready for processing another transaction (CR.state == 'ready'). |
|                   | The inventory level for products attached to the transaction is restored. |
|                   | The sale transaction is NOT recorded in the the transaction register. |
| Step#  | Description  |
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the fidelity program benefits. |
|  2     | C selects the 'cash' payment method but the customer has not enough cash. A warning is raised and the transaction is NOT aborted. The cashier can either remove products from the transaction or cancel it. |


##### Scenario X.3
| Scenario X.3      | The customer pays with credit card and the transaction is completed successfully. |
| ----------------- |:-------------:|
|  Precondition     | Transaction T exists. |
|                   | At least one product is attached to transaction T (T.products.length > 0). |  
|                   | Cash register is ready to modify transaction T (T.cash_register == CR). |
|                   | The credit card POS system is ready. |
|  Post condition   | CR is ready for processing another transaction (CR.state == 'ready'). |
|                   | The inventory level for products affected by the sale transaction is updated. |
|                   | The sale transaction is recorded in the the transaction register. |
| Step#  | Description  |
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the fidelity program benefits. |
|  2     | The cash register CR communicates the total to the credit card POS system. |
|  3     | The credit card POS system notifies a successful payment. |
|  4     | The checkout is completed successfully and a receipt is printed. |
|  5     | T is recorded in the transaction register. |


##### Scenario X.4
| Scenario X.4      | The customer pays with credit card but the POS system notifies a payment exception. |
| ----------------- |:-------------:|
|  Precondition     | Transaction T exists. |
|                   | At least one product is attached to transaction T (T.products.length > 0). |  
|                   | Cash register is ready to modify transaction T (T.cash_register == CR). |
|                   | The credit card POS system is ready. |
|  Post condition   | CR is ready for processing another transaction (CR.state == 'ready'). |
|                   | The inventory level for products attached to the transaction is restored. |
|                   | The sale transaction is NOT recorded in the the transaction register. |
| Step#  | Description  |
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the fidelity program benefits. |
|  2     | The cash register CR communicates the total to the credit card POS system. |
|  3     | The credit card POS system notifies an exception. |
|  4     | Depending on the type of the exception raised by the POS system, the cashier can either proceed with the checkout using a different payment method or cancel the transaction. |


### Use case X, UCX - Cancel a running sale transaction
| Actors Involved     | Cashier |
| ------------------- |:-------------:|
|  Precondition       | Transaction T exists. | 
|                     | Cash register is ready to modify transaction T (T.cash_register == CR). |
|  Post condition     | Transaction T is cancelled. |
|                     | CR is ready for processing another transaction (CR.state == 'ready'). |
|                     | The inventory level for products attached to the transaction is restored. |
|                     | The sale transaction is NOT recorded in the the transaction register. |
|  Nominal Scenario   | The cashier cancels the transaction. |
|  Variants           | - |


### Use case 1, UC1 - Check resupply needs
| Actors Involved | Supplier |
| ------------- |:-------------:|
| Precondition | Supplier S is logged into the system |  
| Post condition | S sees list of products and quantities to be resupplied |
| Nominal Scenario | S checks the resupply needs of the shop |

​
### Use case 2, UC2 - Post resupply claim
| Actors Involved | Supplier |
| ------------- |:-------------:|
| Precondition | Supplier S is logged into the system |
| | The shop is not fully stocked |
| Post condition | A pending resupply claim is created |
| Nominal Scenario | The shop is running low on some items; S checks the shops resupply needs and delivers items; S creates a resupply claim indicating which items have been delivered to the shop |
| Variants | Shop is being fully resupplied |
| | Only a subset of items is being resupplied |
| | Some items are not fully resupplied to the desired quantity |

​
### Use case 3, UC3 - Edit pending resupply claim
| Actors Involved | Supplier |
| ------------- |:-------------:|
| Precondition | Supplier S is logged into the system |
| | S has posted a resupply claim C to the system |
| | No shop employee has approved C yet |
| Post condition | C is updated with the products and quantities about to be delivered |
| Nominal Scenario | S has created C; Before any employee of the shop approves C, S notices a mistake; S updates C with the correct products and quantities he is intending to deliver |
| Variants | A product is removed from C |
| | A product is added to C |
| | The quantity to be delivered is changed in C |

​
### Use case 4, UC4 - Approve pending resupply claim
| Actors Involved | Employee |
| ------------- |:-------------:|
| Precondition | An employee E is logged into the system |
| | A pending resupply claim C exists |
| Post condition | The items from C are added to the shops stock |
| | C is no longer marked as pending |
| Nominal Scenario | E has received and checked the delivery described in C; E approves C, removing it from the pending resupply claims and stock levels are updated |

​
### Use case 5, UC5 - View shop's expenses and earnings
| Actors Involved | Accountant |
| ------------- |:-------------:|
| Precondition | Accountant A is logged into the system |  
| Post condition | A sees a list of expenses and earnings for the shop |
| Nominal Scenario | A analyses the shops expenses and earnings from sales |
| Variants | A can see detailed information for each product sold in the store |
| | A can specify a time frame to analyse accounting data |


##### Scenario 1.1

\<describe here scenarios instances of UC1>

\<a scenario is a sequence of steps that corresponds to a particular execution of one use case>

\<a scenario is a more formal description of a story>

\<only relevant scenarios should be described>

| Scenario 1.1 | |
| ------------- |:-------------:|
|  Precondition     | \<Boolean expression, must evaluate to true before the scenario can start> |
|  Post condition     | \<Boolean expression, must evaluate to true after scenario is finished> |
| Step#        | Description  |
|  1     |  |  
|  2     |  |
|  ...     |  |

##### Scenario 1.2

##### Scenario 1.x

### Use case 2, UC2
..

### Use case x, UCx
..


### Use case X, UCX - Define a new customer
| Actors Involved     | Manager, Cashier |
| ------------------- |:-------------:|
|  Precondition       | The Actor can fill the all essential informations about customer |
|                     | Customer does not exist in the system | 
|  Post condition     | Actor fills the Customer's info |
|  					  | A fidelity card is paired with customer |
|                     | The Customer is added to the system |
|  Nominal Scenario   | Actor reaches the GUI of Customer via a browser. After having filled all the needed information, the Actor gives the Fidelity Card to the new Customer. |
|  Variants           | The email address or phone number is associated to another Customer: raise an error after "Submit" button is clicked |
|  Variants           | Some information marked with "*" (important) are missing: raise an error after "Submit" button is clicked |

### Use case X, UCX - Delete a customer
| Actors Involved     | Manager, Cashier |
| ------------------- |:-------------:|
|  Precondition       | Customer exists in the system. |  
|  Post condition     | The Customer deleted from the system. |
|                     | Deactivate Fidelity Card of Customer |
|  Nominal Scenario   | Actor reaches the GUI of Customer via browser. The Actor Search the Customer through the list and clicks on "Delete" button. After the confirmation the Customer is deleted from the database and his Card deactivated. |
|  Variants           | If Customer's Fidelity Card has some points left, raise a Warning after pressing the "Delete" button. |

### Use case X, UCX - Modify the customer
| Actors Involved     | Manager, Cashier |
| ------------------- |:-------------:|
|  Precondition       | Customer exists in the system. |  
|  Post condition     | The customer information has been updated on the system. |
|  Nominal Scenario   | Actor reaches the GUI of Customer via browser. After having filled all the information that need to be modified, the Actor saves the changes. |
|  Variants           | The new email address/phone number is associated to another Customer: raise an error after "Submit" button is clicked |
|  Variants           | The Customer has lost his Fidelity Card, so the Actor gives him a new one with a new ID, restoring Customer's points and deactivates the old one |

### Use case X, UCX - Manage the catalogue (Insert or Update the catalogue)

| Actors Involved    | Store Manager, Accountant |
| ------------------ |:-------------:|
|  Precondition      | Product P is inside the inventory | 
| | Product P is for sale (P.forsale = 'yes') | 
|  Post condition    | P.description is set, which is a string that describes the product |
| | P.category is set |
| | P.price is set, which is the base price of the Product |
|  Nominal Scenario  | The Actor sets price, category and description of the product. |
|  Variants          |  |

### Use case X, UCX - Update a product
| Actors Involved    | Store Manager, Accountant |
| ------------------ |:-------------:|
|  Precondition      | Product P is inside the catalogue | 
|  Post condition    | P.description can be updated |
| | P.category can be modified |
| | P.price can be changed, which is the base price of the Product |
|  Nominal Scenario  | The Actor search P from the lists and modifies price, category or description of the product. |
|  Variants          |  |

### Use case X, UCX - Delete a product
| Actors Involved     | Manager, Accountant |
| ------------------- |:-------------:|
|  Precondition       | Product exists in the system. |
|  Post condition     | Product has been deleted on the system. |
|  Nominal Scenario   | The product is no longer for sale so the Actor removes it from the Catalogue through the Web GUI |
|  Variants           | |

### Use case X, UCX - Define a special offer to product
| Actors Involved     | Manager, Accountant |
| ------------------- |:-------------:|
|  Precondition       | Product exists in the system. |  
| | There is not previous offer on the product with same type discounts. |
|  Post condition     | The price of product has been recalculated on the system. |
|  Nominal Scenario   | 1.Actor reaches the GUI of product catalogue via browser. |
|  | 2.Actor selects one or more products. |
|  								    | 3. Actor fills the percentage discount of the product for anonymous customer and for fidelity card |
|  Variants           | |

### Use case X, UCX - Define a special offer to product
| Actors Involved     | Manager, Accountant |
| ------------------- |:-------------:|
|  Precondition       | Product exists in the system. |  
|											| There is another previous offer on the product with same type discounts. |
|  Post condition     | The price of product has not been recalculated on the system. |
|											| An error that “There is previous offer of product on the system” is raised. |
| Exceptional Scenario| 1.Actor reaches the GUI of product catalogue via browser. |
|  								    | 2.Actor selects one or more products. |
|  								    | 3. Actor fills the percentage discount of the product for anonymous customer and for fidelity card |
|											| 4. An error is raised and the transactions is rolled back. |
|  Variants           | |


# Glossary

\<use UML class diagram to define important terms, or concepts in the domain of the system, and their relationships>

\<concepts are used consistently all over the document, ex in use cases, requirements etc>

# System Design
\<describe here system design>

\<must be consistent with Context diagram>

# Deployment Diagram

\<describe here deployment diagram >

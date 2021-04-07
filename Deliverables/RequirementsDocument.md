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
|   Actor x..     |  |  |

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
|  FR1     |  |
|  FR2     |   |
| FRx..  | |

## Non Functional Requirements

\<Describe constraints on functional requirements>

| ID        | Type (efficiency, reliability, ..)           | Description  | Refers to |
| ------------- |:-------------:| :-----:| -----:|
|  NFR1     |   |  | |
|  NFR2     | |  | |
|  NFR3     | | | |
| NFRx .. | | | |


# Use case diagram and use cases


## Use case diagram
\<define here UML Use case diagram UCD summarizing all use cases, and their relationships>


\<next describe here each use case in the UCD>


### Use case X, UCX - Insertion of a new category
| Actors Involved    | Store manager |
| ------------------ |:-------------:| 
|  Precondition      | Category C does not exist. |  
|  Post condition    | Category C is created. |
|  Nominal Scenario  | The store manager creates a new category by entering its properties. |
|  Variants          | A category with the same name already exists and the application shows an error message. |
|  		             |The assignment of the parent category generates a loop in the hierarchy and the operation is aborted. |


### Use case X, UCX - Update an existing category
| Actors Involved    | Store manager |
| ------------------ |:-------------:| 
|  Precondition      | Category C exists. |  
|  Post condition    | Category C is updated. |
|  Nominal Scenario  | The store manager selects a category and changes its properties. |
|  Variants          | A category with the same name already exists and the operation is aborted. |
|  		             | The assignment of the parent category generates a loop in the hierarchy and the operation is aborted. |


### Use case X, UCX - Assignment of a product to a category
| Actors Involved    | Store manager |
| ------------------ |:-------------:| 
|  Precondition      | Product P exists. |
|                    | Category C exists. |
|  Post condition    | Product P is associated with category C (P.category = C). |
|  Nominal Scenario  | The store manager selects a product and associates a category to it (P.category = C). |
|  Variants          | - |


### Use case X, UCX - Creation of a new sale transaction
| Actors Involved     | Cashier, Customer |
| ------------------- |:-------------:| 
|  Precondition       | The cash register CR is not processing other transactions (CR.state == 'ready'). |  
|  Post condition     | Transaction T is created. |
|                     | Transaction T is ready and associated with the cash register CR that created it (T.state == 'ready' && T.cash_register == CR) |
|                     | The cash register CR is ready to modify the list of products associated to T (CR.state == 'busy'). |
|  Nominal Scenario   | The cashier creates a new sale transaction T. |
|  Variants           | The customer shows a loyalty card LC. |
|                     | The customer shows an expired loyalty card LC. |


##### Scenario X.1 
| Scenario X.1      | The customer shows a loyalty card. |
| ----------------- |:-------------:| 
|  Precondition     | The cash register is not processing other transactions (CR.state == 'ready'). |
|                   | The loyalty card is not expired (LC.expiration_date < now()) |
|  Post condition   | Transaction T is created. |
|                   | Transaction T is ready and associated with the cash register CR that created it (T.state == 'ready' && T.cash_register == CR) |
|                   | The loyalty card LC is attached to the transaction T. |
|                   | The cash register CR is ready to modify the list of products associated to T (CR.state == 'busy'). |
| Step#  | Description  |
|  1     | The cashier starts a new transaction T. |
|  2     | The cashier scans the loyalty card LC of the customer. |
|  3     | The loyalty card LC is attached to the transaction. |


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
|  Variants           | The customer pays in cash but the cash register has not enough rest. |


##### Scenario X.1 
| Scenario X.1      | The customer pays in cash and the transaction is completed successfully. |
| ----------------- |:-------------:| 
|  Precondition     | Transaction T exists. |
|                   | At least one product is attached to transaction T (T.products.length > 0). |  
|                   | Cash register is ready to modify transaction T (T.cash_register == CR). |
|  Post condition   | CR is ready for processing another transaction (CR.state == 'ready'). |
|                   | The sale transaction is recorded in the the transaction register. |
| Step#  | Description  |
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the loyalty program benefits: total = sum([p.n * p.price * (1 - discount) * (1 - loyalty_benefit) for p in products]). |
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
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the loyalty program benefits: total = sum([p.price * (1 - discount) * (1 - loyalty_benefit) for p in products]). |
|  2     | C selects the 'cash' payment method but the customer has not enough cash. A warning is raised. |


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
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the loyalty program benefits: total = sum([p.price * (1 - discount) * (1 - loyalty_benefit) for p in products]). |
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
|  1     | The cash register computes the total by reading the product prices from the catalogue and taking into account the available special offers and the loyalty program benefits: total = sum([p.price * (1 - discount) * (1 - loyalty_benefit) for p in products]). |
|  2     | The cash register CR communicates the total to the credit card POS system. |
|  3     | The credit card POS system notifies an exception. |
|  4     | The transaction is aborted or the checkout can be repeated depending on the type of the ex. |


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



# Glossary

\<use UML class diagram to define important terms, or concepts in the domain of the system, and their relationships>

\<concepts are used consistently all over the document, ex in use cases, requirements etc>

# System Design
\<describe here system design>

\<must be consistent with Context diagram>

# Deployment Diagram

\<describe here deployment diagram >

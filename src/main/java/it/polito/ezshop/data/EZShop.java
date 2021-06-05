package it.polito.ezshop.data;

import it.polito.ezshop.credit_card_circuit.CreditCardCircuit;
import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.TicketEntry;
import it.polito.ezshop.model.*;
import it.polito.ezshop.model.adapters.*;
import it.polito.ezshop.model.persistence.JsonInterface;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.*;


public class EZShop implements EZShopInterface {

    public static final String PERSISTENCE_PATH = "app_data/";

    /**
     * Simple persistence layer for EZShop.
     */
    private JsonInterface persistenceLayer;

    /**
     * Credit card circuit handling credit card payments
     */
    private CreditCardCircuit creditCardCircuit = new TextualCreditCardCircuit(TextualCreditCardCircuit.WORKING_TEXT_FILE);

    /**
     * List of all the users registered in EZShop.
     */
    private final List<it.polito.ezshop.model.User> users = new ArrayList<>();

    /**
     * Encapsulated list of all the customers registered in EZShop.
     */
    private CustomerList customerList = new CustomerList();

    /**
     * Current logged in user.
     */
    private it.polito.ezshop.model.User currentUser = null;

    /**
     * List of all products in EZShop
     */
    private final List<it.polito.ezshop.model.ProductType> products = new ArrayList<>();

    /**
     * The current date and time for the EZShop
     */
    private Clock clock = Clock.systemDefaultZone();

    /**
     * The account book holding all balance transactions (orders, sale transactions, ect.)
     */
    private AccountBook accountBook = new AccountBook();

    public EZShop () {
        this(PERSISTENCE_PATH);
    }

    public EZShop(String path) {
        try {
            this.persistenceLayer = JsonInterface.create(path);

            this.users.addAll(persistenceLayer.readUsers());
            this.products.addAll(persistenceLayer.readProducts());
            this.customerList = persistenceLayer.readCustomerList();
            this.accountBook = persistenceLayer.readAccountBook();

            this.accountBook.getSaleTransactions().forEach(sale -> {
                // iterate over all transaction
                sale.getTransactionItems().forEach(ti -> {
                    // set the product type in the ticket entry to the actual product reference
                    this.products.stream()
                            .filter(x -> x.getId() == ti.getProductType().getId())
                            .findFirst().ifPresent(ti::setProductType);
                });
            });

            this.accountBook.getReturnTransactions().forEach(_return -> {
                // iterate over all transaction
                _return.getTransactionItems().forEach(ti -> {
                    // set the product type in the item to the actual product reference
                    this.products.stream()
                            .filter(x -> x.getId() == ti.getProductType().getId())
                            .findFirst().ifPresent(ti::setProductType);
                });
            });
        } catch (Exception ex) {
            // exceptions are ignored
        }
    }

    /**
     * Get the current date and time of the EZShop system
     *
     * @return currently used clock
     */
    public Clock getClock() {
        return clock;
    }

    /**
     * Set the current date and time of the EZShop system
     *
     * @param clock clock to be used
     */
    public void setClock(Clock clock) {
        Objects.requireNonNull(clock);
        this.clock = clock;
    }

    /**
     * Set the credit card circuit that should be used to handle credit card payments for the shop
     *
     * @param creditCardCircuit credit card circuit used for payments
     */
    public void setCreditCardCircuit(CreditCardCircuit creditCardCircuit) {
        this.creditCardCircuit = creditCardCircuit;
    }

    /**
     * Write current state to the persistence layer
     */
    private void writeState () {
        try {
            persistenceLayer.writeUsers(users);
            persistenceLayer.writeCustomerList(customerList);
            persistenceLayer.writeProducts(products);
            persistenceLayer.writeAccountBook(accountBook);
        } catch (Exception ex) {
            // exceptions are ignored
        }
    }

    /**
     * Get the internal account book (use for testing purposes only)
     * @return a reference to the account book
     */
    public AccountBook getAccountBook() {
        return this.accountBook;
    }

    @Override
    public void reset() {
        this.users.clear();
        this.currentUser = null;
        this.customerList.reset();
        this.products.clear();
        this.accountBook.reset();
        this.clock = Clock.systemDefaultZone();

        // reset the credit card system
        this.creditCardCircuit.reset();

        writeState();
    }


    /**
     * Check whether the role of the current user is the expected one.
     *
     * @param roles is the list of eligible roles.
     * @throws UnauthorizedException if no user is currently logged in or if its role
     *                               is not the expected one.
     */
    private void verifyCurrentUserRole(Role... roles) throws UnauthorizedException {
        if (currentUser == null) {
            throw new UnauthorizedException("No user is currently logged in");
        }

        if (Arrays.stream(roles).noneMatch(r -> r.equals(currentUser.getRole()))) {
            throw new UnauthorizedException("Invalid current user's role");
        }
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // check that a user with the same name does not already exists
        it.polito.ezshop.model.User.validateUsername(username);
        it.polito.ezshop.model.User.validatePassword(password);
        it.polito.ezshop.model.User.validateRole(role);

        if (users.stream().anyMatch(x -> x.getUsername().equals(username))) {
            return -1;
        }

        // generate a list of all ids
        List<Integer> ids = users.stream().map(it.polito.ezshop.model.User::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list
        Integer id = generateId(ids);

        // create a new user
        it.polito.ezshop.model.User u;
        try {
            u = new it.polito.ezshop.model.User(id, username, password, role);
            users.add(u);

            writeState();
            return u.getId();
        } catch (InvalidUserIdException e) {
            throw new Error("UserId was generated improperly", e);
        }
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        it.polito.ezshop.model.User.validateId(id);

        // removeIf returns true if any elements were removed
        boolean result = users.removeIf(x -> x.getId().equals(id));

        writeState();
        return result;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);
        // return a list of users
        return users.stream().map(UserAdapter::new).collect(Collectors.toList());
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        it.polito.ezshop.model.User.validateId(id);

        return users.stream()
                // filter users with the given id
                .filter(x -> x.getId().equals(id))
                // find the first matching user
                .findFirst().map(UserAdapter::new)
                // if a matching user is not found, return null
                .orElse(null);
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        it.polito.ezshop.model.User.validateId(id);
        // check if the role is valid
        it.polito.ezshop.model.User.validateRole(Role.fromString(role));

        // find the user
        it.polito.ezshop.model.User user = users.stream()
                // filter users with the given id
                .filter(x -> x.getId().equals(id)).findFirst().orElse(null);

        // if the user is present, update its role
        if (user != null) {
            user.setRole(Role.fromString(role));
            writeState();
            return true;
        }
        return false;
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        // check that the username is neither null or empty
        it.polito.ezshop.model.User.validateUsername(username);

        // check that the password is neither null or empty
        it.polito.ezshop.model.User.validatePassword(password);

        currentUser = users.stream()
                // filters all the users with a matching username and password
                .filter(x -> x.getUsername().equals(username) && x.getPassword().equals(password))
                // get a User from the filtered list
                .findAny()
                // if one user is found return it, otherwise return null
                .orElse(null);

        if (currentUser == null) return null;
        return new UserAdapter(currentUser);
    }

    @Override
    public boolean logout() {
        // check if there's a logged in user
        boolean loggedIn = (currentUser != null);
        // logout the current user
        currentUser = null;
        // true if the logout is successful, false otherwise
        return loggedIn;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        if (products.stream().anyMatch(x -> x.getBarCode().equals(productCode))) {
            return -1;
        }

        // generate a list of all ids
        List<Integer> ids = products.stream().map(it.polito.ezshop.model.ProductType::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list
        int id = generateId(ids);

        it.polito.ezshop.model.ProductType p;
        try {
            p = new it.polito.ezshop.model.ProductType(id, description, productCode, pricePerUnit, note);

        } catch (InvalidProductIdException e) {
            // InvalidProductIdException should never occur for properly generated ID!
            throw new Error("productID was generated improperly", e);

        } catch (InvalidQuantityException e) {
            // InvalidQuantityException should never be thrown.
            throw new Error("It is impossible to initialize ProductType with negative quantity", e);
        }

        products.add(p);

        writeState();

        return p.getId();
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidProductIdException("Invalid product id less or equal to 0");
        }

        if (newDescription == null || newDescription.equals("")) {
            throw new InvalidProductDescriptionException("Invalid description");
        }

        if (!isValidBarcode(newCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        if (newPrice <= 0) {
            throw new InvalidPricePerUnitException("Price per Unit must be greater or equal than zero");
        }

        if (products.stream().anyMatch(x -> !(x.getId() == id) && x.getBarCode().equals(newCode))) {
            return false;
        }

        it.polito.ezshop.model.ProductType product = products.stream()
                // filter products with the given id
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElse(null);

        if (product == null) {
            return false;
        }

        String oldBarcode = product.getBarCode();

        product.setProductDescription(newDescription);
        product.setBarCode(newCode);
        product.setPricePerUnit(newPrice);
        product.setNote(newNote);

        // if the barcode of the product is changed, propagate the change to the orders list
        if (!oldBarcode.equals(newCode)) {
            // update the barcode of the product in the orders list
            this.accountBook.updateBarcodeInOrders(oldBarcode, newCode);
        }

        writeState();

        return true;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidProductIdException("Invalid product id less or equal to 0");
        }

        // removeIf returns true if any elements were removed
        boolean result =  products.removeIf(x -> x.getId() == id);

        writeState();
        return result;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // return a list of products
        return products.stream().map(ProductTypeAdapter::new).collect(Collectors.toList());
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that barcode is neither null or not valid
        if (!isValidBarcode(barCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        return products.stream()
                // filter users with the given id
                .filter(x -> x.getBarCode().equals(barCode))
                // find the first matching user
                .findFirst()
                // map to the interface type
                .map(ProductTypeAdapter::new)
                // if a matching user is not found, return null
                .orElse(null);
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // null should be considered as the empty string
        String query = (description == null) ? "" : description;

        return products.stream()
                // filter users with the given id
                .filter(x -> x.getProductDescription().contains(query))
                .map(ProductTypeAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        // verify current user has sufficient rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that product ID is valid
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // get product or null if it does not exist
        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(p -> p.getId() == productId)
                .findAny()
                .orElse(null);

        // check that product exists
        if (product == null) {
            return false;
        }

        // update product quantity
        try {
            product.setQuantity(product.getQuantity() + toBeAdded);
        } catch (InvalidQuantityException | IllegalStateException e) {
            return false;
        }

        writeState();
        return true;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {

        // verify current user has sufficient rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that product id is valid
        if (productId == null || productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // get product to be updated
        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(p -> p.getId() == productId)
                .findAny()
                .orElse(null);

        // if newPos is null or empty string, unassign position from product
        if (newPos == null || newPos.equals("")) {

            // if product does not exist return false
            if (product == null) {
                return false;
            }

            product.setPosition(null);
            writeState();
            return true;
        }

        // try and parse position, throw exception if format is invalid
        Position position = new Position(newPos);

        // return false if position is already taken by different product
        if (products.stream().anyMatch(p -> position.equals(p.getPosition()))) {
            return false;
        }

        // if product does not exist return false
        if (product == null) {
            return false;
        }

        // update product position if product with given ID exists
        product.setPosition(position);

        // persist changes and return true;
        writeState();
        return true;
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

        // check that barcode is valid
        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Product code must follow specification");
        }

        // check that quantity is positive value
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive integer");
        }

        // check that price is positive value
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException("Price must be positive double");
        }

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // verify product exists
        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(p -> p.getBarCode().equals(productCode))
                .findAny()
                .orElse(null);
        if (product == null) {
            return -1;
        }

        // create Order object
        int balanceId = accountBook.generateNewId();
        LocalDate date = LocalDate.now(clock);
        it.polito.ezshop.model.Order order = new it.polito.ezshop.model.Order(balanceId, date, productCode, pricePerUnit, quantity);

        // add order to account book
        this.accountBook.addTransaction(order);

        writeState();
        // return order ID on success
        return balanceId;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {

        // check that barcode is valid
        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Product code must follow specification");
        }

        // check that quantity is positive value
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive integer");
        }

        // check that price is positive value
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException("Price must be positive double");
        }

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // verify product exists
        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(p -> p.getBarCode().equals(productCode))
                .findAny()
                .orElse(null);
        if (product == null) {
            return -1;
        }

        // create Order object
        int orderID = accountBook.generateNewId();
        LocalDate date = LocalDate.now(clock);
        it.polito.ezshop.model.Order order = new it.polito.ezshop.model.Order(orderID, date, productCode, pricePerUnit, quantity);

        // ensure sufficient funds in the account book
        if (!accountBook.checkAvailability(Math.abs(order.getMoney()))) {
            return -1;
        }

        // set order state to PAID
        order.setStatus(OperationStatus.PAID);

        // record order in account book and update balance automatically
        accountBook.addTransaction(order);

        writeState();
        // return order ID on success
        return orderID;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {

        // verify orderId is valid ID
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException("Order ID must be positive integer");
        }

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // verify that order exists
        it.polito.ezshop.model.BalanceOperation transactionWithId = accountBook.getTransaction(orderId);
        if (!(transactionWithId instanceof it.polito.ezshop.model.Order)) {
            return false;
        }

        // verify that Order was either issued or already paid for
        it.polito.ezshop.model.Order order = (it.polito.ezshop.model.Order) transactionWithId;
        OperationStatus previousStatus = order.getStatus();
        if (!(previousStatus == OperationStatus.CLOSED || previousStatus == OperationStatus.PAID)) {
            return false;
        }

        // ensure sufficient funds in the account book
        if (!accountBook.checkAvailability(-order.getMoney())) {
            return false;
        }

        // set order status to paid and update account book
        accountBook.setTransactionStatus(orderId, OperationStatus.PAID);

        writeState();
        // return success of operation
        return true;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        // check that orderId is valid ID
        if (orderId == null || orderId <= 0) {
            throw new InvalidOrderIdException("Order ID must be positive integer");
        }

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // verify that order exists
        it.polito.ezshop.model.BalanceOperation transactionWithId = accountBook.getTransaction(orderId);
        if (!(transactionWithId instanceof it.polito.ezshop.model.Order)) return false;

        // verify that Order was either paid for or has already been completed
        it.polito.ezshop.model.Order order = (it.polito.ezshop.model.Order) transactionWithId;
        OperationStatus previousStatus = order.getStatus();
        if (!(previousStatus == OperationStatus.PAID || previousStatus == OperationStatus.COMPLETED)) {
            return false;
        }

        // find the product that is being reordered
        it.polito.ezshop.model.ProductType orderedProduct = products.stream()
                .filter(p -> p.getBarCode().equals(order.getProductCode()))
                .findAny()
                .orElse(null);

        // verify ordered product exists
        if (orderedProduct == null){
            return false;
        }

        // verify the product is assigned to a location
        if (orderedProduct.getPosition() == null) {
            throw new InvalidLocationException();
        }

        // update product quantity
        try {
            orderedProduct.setQuantity(orderedProduct.getQuantity() + order.getQuantity());
        } catch (InvalidQuantityException | IllegalStateException e) {
            return false;
        }

        // mark order as completed
        accountBook.setTransactionStatus(orderId, OperationStatus.COMPLETED);

        writeState();
        // return success of operation
        return true;
    }

    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, 
InvalidLocationException, InvalidRFIDException {
        return false;
    }
    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // return list of all orders
        return accountBook.getOrders().stream()
                .filter(order -> {
                    OperationStatus orderstatus = order.getStatus();
                    return orderstatus == OperationStatus.CLOSED
                            || orderstatus == OperationStatus.PAID
                            || orderstatus == OperationStatus.COMPLETED;
                })
                .map(OrderAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        int newCustomerID = customerList.addCustomer(customerName);

        writeState();
        return newCustomerID;
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {

        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        boolean success = customerList.modifyCustomer(id, newCustomerName, newCustomerCard);

        writeState();
        return success;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        boolean success = customerList.removeCustomer(id);
        writeState();
        return success;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // return customer if found, null otherwise, throw exception if necessary
        it.polito.ezshop.model.Customer customer = customerList.getCustomer(id);
        if (customer == null) {
            return null;
        }

        return new CustomerAdapter(customer);
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        return customerList.getAllCustomers().stream().map(CustomerAdapter::new).collect(Collectors.toList());
    }

    @Override
    public String createCard() throws UnauthorizedException {

        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        String newCard = customerList.generateNewLoyaltyCard();

        writeState();
        return newCard;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {

        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        boolean success = customerList.attachCardToCustomer(customerId, customerCard);

        writeState();
        return success;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {

        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        boolean success = customerList.modifyPointsOnCard(customerCard, pointsToBeAdded);

        writeState();
        return success;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        int id = accountBook.generateNewId();

        // add SaleTransaction to account book
        it.polito.ezshop.model.SaleTransaction st = new it.polito.ezshop.model.SaleTransaction(id, LocalDate.now(clock));
        this.accountBook.addTransaction(st);

        writeState();
        return id;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);
        it.polito.ezshop.model.ProductType.validateProductCode(productCode);

        // verify amount is a non-negative integer value
        TicketEntry.validateAmount(amount);

        // retrieve the OPEN sale transaction
        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus() != OperationStatus.OPEN) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;

        // retrieve the product and verify the quantity on the shelves is greater than amount
        it.polito.ezshop.model.ProductType p = products.stream()
                .filter(x -> x.getBarCode().equals(productCode) && x.getQuantity() >= amount)
                .findFirst().orElse(null);
        if (p == null) return false;

        try {
            // update the quantity on the shelves
            p.setQuantity(p.getQuantity() - amount);
            // amount the amount in the transaction
            sale.addSaleTransactionItem(p, amount);

            writeState();
            return true;
        } catch (Exception ignored) {
            // ignored exception: should never reach this point
        }
        return false;
    }


    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
        return false;
    }
    
    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws
            InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);
        it.polito.ezshop.model.ProductType.validateProductCode(productCode);

        // verify amount is a non-negative integer value
        TicketEntry.validateAmount(amount);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus() != OperationStatus.OPEN) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;

        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(x -> x.getBarCode().equals(productCode))
                .findFirst().orElse(null);
        if (product == null) return false;

        try {
            if (sale.removeSaleTransactionItem(product, amount)) {
                // update the quantity on the shelves
                product.setQuantity(product.getQuantity() + amount);
                writeState();
                return true;
            }
        } catch (Exception ignored) {
            // ignored exception: should never reach this point
        }

        return false;
    }

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);
        it.polito.ezshop.model.ProductType.validateProductCode(productCode);

        // validate the discount rate
        TicketEntry.validateDiscount(discountRate);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus() != OperationStatus.OPEN) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;

        boolean result = sale.applyDiscountToProduct(productCode, discountRate);

        writeState();
        return result;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);

        // validate the discount rate
        it.polito.ezshop.model.SaleTransaction.validateDiscount(discountRate);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus() == OperationStatus.PAID || transaction.getStatus() == OperationStatus.COMPLETED) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;

        try {
            sale.setDiscountRate(discountRate);
            writeState();
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return -1;

        return ((it.polito.ezshop.model.SaleTransaction) transaction).computePoints();
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus() != OperationStatus.OPEN) return false;

        accountBook.setTransactionStatus(transactionId, OperationStatus.CLOSED);

        writeState();
        return true;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        it.polito.ezshop.model.SaleTransaction.validateId(saleNumber);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(saleNumber);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;
        if (transaction.getStatus().affectsBalance()) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;
        // restore product quantities
        for (TicketEntry entry : sale.getTransactionItems()) {
            products.stream()
                    .filter(p -> p.getBarCode().equals(entry.getProductType().getBarCode()))
                    .findAny()
                    .ifPresent(p -> {
                        try {
                            p.setQuantity(p.getQuantity() + entry.getAmount());
                        } catch (InvalidQuantityException ignored) {
                        }
                    });
        }

        accountBook.removeTransaction(saleNumber);
        writeState();
        return true;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        it.polito.ezshop.model.SaleTransaction.validateId(transactionId);

        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(transactionId);
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return null;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) transaction;
        if (sale.getStatus() == OperationStatus.OPEN) return null;

        return new SaleTransactionAdapter(sale);
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // check that sale number is valid ID
        it.polito.ezshop.model.SaleTransaction.validateId(saleNumber);

        // get transaction with ID from account book
        it.polito.ezshop.model.BalanceOperation transaction = accountBook.getTransaction(saleNumber);

        // return -1 if transaction does not exist or if it is not a sale transaction
        if (!(transaction instanceof it.polito.ezshop.model.SaleTransaction)) return -1;

        // cast to type sale transaction
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) transaction;

        // return -1 if sale transaction has not been paid yet
        if (!saleTransaction.getStatus().affectsBalance()) return -1;

        // initialize new return transaction
        int returnId = accountBook.generateNewId();
        ReturnTransaction returnTransaction = new ReturnTransaction(returnId, saleNumber, LocalDate.now(clock));

        // add return transaction to sale transaction
        saleTransaction.addReturnTransaction(returnTransaction);

        // add ReturnTransaction to account book
        this.accountBook.addTransaction(returnTransaction);

        // return return transaction ID on success
        writeState();
        return returnId;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Return ID");
        }
        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }
        if (amount <= 0) {
            throw new InvalidQuantityException("Invalid Quantity");
        }

        it.polito.ezshop.model.BalanceOperation returnTransaction = accountBook.getTransaction(returnId);
        if (!(returnTransaction instanceof ReturnTransaction)) return false;
        if (returnTransaction.getStatus() != OperationStatus.OPEN) return false;

        ReturnTransaction _return = (ReturnTransaction) returnTransaction;

        it.polito.ezshop.model.BalanceOperation saleTransaction = accountBook.getTransaction(_return.getSaleTransactionId());
        if (!(saleTransaction instanceof it.polito.ezshop.model.SaleTransaction)) return false;

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) saleTransaction;

        it.polito.ezshop.model.ProductType product = products.stream()
                // filter products with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode))
                // find the first matching product
                .findFirst()
                // if a matching product is not found, return null
                .orElse(null);
        if (product == null) return false;

        int amountAlreadyReturned = _return.getTransactionItems().stream()
                .filter(item -> item.getBarCode().equals(productCode))
                .mapToInt(ReturnTransactionItem::getAmount).sum();

        TicketEntry ticketEntry = sale.getTransactionItems().stream()
                .filter(x -> x.getProductType().getBarCode().equals(productCode))
                .findFirst().orElse(null);

        // product is not available in the transaction
        if (ticketEntry == null || ticketEntry.getAmount() == 0) return false;

        // verify the total amount returned is below the amount in the sale transaction
        if ((amount + amountAlreadyReturned) > ticketEntry.getAmount()) return false;
        
        double value = ticketEntry.getPricePerUnit() * (1 - ticketEntry.getDiscountRate()) * (1 - sale.getDiscountRate());
        _return.addReturnTransactionItem(product, amount, value);

        writeState();
        return true;
    }

    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException 
    {
        return false;
    }


    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // verify returnId
        if(returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Return ID");
        }

        // get return transaction
        it.polito.ezshop.model.BalanceOperation returnTransaction = accountBook.getTransaction(returnId);

        // return false if return transaction doesn't exist
        if (!(returnTransaction instanceof ReturnTransaction)) return false;
        // return false if return transaction is not in an OPEN state
        if (returnTransaction.getStatus() != OperationStatus.OPEN) return false;

        // cast return transaction
        ReturnTransaction _return = (ReturnTransaction) returnTransaction;

        // get sale transaction
        it.polito.ezshop.model.BalanceOperation saleT = accountBook.getTransaction(_return.getSaleTransactionId());
        if (!(saleT instanceof it.polito.ezshop.model.SaleTransaction)) return false;

        // cast the sale transaction
        it.polito.ezshop.model.SaleTransaction saleTransaction = (it.polito.ezshop.model.SaleTransaction) saleT;

        // rollback
        if (!commit) {

            // delete return transaction from sale transaction
            saleTransaction.removeReturnTransaction(returnId);

            // delete return transaction from account book
            accountBook.removeTransaction(returnId);

            // roll back performed successfully
            writeState();
            return true;
        }

        // before committing verify the quantities of the returned products are valid
        // the following condition may become false if multiple return transactions are
        // happening at the same time: the total quantity returned for each product
        // may exceed the quantity in the sale transaction
        boolean valid = _return.getTransactionItems().stream().allMatch(rti -> {
            int qty = saleTransaction.getTransactionItems().stream()
                    .filter(te -> te.getProductType().getBarCode().equals(rti.getBarCode()))
                    .map(TicketEntry::getAmount).findAny().orElse(-1);

            return rti.getAmount() <= qty;
        });
        if (!valid) return false;

        // commit
        // for each item of the sale transaction
        for (TicketEntry saleTransactionItem:saleTransaction.getTransactionItems()) {

            // get the corresponding return transaction item
            ReturnTransactionItem returnTransactionItem = _return.getTransactionItems().stream()
                    .filter(rti -> rti.getBarCode().equals(saleTransactionItem.getProductType().getBarCode()))
                    .findAny()
                    .orElse(null);

            // if some items of this product were returned we need to increase their amount in the shop and decrease the
            //  amount in the transaction
            if (returnTransactionItem != null) {

                // increase the amount in the shop
                // get product
                it.polito.ezshop.model.ProductType product = products.stream()
                        .filter(p -> p.getBarCode().equals(returnTransactionItem.getBarCode()))
                        .findAny()
                        .orElse(null);
                // increase available amount if product still exists
                if (product != null) {
                    try {
                        product.setQuantity(product.getQuantity() + returnTransactionItem.getAmount());
                    } catch (InvalidQuantityException e) {
                        // this should never happen, quantity can always be increased
                        throw new Error("Unexpected error encountered when handling return transaction.", e);
                    }
                }

                // reduce the amount in the sale transaction
                try {
                    saleTransactionItem.setAmount(saleTransactionItem.getAmount() - returnTransactionItem.getAmount());
                } catch (InvalidQuantityException e) {
                    // this should never happen, you can't return more products than you purchased
                    throw new Error("Unexpected error encountered when handling return transaction.", e);
                }
            }
        }

        // set status of return transaction to CLOSED
        accountBook.setTransactionStatus(returnId, OperationStatus.CLOSED);

        // write state and return successfully
        writeState();
        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // verify returnId
        if (returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Return ID");
        }

        it.polito.ezshop.model.BalanceOperation returnTransaction = accountBook.getTransaction(returnId);
        if (!(returnTransaction instanceof ReturnTransaction)) return false;

        // return false if the return transaction hasn't been paid yet
        if (returnTransaction.getStatus().affectsBalance()) return false;

        this.accountBook.removeTransaction(returnId);

        writeState();
        return true;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(ticketNumber == null || ticketNumber <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //if the cash is less than or equal to 0
        if(cash <= 0)
            throw new InvalidPaymentException("Invalid cash amount.");

        // get sale transaction
        it.polito.ezshop.model.BalanceOperation balanceOperation = accountBook.getTransaction(ticketNumber);

        // return -1 if transaction doesn't exist or is not a sale transaction
        if (!(balanceOperation instanceof it.polito.ezshop.model.SaleTransaction)) {
            return -1;
        }

        // return -1 if transaction is not in closed state
        if (balanceOperation.getStatus() != OperationStatus.CLOSED) {
            return -1;
        }

        // calculate change
        double change = cash - Math.abs(balanceOperation.getMoney());

        // return -1 if cash is not enough
        if (change < 0) {
            return -1;
        }

        // set transaction status to COMPLETED and automatically update balance
        accountBook.setTransactionStatus(ticketNumber, OperationStatus.COMPLETED);

        writeState();
        return change;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // if the number is less than or equal to 0 or if it is null
        if(ticketNumber == null || ticketNumber <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        // if the credit card number is empty, null or if luhn algorithm does not validate the credit card
        if(!isValidCreditCardNumber(creditCard))
            throw new InvalidCreditCardException("Invalid credit card.");

        // get sale from account book
        it.polito.ezshop.model.BalanceOperation sale = accountBook.getTransaction(ticketNumber);

        // return false if sale does not exist
        if (!(sale instanceof it.polito.ezshop.model.SaleTransaction)) {
            return false;
        }

        // return false if state is not CLOSED
        if (sale.getStatus() != OperationStatus.CLOSED) {
            return false;
        }

        // get the price to be paid by card
        double saleValue = sale.getMoney();

        // return false if the credit card has insufficient balance
        if (!creditCardCircuit.checkAvailability(creditCard, saleValue)) {
            return false;
        }

        // try to reduce funds on credit card for payment, return false on failure
        if (!creditCardCircuit.addDebit(creditCard, saleValue)) {
            return false;
        }

        // change transaction status to COMPLETED and automatically updated shop balance
        accountBook.setTransactionStatus(ticketNumber, OperationStatus.COMPLETED);

        // presist and return successfully
        writeState();
        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {

        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        // get transaction
        it.polito.ezshop.model.BalanceOperation balanceOperation = accountBook.getTransaction(returnId);

        // return -1 if return transaction does not exist
        if (!(balanceOperation instanceof ReturnTransaction)) {
            return -1;
        }

        // return -1 if state is not closed
        if (balanceOperation.getStatus() != OperationStatus.CLOSED) {
            return -1;
        }

        // set status of transaction to completed and automatically update balance
        accountBook.setTransactionStatus(returnId, OperationStatus.COMPLETED);

        // persist state and return on success
        writeState();
        return Math.abs(balanceOperation.getMoney());
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {

        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // if the  number is less than or equal to 0 or if it is null
        if(returnId == null || returnId <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //if the credit card number is empty, null or if luhn algorithm does not validate the credit card
        if (!isValidCreditCardNumber(creditCard))
            throw new InvalidCreditCardException("Invalid credit card number.");

        // get return transaction
        it.polito.ezshop.model.BalanceOperation returnT = accountBook.getTransaction(returnId);

        // return -1 if the return transaction does not exist
        if (!(returnT instanceof ReturnTransaction)) {
            return -1;
        }

        // return -1 if the return transaction is not in CLOSED state
        if (returnT.getStatus() != OperationStatus.CLOSED) {
            return -1;
        }

        // get the value of the return transaction
        double returnValue = Math.abs(returnT.getMoney());

        // try to add funds to credit card, return -1 if operation fails
        if (!creditCardCircuit.addCredit(creditCard, returnValue)) {
            return -1;
        }

        // change transaction status to COMPLETED and automatically updated shop balance
        accountBook.setTransactionStatus(returnId, OperationStatus.COMPLETED);

        // persist and return amount of money that was credited to the customer
        writeState();
        return returnValue;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {

        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // set parameters
        int balanceId = accountBook.generateNewId();
        LocalDate date = LocalDate.now(clock);
        OperationStatus newStatus = OperationStatus.COMPLETED;

        if (toBeAdded >= 0) {

            // record positive balance update
            accountBook.addTransaction(new Credit(balanceId, date, toBeAdded, newStatus));
        } else {

            // if balance would be decreased below zero, return false
            if (!accountBook.checkAvailability(-toBeAdded)) {
                return false;
            }
            // record negative balance update
            accountBook.addTransaction(new Debit(balanceId, date, -toBeAdded, newStatus));
        }

        writeState();
        return true;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        final LocalDate actualFrom, actualTo;
        if (from != null && to != null && from.isAfter(to)) {
            // swap from and to
            actualFrom = to;
            actualTo = from;
        } else {
            actualFrom = from;
            actualTo = to;
        }

        // collect all transactions
        return accountBook.getAllTransactions().stream()
                .filter(x -> actualFrom == null || x.getDate().compareTo(actualFrom) >= 0)
                .filter(x -> actualTo == null || x.getDate().compareTo(actualTo) <= 0)
                .filter(x -> x.getStatus().affectsBalance())
                .map(BalanceOperationAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public double computeBalance() throws UnauthorizedException {

        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        return accountBook.computeBalance();
    }
}

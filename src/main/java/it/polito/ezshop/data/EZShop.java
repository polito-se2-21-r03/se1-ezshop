package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;
import it.polito.ezshop.model.adapters.*;
import it.polito.ezshop.model.persistence.JsonInterface;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.*;


public class EZShop implements EZShopInterface {

    public static final String PERSISTENCE_PATH = "tmp/";

    /**
     * Simple persistence layer for EZShop.
     */
    private JsonInterface persistenceLayer;

    /**
     * List of all the users registered in EZShop.
     */
    private final List<it.polito.ezshop.model.User> users = new ArrayList<>();

    /**
     * List of all the customers registered in EZShop.
     */
    private final List<it.polito.ezshop.model.Customer> customers = new ArrayList<>();

    /**
     * List of loyalty card registered in the system
     */
    public final List<LoyaltyCard> loyaltyCards = new ArrayList<>();

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
            this.customers.addAll(persistenceLayer.readCustomers());
            this.loyaltyCards.addAll(persistenceLayer.readLoyaltyCards());
            this.accountBook = persistenceLayer.readAccountBook();
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
     * Write current state to the persistence layer
     */
    private void writeState () {
        try {
            persistenceLayer.writeUsers(users);
            persistenceLayer.writeCustomers(customers);
            persistenceLayer.writeProducts(products);
            persistenceLayer.writeAccountBook(accountBook);
        } catch (Exception ex) {
            // exceptions are ignored
        }
    }

    @Override
    public void reset() {
        this.users.clear();
        this.currentUser = null;
        this.customers.clear();
        this.products.clear();
        this.accountBook.reset();

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

    /**
     * Returns the customer with the given ID. If no customer with that ID exists null is returned. Throws an
     * InvalidCustomerIdException if the given ID is not valid
     *
     * @param id ID of the requested customer
     * @return the customer with the given id if he exists
     *         null, if no customer with id exists
     * @throws InvalidCustomerIdException if the provided ID is not valid
     */
    private it.polito.ezshop.model.Customer getCustomerById(Integer id) throws InvalidCustomerIdException {
        if (id == null || id <= 0) {
            throw new InvalidCustomerIdException("The customer id must be a non-null positive integer");
        }

        return customers.stream()
                .filter(c -> id.equals(c.getId()))
                .findAny()
                .orElse(null);
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

        product.setProductDescription(newDescription);
        product.setBarCode(newCode);
        product.setPricePerUnit(newPrice);
        product.setNote(newNote);

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
        if (productId <= 0) {
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
        if (productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // get product to be updated
        it.polito.ezshop.model.ProductType product = products.stream()
                .filter(p -> p.getId() == productId)
                .findAny()
                .orElse(null);

        // if product does not exist return false
        if (product == null) {
            return false;
        }

        // if newPos is null or empty string, unassign position from product
        if (newPos == null || newPos.equals("")) {
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
        // issue the order
        int orderId = this.issueOrder(productCode, quantity, pricePerUnit);

        // verify order was issued correctly
        if (orderId <= 0) {
            return -1;
        }

        // try to pay order
        boolean orderPayedSuccessfully;
        try {
            orderPayedSuccessfully = this.payOrder(orderId);
        } catch (InvalidOrderIdException e) {
            return -1;
        }

        // rollback issuing of order if funds are insufficient for paying
        if (!orderPayedSuccessfully) {
            accountBook.removeTransaction(orderId);
        }

        writeState();
        // return order ID on success
        return orderId;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        // verify orderId is valid ID
        if (orderId <= 0) {
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
        if (!accountBook.checkAvailability(order.getMoney())) {
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
        if (orderId <= 0) {
            throw new InvalidOrderIdException("Order ID must be positive integer");
        }

        // verify access rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // verify that order exists
        it.polito.ezshop.model.BalanceOperation transactionWithId = accountBook.getTransaction(orderId);
        if (transactionWithId == null || !it.polito.ezshop.model.Order.class.isAssignableFrom(transactionWithId.getClass())) {
            return false;
        }

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
        if (orderedProduct == null) {
            throw new InvalidLocationException("The product specified in this order does not exist");
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
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // validate the name parameter
        it.polito.ezshop.model.Customer.validateName(customerName);

        // unique name checking
        boolean alreadyRegisteredName = customers.stream()
                .map(it.polito.ezshop.model.Customer::getCustomerName)
                .anyMatch(x -> x.equals(customerName));

        if (alreadyRegisteredName) {
            return -1;
        }

        // generate a new id that is not already in the list
        // create a new customer
        List<Integer> ids = customers.stream().map(it.polito.ezshop.model.Customer::getId).collect(Collectors.toList());
        Integer id = generateId(ids);

        try {
            customers.add(new it.polito.ezshop.model.Customer(id, customerName, null));

            writeState();
            return id;
        } catch (InvalidCustomerIdException e) {
            throw new Error("Customer ID was generated improperly", e);
        }
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {

        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (newCustomerName == null || newCustomerName.equals("")) {
            throw new InvalidCustomerNameException("Invalid Customer Name");
        }
        if (newCustomerCard == null || newCustomerCard.equals("") || newCustomerCard.length() == 10) {
            throw new InvalidCustomerCardException("Invalid Card Number");
        }

        // get the customer
        it.polito.ezshop.model.Customer customer = getCustomerById(id);

        // return false if customer does not exist
        if (customer == null) {
            return false;
        }

        // generate a list of all card numbers
        List<String> cards = customers.stream().map(it.polito.ezshop.model.Customer::getCustomerName).collect(Collectors.toList());
        // uniqie card number checking
        for(String card : cards){
            if(card.equals(newCustomerCard))
                throw new InvalidCustomerCardException("Invalid Card Number");
        }


        // if the customer is present, update his card and name
        customer.setCustomerName(newCustomerName);

        LoyaltyCard card = loyaltyCards.stream().filter(x -> x.getCode().equals(newCustomerCard))
                .findAny().orElse(null);

        customer.setCard(card);

        writeState();
        // if the customer is present return true, otherwise return false
        return true;

    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //InvalidCustomerIdException if the ***id is null***, less than or equal to 0
        if (id == null || id <= 0) {
            throw new InvalidCustomerIdException("Invalid Customer id");
        }


        //removeIf returns true if any elements were removed
        boolean result = customers.removeIf(x -> x.getId().equals(id));

        writeState();
        return result;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // return customer if found, null otherwise, throw exception if necessary
        it.polito.ezshop.model.Customer customer = getCustomerById(id);
        if (customer == null) {
            return null;
        }

        return new CustomerAdapter(customer);
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        return customers.stream().map(CustomerAdapter::new).collect(Collectors.toList());
    }

    @Override
    public String createCard() throws UnauthorizedException {
        // invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        List<String> codes = loyaltyCards.stream().map(LoyaltyCard::getCode).collect(Collectors.toList());

        // generate a new card
        String newCode = LoyaltyCard.generateNewCode();
        while (codes.contains(newCode)) {
            newCode = LoyaltyCard.generateNewCode();
        }

        try {
            loyaltyCards.add(new LoyaltyCard(newCode, 0));
            return newCode;
        } catch (InvalidCustomerCardException e) {
            throw new Error("Card ID was generated improperly", e);
        }
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //InvalidCustomerCardException if the card is null, empty or in an invalid format
        if (customerCard == null || customerCard.length() != 10) {
            throw new InvalidCustomerCardException("Invalid Customer Card");
        }

        // check if another customer is associated with the same card
        boolean alreadyAssigned = customers.stream()
                .filter(x -> !x.getId().equals(customerId)) // not same customer
                .map(it.polito.ezshop.model.Customer::getCard)
                .anyMatch(x -> x != null && x.getCode().equals(customerCard));

        if (alreadyAssigned) {
            return false;
        }

        // get the customer
        it.polito.ezshop.model.Customer customer = customers.stream()
                .filter(x -> x.getId().equals(customerId)).findFirst().orElse(null);

        if (customer == null) {
            return false;
        }

        LoyaltyCard card = loyaltyCards.stream().filter(x -> x.getCode().equals(customerCard)).findAny().orElse(null);

        // set customer's card to new card
        customer.setCard(card);

        writeState();
        return true;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // validate the card code
        LoyaltyCard.validateCode(customerCard);

        // find the card
        LoyaltyCard card = loyaltyCards.stream()
                .filter(x -> x != null && x.getCode().equals(customerCard))
                .findFirst().orElse(null);

        // if there is no card with given code return false
        if (card == null) {
            return false;
        }

        if (card.updatePoints(pointsToBeAdded)) {
            writeState();
            return true;
        }

        return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        int id = accountBook.generateNewId();
        it.polito.ezshop.model.SaleTransaction st = new it.polito.ezshop.model.SaleTransaction(id, LocalDate.now(clock));

        // add SaleTransaction to account book
        this.accountBook.addTransaction(st);

        writeState();
        return id;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        if (amount <= 0) {
            throw new InvalidQuantityException("Product quantity must be greater than 0");
        }

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (sale == null || sale.getStatus() != OperationStatus.OPEN){
            return false;
        }

        it.polito.ezshop.model.ProductType p = products.stream()
                // filter users with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode) && x.getQuantity() >= amount)
                // find the first matching product
                .findFirst().orElse(null);
        if (p == null) {
            return false;
        }

        // update the quantity on the shelves
        try {
            p.setQuantity(p.getQuantity() - amount);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return false;
        }

        sale.addSaleTransactionItem(p, amount, p.getPricePerUnit(), 0);

        writeState();
        return true;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        if (amount <= 0) {
            throw new InvalidQuantityException("Product quantity must be greater than 0");
        }

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (sale == null || !(sale.getStatus() == OperationStatus.OPEN)) {
            return false;
        }

        it.polito.ezshop.model.ProductType product = products.stream()
                // filter users with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode))
                // find the first matching product
                .findFirst()
                // if a matching product is not found, return null
                .orElse(null);
        if (product == null) {
            return false;
        }

        if (sale.removeSaleTransactionItem(product, amount)) {
            // update the quantity on the shelves
            try {
                product.setQuantity(product.getQuantity() + amount);
            } catch (IllegalArgumentException |IllegalStateException e) {
                return false;
            }

            writeState();
            return true;
        }

        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        if (discountRate >= 1.00 || discountRate < 0) {
            throw new InvalidDiscountRateException("Discount Rate must be between 0 and 1");
        }

        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (sale == null || !(sale.getStatus() == OperationStatus.OPEN)) {
            return false;
        }

        boolean result = sale.applyDiscountToProduct(productCode, discountRate);

        writeState();
        return result;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (transactionId == null || transactionId <= 0) {
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        if (discountRate >= 1.00 || discountRate < 0) {
            throw new InvalidDiscountRateException("Discount Rate must be between 0 and 1");
        }

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null) {
            return false;
        }
        if (t.getStatus() == OperationStatus.PAID || t.getStatus() == OperationStatus.COMPLETED) {
            return false;
        }

        t.setDiscountRate(discountRate);
        writeState();
        return true;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null){
            return -1;
        }

        return t.computePoints();
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        if (transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (sale == null){
            return false;
        }
        if (sale.getStatus() == OperationStatus.CLOSED){
            return false;
        }
        sale.setMoney(sale.computeTotal());
        accountBook.setTransactionStatus(transactionId, OperationStatus.CLOSED);

        writeState();
        return true;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        if (saleNumber == null || saleNumber <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(saleNumber);
        if (t == null || t.getStatus() == OperationStatus.PAID){
            return false;
        }

        accountBook.removeTransaction(saleNumber);
        writeState();
        return true;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        if (transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (sale == null){
            return null;
        }
        if(!(sale.getStatus() == OperationStatus.CLOSED)){
            return null;
        }

        return new SaleTransactionAdapter(sale);
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        int returnId = accountBook.generateNewId();
        if (returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(saleNumber);
        if (sale == null || !(sale.getStatus() == OperationStatus.CLOSED)) {
            return -1;
        }

        ReturnTransaction rt = new ReturnTransaction(returnId, saleNumber, LocalDate.now(clock));
        sale.addReturnTransaction(rt);

        // add ReturnTransaction to account book
        this.accountBook.addTransaction(rt);

        writeState();
        return returnId;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if(returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Return ID");
        }
        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }
        if (amount <= 0){
            throw new InvalidQuantityException("Invalid Quantity");
        }

        ReturnTransaction rt = (ReturnTransaction) accountBook.getTransaction(returnId);
        if(rt == null){
            return false;
        }
        if(!(rt.getStatus() == OperationStatus.OPEN)){
            return false;
        }
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(rt.getSaleTransactionId());
        if(sale == null){
            return false;
        }
        it.polito.ezshop.model.ProductType product = products.stream()
                // filter products with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode))
                // find the first matching product
                .findFirst()
                // if a matching product is not found, return null
                .orElse(null);
        if (product == null){
            return false;
        }
        Optional<it.polito.ezshop.model.TicketEntry> entry = sale.getTransactionItems()
                .stream()
                .filter(x -> x.getProductType().getBarCode().equals(productCode))
                .findFirst();
        if (!entry.isPresent()) {
            return false;
        }

        if(amount > entry.get().getAmount()){
            return false;
        }

        double value = entry.get().getPricePerUnit() * (1-entry.get().getDiscountRate()) * (1-sale.getDiscountRate());
        rt.addReturnTransactionItem(product, amount, value);

        writeState();
        return true;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if(returnId == null || returnId <= 0) {
            throw new InvalidTransactionIdException("Invalid Return ID");
        }
        ReturnTransaction rt = (ReturnTransaction) accountBook.getTransaction(returnId);
        if(rt == null){
            return false;
        }
        if(rt.getStatus() != OperationStatus.OPEN){
            return false;
        }
        it.polito.ezshop.model.SaleTransaction sale = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(rt.getSaleTransactionId());
        if(sale == null){
            return false;
        }
        if(commit){
            List <ReturnTransactionItem> ritems = rt.getTransactionItems();
            List <it.polito.ezshop.model.TicketEntry> titems = sale.getTransactionItems();
            //didn't know how to use .stream in this case lol

            for(int i = 0; i < ritems.size(); i++) {
                ReturnTransactionItem ritem = ritems.get(i);
                it.polito.ezshop.model.TicketEntry titem = titems.stream()
                        // filter products with the given BarCode
                        .filter(x -> x.getProductType().getBarCode().equals(ritem.getBarCode()))
                        // find the first matching product
                        .findFirst()
                        // if a matching product is not found, return null
                        .orElse(null);
                it.polito.ezshop.model.ProductType p = products.stream()
                        // filter products with the given BarCode
                        .filter(x -> x.getBarCode().equals(ritem.getBarCode()))
                        // find the first matching product
                        .findFirst()
                        // if a matching product is not found, return null
                        .orElse(null);
                if (p == null || titem == null) {
                    return false;
                }
                //change quantity of Product p by adding the returned amount
                try {
                    p.setQuantity(p.getQuantity() + ritem.getAmount());
                } catch (InvalidQuantityException | IllegalStateException e) {
                    return false;
                }
                //change quantity of Product p inside the transaction by reducing the returned amount
                titem.setAmount(titem.getAmount() - ritem.getAmount());
                //??? do we need to change the final price or it does automatically after removing the items ???
            }
            accountBook.setTransactionStatus(returnId, OperationStatus.CLOSED);
        }
        else{
            //anything else to do if commit == false?
            accountBook.setTransactionStatus(returnId, OperationStatus.CLOSED);
        }

        writeState();
        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(ticketNumber == null || ticketNumber.compareTo(0) <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //if the cash is less than or equal to 0
        if(cash <= 0)
            throw new InvalidPaymentException("Invalid cash amount.");

        //get sale price information
        double salePrice = accountBook.getTransaction(ticketNumber).getMoney();

        // TODO: add writeState()

        //calculate the return amount ***there need to chech for "if the sale does not exists and if there is some problemi with the db"***
        if((cash-salePrice) >= 0)
            return cash - salePrice;
        else
            return -1;

    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(ticketNumber == null || ticketNumber.compareTo(0) <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //if the credit card number is empty, null or if luhn algorithm does not validate the credit card
        if(creditCard.isEmpty() || !isValidCreditCardNumber(creditCard) )
            throw new InvalidCreditCardException("Invalid credit card.");

        //get the sale price information
        double salePrice = accountBook.getTransaction(ticketNumber).getMoney();

        /* The credit card should be registered in the system.
        *  */

        // TODO: add writeState()
        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(returnId == null || returnId.compareTo(0) <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //get the transaction and sale price information
        ReturnTransaction returnTransaction = accountBook.getReturnTransactions().stream()
                .filter(x -> x.getBalanceId() == returnId)
                .findFirst()
                .orElse(null);

        assert returnTransaction != null;
        double returnAmount = returnTransaction.getMoney();

        //if the return transaction is not ended,
        if(returnTransaction.getStatus() == OperationStatus.OPEN)
            return -1;
        //the money returned to the customer
        else
            return returnAmount;

        // TODO: add writeState()
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //if the  number is less than or equal to 0 or if it is null
        if(returnId == null || returnId.compareTo(0) <= 0)
            throw new InvalidTransactionIdException("Invalid ticket number.");

        //if the credit card number is empty, null or if luhn algorithm does not validate the credit card
        if (creditCard.isEmpty() || !isValidCreditCardNumber(creditCard))
            throw new InvalidCreditCardException("Invalid credit card number.");

        //find the returntransaction and the return amount
        ReturnTransaction returnTransaction = accountBook.getReturnTransactions().stream()
                .filter(x -> x.getBalanceId() == returnId)
                .findFirst()
                .orElse(null);

        assert returnTransaction != null;
        double returnAmount = returnTransaction.getMoney();

        //if the return transaction is not ended,
        if(returnTransaction.getStatus() == OperationStatus.OPEN)
            return -1;
        //*** I should registered card control ***
            //the money returned to the customer
        else
            return returnAmount;

        // TODO: add writeState()
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        //date
        LocalDate date = LocalDate.now(clock);
        // create Order object
        int balanceId = accountBook.generateNewId();
        //status
        OperationStatus newStatus = OperationStatus.PAID;

        it.polito.ezshop.model.BalanceOperation newRecord;
        if (toBeAdded >= 0) {
            newRecord = new Credit(balanceId, date, toBeAdded, newStatus);
        } else {
            if (!accountBook.checkAvailability(-toBeAdded)) {
                return false;
            }

            newRecord = new Debit(balanceId, date, -toBeAdded, newStatus);
        }
        accountBook.addTransaction(newRecord);

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
                .map(BalanceOperationAdapter::new)
                .collect(Collectors.toList());
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        //collect all the balance records to calculate
        List<Double> moneyList = accountBook.getAllTransactions().stream()
                .map(it.polito.ezshop.model.BalanceOperation::getMoney).collect(Collectors.toList());

        //sum all of the moneys
        double total = 0;
        for(double money:moneyList ){
            total += money;
        }

        return total;
    }
}

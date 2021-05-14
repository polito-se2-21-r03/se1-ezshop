package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.*;


public class EZShop implements EZShopInterface {

    /**
     * List of all the users registered in EZShop.
     */
    private final List<User> users = new ArrayList<>();

    /**
     * List of all the customers registered in EZShop.
     */
    private final List<Customer> customers = new ArrayList<>();

    /**
     * Current logged in user.
     */
    private User currentUser = null;

    /**
     * List of all products in EZShop
     */
    private final List<ProductType> products = new ArrayList<>();


    /**
     * The account book holding all balance transactions (orders, sale transactions, ect.)
     */
    private final AccountBook accountBook = new AccountBook();




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

        if (Arrays.stream(roles).map(Role::getValue).noneMatch(r -> r.equals(currentUser.getRole()))) {
            throw new UnauthorizedException("Invalid current user's role");
        }
    }

    @Override
    public void reset() {
        this.users.clear();
        this.currentUser = null;
        this.customers.clear();
        this.products.clear();
        this.accountBook.reset();
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        // check that the username is neither null or empty
        if (username == null || username.equals("")) {
            throw new InvalidUsernameException("Username can not be null or empty");
        }

        // check that a user with the same name does not already exists
        if (users.stream().anyMatch(x -> x.getUsername().equals(username))) {
            return -1;
        }

        // check that the password is neither null or empty
        if (password == null || password.equals("")) {
            throw new InvalidPasswordException("Password can not be null or empty");
        }

        // check if the role is valid
        if (Role.fromString(role) == null) {
            throw new InvalidRoleException("Invalid role");
        }

        // generate a list of all ids
        List<Integer> ids = users.stream().map(User::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list
        Integer id = generateId(ids);

        // create a new user
        User u = new it.polito.ezshop.model.User(id, username, password, Role.fromString(role));
        users.add(u);

        return u.getId();
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidUserIdException("Invalid user id less or equal to 0");
        }

        // removeIf returns true if any elements were removed
        return users.removeIf(x -> x.getId().equals(id));
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);
        // return an unmodifiable list of users
        return Collections.unmodifiableList(users);
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidUserIdException("Invalid user id less or equal to 0");
        }

        return users.stream()
                // filter users with the given id
                .filter(x -> x.getId().equals(id))
                // find the first matching user
                .findFirst()
                // if a matching user is not found, return null
                .orElse(null);
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR);

        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidUserIdException("Invalid user id less or equal to 0");
        }


        // check if the role is valid
        if (Role.fromString(role) == null) {
            throw new InvalidRoleException("Invalid role");
        }

        // find the user
        Optional<User> user = users.stream()
                // filter users with the given id
                .filter(x -> x.getId().equals(id)).findFirst();

        // if the user is present, update its role
        user.ifPresent(value -> value.setRole(role));

        // if the user is present return true, otherwise return false
        return user.isPresent();
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        // check that the username is neither null or empty
        if (username == null || username.equals("")) {
            throw new InvalidUsernameException("Username can not be null or empty");
        }

        // check that the password is neither null or empty
        if (password == null || password.equals("")) {
            throw new InvalidPasswordException("Password can not be null or empty");
        }

        currentUser = users.stream()
                // filters all the users with a matching username and password
                .filter(x -> x.getUsername().equals(username) && x.getPassword().equals(password))
                // get a User from the filtered list
                .findAny()
                // if one user is found return it, otherwise return null
                .orElse(null);

        return currentUser;
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

        // create a new product
        if (description == null || description.equals("")) {
            throw new InvalidProductDescriptionException("Invalid description");
        }

        if (!isValidBarcode(productCode)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }

        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException("Price per Unit must be greater or equal than zero");
        }

        if (products.stream().anyMatch(x -> x.getBarCode().equals(productCode))) {
            return -1;
        }

        // generate a list of all ids
        List<Integer> ids = products.stream().map(ProductType::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list
        int id = generateId(ids);

        // if null an empty string should be saved as note
        if (note == null) {
            note = "";
        }
        ProductType p = new it.polito.ezshop.model.ProductType(note, description, productCode, pricePerUnit, id);
        products.add(p);

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

        if (products.stream().anyMatch(x -> !x.getId().equals(id) && x.getBarCode().equals(newCode))) {
            return false;
        }

        Optional<ProductType> product = products.stream()
                // filter products with the given id
                .filter(x -> x.getId().equals(id)).findFirst();
        product.ifPresent(value -> {
            value.setProductDescription(newDescription);
            value.setBarCode(newCode);
            value.setPricePerUnit(newPrice);
            value.setNote(newNote);
        });

        return product.isPresent();
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
        return products.removeIf(x -> x.getId().equals(id));
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // return an unmodifiable list of products
        return Collections.unmodifiableList(products);

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
                .filter(x -> x.getProductDescription().contains(query)).collect(Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        // check that product ID is valid
        if (productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // verify current user has sufficient rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // get product or null if it does not exist
        ProductType product = products.stream()
                .filter(p -> p.getId().equals(productId))
                .findAny()
                .orElse(null);

        // check that product exists
        if (product == null) {
            return false;
        }

        // check that product has a specified position
        if (product.getLocation() == null || product.getLocation().equals("")) {
            return false;
        }

        // check that resulting quantity is non-negative
        if (product.getQuantity() + toBeAdded < 0) {
            return false;
        }

        // update product quantity
        product.setQuantity(product.getQuantity() + toBeAdded);

        return true;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {

        // check that product id is valid
        if (productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // verify current user has sufficient rights
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // check that position has valid format
        Position position = Position.parsePosition(newPos);
        if (position == null) {
            throw new InvalidLocationException("Position must be of form <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>");
        }

        // check that no product already has given position
        Optional<ProductType> productAtPosition = products.stream()
                .filter(p -> newPos.equals(p.getLocation()))
                .findFirst();
        if (productAtPosition.isPresent()) {
            return false;
        }

        // get product to be updated
        Optional<ProductType> productWithID = products.stream()
                .filter(p -> p.getId().equals(productId))
                .findAny();

        // update product position if product with given ID exists
        productWithID.ifPresent(p -> p.setLocation(newPos));

        // return true iff product exists
        return productWithID.isPresent();
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
        ProductType product = products.stream()
                .filter(p -> p.getBarCode().equals(productCode))
                .findAny()
                .orElse(null);
        if (product == null) {
            return -1;
        }

        // create Order object
        int balanceId = accountBook.generateNewId();
        LocalDate date = LocalDate.now();
        it.polito.ezshop.model.Order order = new it.polito.ezshop.model.Order(balanceId, date, productCode, pricePerUnit, quantity);

        // add order to account book
        this.accountBook.addTransaction(order);

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
        OperationStatus previousStatus = OperationStatus.valueOf(order.getStatus());
        if (!(previousStatus == OperationStatus.CLOSED || previousStatus == OperationStatus.PAID)) {
            return false;
        }

        // ensure sufficient funds in the account book
        if (!accountBook.checkAvailability(order.getMoney())) {
            return false;
        }

        // set order status to paid and update account book
        accountBook.setTransactionStatus(orderId, OperationStatus.PAID);

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
        OperationStatus previousStatus = OperationStatus.valueOf(order.getStatus());
        if (!(previousStatus == OperationStatus.PAID || previousStatus == OperationStatus.COMPLETED)) {
            return false;
        }

        // find the product that is being reordered
        ProductType orderedProduct = products.stream()
                .filter(p -> p.getBarCode().equals(order.getProductCode()))
                .findAny()
                .orElse(null);

        // verify ordered product exists
        if (orderedProduct == null) {
            throw new InvalidLocationException("The product specified in this order does not exist");
        }

        // verify product has valid location
        Position productLocation = Position.parsePosition(orderedProduct.getLocation());
        if (productLocation == null) {
            throw new InvalidLocationException("The product does not have a location assigned to it");
        }

        // update product quantity
        orderedProduct.setQuantity(orderedProduct.getQuantity() + order.getQuantity());

        // mark order as completed
        accountBook.setTransactionStatus(orderId, OperationStatus.COMPLETED);

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
                    OperationStatus orderstatus = OperationStatus.valueOf(order.getStatus());
                    return orderstatus == OperationStatus.CLOSED
                            || orderstatus == OperationStatus.PAID
                            || orderstatus == OperationStatus.COMPLETED;
                })
                .map(OrderInterface::new)
                .collect(Collectors.toList());
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        // generate a list of all ids
        List<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list

        // create a new customer
        Integer id = generateId(ids);
        Integer points = null;
        String customerCard = null;

        if (customerName == null || customerName.equals("")) {
            throw new InvalidCustomerNameException("Customer name can not be null or empty");
        }
        if (id == null || id <= 0) {
            return -1;
        }

        Customer c = new it.polito.ezshop.model.Customer(customerName, customerCard, id, points);
        customers.add(c);


        return c.getId();
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        // check the role of the current user
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        if (newCustomerName == null || newCustomerName.equals("")) {
            throw new InvalidCustomerNameException("Invalid Customer Name");
        }
        if (id == null || id.compareTo(0) >0){
            throw new InvalidCustomerIdException("Invalid customer ID");
        }
        if (newCustomerCard == null || newCustomerCard.length()!=10) {
            throw new InvalidCustomerCardException("Invalid Customer Card");
        }

        // generate a list of all ids
        List<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toList());

        // find the customer
        Customer customer = customers.get(id);

        // if the customer is present, update its card and name
        customer.setCustomerName(newCustomerName);

        if(newCustomerCard == " ")
            customer.setCustomerCard(null);
        else if(newCustomerCard.equals(null))
            ;
        else
            customer.setCustomerCard(newCustomerCard);


        // if the customer is present return true, otherwise return false
        return customer.getCustomerCard().equals(newCustomerCard);

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
        return customers.removeIf(x -> x.getId().equals(id));


    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //InvalidCustomerIdException if the id is null, less than or equal to 0
        if (id == null || id <= 0) {
            throw new InvalidCustomerIdException("Invalid Customer id");
        }

        return customers.get(id);
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        return customers;
    }

    @Override
    public String createCard() throws UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            char c = (char) ('A' + ((char)rnd.nextInt('Z' - 'A')));
            sb.append(c);
        }

        return sb.toString();
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //InvalidCustomerIdException if the id is null, less than or equal to 0.
        if (customerId == null || customerId.compareTo(0) > 0) {
            throw new InvalidCustomerIdException("Invalid Customer id");
        }

        //InvalidCustomerCardException if the card is null, empty or in an invalid format
        if (customerCard == null || customerCard.length()!=10) {
            throw new InvalidCustomerCardException("Invalid Customer Card");
        }

        // find the customer
        customers.get(customerId).setCustomerCard(customerCard);

        return customers.get(customerId).getCustomerCard().equals(customerCard);
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        //invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        //InvalidCustomerCardException if the card is null, empty or in an invalid format
        if (customerCard == null || customerCard.length()!=10) {
            throw new InvalidCustomerCardException("Invalid Customer Card");
        }

        // find the customer
        Optional<Customer> customer = customers.stream()
                // filter users with the given id
                .filter(x -> x.getCustomerCard().equals(customerCard)).findFirst();
        //false   if there is no card with given code
        if(customer.get().getCustomerCard().equals(customerCard))
            return false;

        Integer validPoints = customer.get().getPoints();

        if(validPoints.compareTo(pointsToBeAdded) < 0 && pointsToBeAdded < 0 )
            return false;
        else
            validPoints += pointsToBeAdded;

        customer.get().setPoints(validPoints);

        return true;

    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        int SaleTransactionID = accountBook.generateNewId();
        it.polito.ezshop.model.SaleTransaction st = new it.polito.ezshop.model.SaleTransaction(SaleTransactionID, LocalDate.now(), new ArrayList<>(), new ArrayList<>(),0 , 0);
        st.setStatus(OperationStatus.OPEN.name());

        // add SaleTransaction to account book
        this.accountBook.addTransaction(st);

        return SaleTransactionID;
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

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null || !t.getStatus().equals(OperationStatus.OPEN.name())) {
            return false;
        }

        ProductType p = products.stream()
                // filter users with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode) && x.getQuantity() >= amount)
                // find the first matching product
                .findFirst().orElse(null);
        if (p == null) {
            return false;
        }

        // update the quantity on the shelves
        p.setQuantity(p.getQuantity() - amount);

        // check if a TicketEntry for the product already exists
        TicketEntry ticketEntry = t.getEntries().stream()
                .filter(x -> x.getBarCode().equals(p.getBarCode())).findFirst().orElse(null);

        if (ticketEntry != null) {
            ticketEntry.setAmount(ticketEntry.getAmount() + amount);
        } else {
            t.getEntries().add(new it.polito.ezshop.model.TicketEntry(p, amount, p.getPricePerUnit(), 0));
        }

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

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null || !t.getStatus().equals(OperationStatus.OPEN.name())) {
            return false;
        }

        ProductType p = products.stream()
                // filter users with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode))
                // find the first matching product
                .findFirst()
                // if a matching product is not found, return null
                .orElse(null);
        if (p == null) {
            return false;
        }

        TicketEntry entry = t.getEntries().stream()
                .filter(x -> x.getBarCode().equals(productCode)).findFirst().orElse(null);
        if (entry == null) {
            return false;
        }
        if (entry.getAmount() < amount) {
            return false;
        }

        if (entry.getAmount() > amount) {
            entry.setAmount(entry.getAmount() - amount);
        } else {
            t.getEntries().remove(entry);
        }

        // update the quantity on the shelves
        p.setQuantity(p.getQuantity() + amount);

        return true;
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

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null || !t.getStatus().equals(OperationStatus.OPEN.name())) {
            return false;
        }

        Optional<TicketEntry> entry = t.getEntries().stream().filter(x -> x.getBarCode().equals(productCode)).findFirst();
        entry.ifPresent((value) -> value.setDiscountRate(discountRate));

        return entry.isPresent();
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
        if (t.getStatus().equals(OperationStatus.PAID.name()) || t.getStatus().equals(OperationStatus.COMPLETED.name())) {
            return false;
        }

        t.setDiscountRate(discountRate);
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
        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null){
            return false;
        }
        if (t.getStatus().equals(OperationStatus.CLOSED.name())){
            return false;
        }
        t.setStatus(OperationStatus.CLOSED.name());
        return true;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        if (saleNumber == null || saleNumber <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }

        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(saleNumber);
        if (t == null || t.getStatus().equals(OperationStatus.PAID.name())){
            return false;
        }

        accountBook.removeTransaction(saleNumber);
        return true;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);
        if (transactionId == null || transactionId <= 0){
            throw new InvalidTransactionIdException("Invalid transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(transactionId);
        if (t == null){
            return null;
        }
        if(!t.getStatus().equals(OperationStatus.CLOSED.name())){
            return null;
        }

        return t;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER);

        Integer RetrunTransactionID = accountBook.generateNewId();
        if( RetrunTransactionID == null || RetrunTransactionID <= 0){
            throw new InvalidTransactionIdException("Invalid Transaction ID");
        }
        it.polito.ezshop.model.SaleTransaction t = (it.polito.ezshop.model.SaleTransaction) accountBook.getTransaction(saleNumber);
        if( !t.getStatus().equals(OperationStatus.CLOSED.name()) || t == null){
            return -1;
        }
        ReturnTransaction rt = new ReturnTransaction(RetrunTransactionID, LocalDate.now(), new ArrayList<>(), t,0);
        rt.setStatus(OperationStatus.OPEN.name());
        t.getReturnTransactions().add(rt);


        // add ReturnTransaction to account book

        this.accountBook.addTransaction(rt);


        return RetrunTransactionID;
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
        if(!rt.getStatus().equals(OperationStatus.OPEN.name())){
            return false;
        }
        it.polito.ezshop.model.SaleTransaction t = rt.getSaleTransaction();
        if(t == null){
            return false;
        }
        ProductType p = products.stream()
                // filter products with the given BarCode
                .filter(x -> x.getBarCode().equals(productCode))
                // find the first matching product
                .findFirst()
                // if a matching product is not found, return null
                .orElse(null);
        if (p == null){
            return false;
        }
        Optional <TicketEntry> entry = t.getEntries().stream().filter(x -> x.getBarCode().equals(productCode)).findFirst();
        if(!entry.isPresent()){
            return false;
        }

        if(amount > entry.get().getAmount()){
            return false;
        }
        ReturnTransactionItem returnitem = new ReturnTransactionItem(p, amount);
        rt.getEntries().add(returnitem);
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
        if(!rt.getStatus().equals(OperationStatus.OPEN.name())){
            return false;
        }
        it.polito.ezshop.model.SaleTransaction t = rt.getSaleTransaction();
        if(t == null){
            return false;
        }
        if(commit == true){
            List <ReturnTransactionItem> ritems = rt.getEntries();
            List <TicketEntry> titems = t.getEntries();
            //didn't know how to use .stream in this case lol

            for(int i = 0; i < ritems.size(); i++) {
                ReturnTransactionItem ritem = ritems.get(i);
                TicketEntry titem = titems.stream()
                        // filter products with the given BarCode
                        .filter(x -> x.getBarCode().equals(ritem.getBarCode()))
                        // find the first matching product
                        .findFirst()
                        // if a matching product is not found, return null
                        .orElse(null);
                ProductType p = products.stream()
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
                p.setQuantity(p.getQuantity() + ritem.getAmount());
                //change quantity of Product p inside the transaction by reducing the returned amount
                titem.setAmount(titem.getAmount() - ritem.getAmount());
                //??? do we need to change the final price or it does automatically after removing the items ???
            }
            rt.setStatus(OperationStatus.CLOSED.name());
        }
        else{
            //anything else to do if commit == false?
            rt.setStatus(OperationStatus.CLOSED.name());
        }
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
        if(returnTransaction.getStatus().equals("open"))
            return -1;
        //if it does not exist,
        else if(returnTransaction.getStatus().isEmpty())
            return -1;
        //the money returned to the customer
        else
            return returnAmount;
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
        if(returnTransaction.getStatus().equals("OPEN"))
            return -1;
            //if it does not exist,
        else if(returnTransaction.getStatus().isEmpty())
            return -1;
        //*** I should registered card control ***
            //the money returned to the customer
        else
            return returnAmount;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);


        //date
        LocalDate date = LocalDate.now();
        // create Order object
        int balanceId = accountBook.generateNewId();
        //status
        OperationStatus newStatus = OperationStatus.PAID;

        it.polito.ezshop.model.BalanceOperation newRecord;
        if (toBeAdded >= 0) {
            newRecord = new Credit(balanceId, date, toBeAdded, newStatus);
        } else {
            newRecord = new Debit(balanceId, date, toBeAdded, newStatus);
        }
        accountBook.addTransaction(newRecord);

        //collect all the balance records to calculate
        List<Double> moneyList = accountBook.getAllTransactions().stream().map(BalanceOperation::getMoney).collect(Collectors.toList());

        //sum all of the moneys
        double total = 0;
        for(double money:moneyList ){
            total += money;
        }

        return !(total + toBeAdded < 0);
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        // collect all transactions
        List<BalanceOperation> listOfCreditsAndDebits = accountBook.getAllTransactions().stream().filter(x -> x.getDate().isAfter(from) && x.getDate().isBefore(to)).collect(Collectors.toList());
        return listOfCreditsAndDebits;

    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        verifyCurrentUserRole(Role.ADMINISTRATOR, Role.SHOP_MANAGER);

        //collect all the balance records to calculate
        List<Double> moneyList = accountBook.getAllTransactions().stream().map(BalanceOperation::getMoney).collect(Collectors.toList());

        //sum all of the moneys
        double total = 0;
        for(double money:moneyList ){
            total += money;
        }

        return total;
    }
}

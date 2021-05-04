package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.Role;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.model.Utils.generateId;
import static it.polito.ezshop.model.Utils.validateBarcode;


public class EZShop implements EZShopInterface {

    /**
     * List of all the users registered in EZShop.
     */
    private final List<User> users = new ArrayList<>();

    /**
     * Current logged in user.
     */
    private User currentUser = null;

    /**
     * List of all products in EZShop
     */
    private final List<ProductType> products = new ArrayList<>();

    /**
     * Check whether the expectedRole of the current user is the expected one.
     *
     * @param expectedRole is the expected current user's role.
     * @throws UnauthorizedException if no user is currently logged in or if its role
     *                               is not the expected one.
     */
    private void expectAuthorization(Role expectedRole) throws UnauthorizedException {
        if (currentUser == null || !currentUser.getRole().equals(expectedRole.getValue())) {
            throw new UnauthorizedException("Unauthorized user");
        }
    }

    @Override
    public void reset() {

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
        expectAuthorization(Role.ADMINISTRATOR);

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
        expectAuthorization(Role.ADMINISTRATOR);
        // return an unmodifiable list of users
        return Collections.unmodifiableList(users);
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        // check the role of the current user
        expectAuthorization(Role.ADMINISTRATOR);

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
        expectAuthorization(Role.ADMINISTRATOR);

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

        return users.stream()
                // filters all the users with a matching username and password
                .filter(x -> x.getUsername().equals(username) && x.getPassword().equals(password))
                // get a User from the filtered list
                .findAny()
                // if one user is found return it, otherwise return null
                .orElse(null);
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
        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);
        // create a new product
        if ( description == null || description.equals("") ){
            throw new InvalidProductDescriptionException("Invalid user Description");
        }

        if(validateBarcode(productCode) == false){
            throw new InvalidProductCodeException("Invalid Bar Code");
        }
        if (products.stream().anyMatch(x -> x.getBarCode().equals(productCode))) {
            return -1;
        }
        if(pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException("Price per Unit must be greater or equal than zero");
        }
        // generate a list of all ids
        List<Integer> ids = products.stream().map(ProductType::getId).collect(Collectors.toList());
        // generate a new id that is not already in the list
        Integer id = generateId(ids);

        //ProductType p = new it.polito.ezshop.model.ProductType(quantity, location, note, productDescription, barCode, pricePerUnit, id);
        ProductType p = new it.polito.ezshop.model.ProductType(note, description, productCode, pricePerUnit, id);
        products.add(p);
        return p.getId();
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {

        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);

        if ( newDescription == null || newDescription.equals("") ){
            throw new InvalidProductDescriptionException("Invalid user Description");
        }

        if(validateBarcode(newCode) == false){
            throw new InvalidProductCodeException("Invalid Bar Code");
        }
        if (products.stream().anyMatch(x -> x.getBarCode().equals(newCode))) {
            return false;
        }
        if(newPrice <= 0) {
            throw new InvalidPricePerUnitException("Price per Unit must be greater or equal than zero");
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
        // check the role of the current product
        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);
        // check that id is neither null or non-positive
        if (id == null || id <= 0) {
            throw new InvalidProductIdException("Invalid product id less or equal to 0");
        }

        // removeIf returns true if any elements were removed
        return products.removeIf(x -> x.getId().equals(id));
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);
        expectAuthorization(Role.CASHIER);

        // return an unmodifiable list of products
        return Collections.unmodifiableList(products);

    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);

        // check that barcode is neither null or not valid
        if (barCode == null || validateBarcode(barCode) == false) {
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
        expectAuthorization(Role.ADMINISTRATOR);
        expectAuthorization(Role.SHOP_MANAGER);

        return products.stream()
                // filter users with the given id
                .filter(x -> x.getProductDescription().contains(description)).collect(Collectors.toList());

    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        // check that product ID is valid
        if (productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }

        // check that user has sufficient rights (admin or shop manager)
        if (Role.ADMINISTRATOR.getValue().equals(currentUser.getRole())
                || Role.SHOP_MANAGER.getValue().equals(currentUser.getRole())) {
            throw new UnauthorizedException("Action may only be performed by administrator or shop manager");
        }

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

        product.setQuantity(product.getQuantity() + toBeAdded);

        return true;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {

        // check that product id is valid
        if (productId <= 0) {
            throw new InvalidProductIdException("Product ID must be positive integer");
        }
        // check that user has sufficient rights (admin or shop manager)
        if (Role.ADMINISTRATOR.getValue().equals(currentUser.getRole())
                || Role.SHOP_MANAGER.getValue().equals(currentUser.getRole())) {
            throw new UnauthorizedException("Action may only be performed by shop manager or administrator");
        }

        Position position = Position.parsePosition(newPos);

        // check that position has valid format
        if (position == null) {
            throw new InvalidLocationException("Position must be of form <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>");
        }

        Optional<ProductType> productAtPosition = products.stream()
                .filter(p -> p.getLocation().equals(newPos))
                .findFirst();

        // check that no product already has given position
        if (productAtPosition.isPresent()) {
            return false;
        }

        Optional<ProductType> productWithID = products.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();

        // update product position if product with given ID exists
        productWithID.ifPresent(p -> p.setLocation(newPos));

        // return true iff product exists
        return productWithID.isPresent();
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        return null;
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        return null;
    }

    @Override
    public String createCard() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }
}

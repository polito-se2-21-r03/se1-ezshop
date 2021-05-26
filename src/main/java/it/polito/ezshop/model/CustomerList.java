package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.InvalidCustomerNameException;

import java.util.*;
import java.util.stream.Collectors;

import static it.polito.ezshop.utils.Utils.generateId;

public class CustomerList {

    private final List<Customer> customers = new ArrayList<>();
    public final List<LoyaltyCard> loyaltyCards = new ArrayList<>();

    public CustomerList() {
        this.reset();
    }

    /**
     * Add a customer to the list of customers, returns the customers ID if he was added successfully, -1 if the name
     * was already taken
     *
     * @param name unique name of the customer to be added
     * @return id of the customer if successful,
     *         -1 if the name was already taken
     * @throws InvalidCustomerNameException if the customer name is empty or null
     */
    public int addCustomer(String name) throws InvalidCustomerNameException {

        // return -1 if the customer name is already taken
        if (isCustomerNameTaken(name)) {
            return -1;
        }

        // add customer to list
        int customerID = this.generateNewId();
        Customer customer;
        try {
            customer = new Customer(customerID, name);
        } catch (InvalidCustomerIdException e) {
            // this should not happen if the ID was generated properly
            throw new Error("An unexpected error was encountered when creating a new customer", e);
        }
        this.customers.add(customer);

        // return successfully
        return customerID;
    }

    /**
     * Modifies the customer with the given ID.
     *
     * @param id ID of the customer who should be modified
     * @param newName The new unique name for the customer
     * @param newCard The new loyalty card for the customer. Can be null to leave unchanged or empty string to unassign
     *               the currently used card
     * @return true if the customer was modified successfully,
     *         false if customer or card do not exist, card is already assigned or name is not unique
     * @throws InvalidCustomerNameException if the customer name is empty or null
     * @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
     */
    public boolean modifyCustomer(Integer id, String newName, String newCard) throws InvalidCustomerIdException, InvalidCustomerCardException, InvalidCustomerNameException {

        // ensure ID is valid
        Customer.validateID(id);

        // validate name
        Customer.validateName(newName);

        // ensure card is valid or special value
        if (newCard != null && !"".equals(newCard)) {
            LoyaltyCard.validateCode(newCard);
        }

        // get the customer from the list of customers
        Customer customer = this.customers.stream()
                .filter(c -> c.getId().equals(id))
                .findAny()
                .orElse(null);

        // return false if customer does not exist
        if (customer == null) {
            return false;
        }

        // return false if newName is not unique
        if (isCustomerNameTaken(newName, Collections.singletonList(id))) {
            return false;
        }

        // set the loyalty card according to special values
        LoyaltyCard loyaltyCard;
        if (newCard == null) {

            // if newCard is null, delete the card
            loyaltyCard = null;
        } else if (newCard.equals("")) {

            // if newCard is empty string, leave card attached to customer
            loyaltyCard = customer.getCard();
        } else {

            // get card from list of cards
            loyaltyCard = loyaltyCards.stream().filter(c -> newCard.equals(c.getCode())).findAny().orElse(null);

            // if card does not exist return false
            if (loyaltyCard == null) {
                return false;
            }

            // return false if card is already assigned to different user
            if (customers.stream().anyMatch(c -> !c.getId().equals(id) && loyaltyCard.equals(c.getCard()))) {
                return false;
            }
        }

        // update values and return successfully
        customer.setCustomerName(newName);
        customer.setCard(loyaltyCard);
        return true;
    }

    /**
     * Assign a card to a customer. Each customer can only have one card and each card can only be used by one customer.
     *
     * @param id The ID of the customer the card should be assigned to.
     * @param cardCode The card that should be assigned to the customer.
     * @return true if successful, false if card is already assigned or user does not exist
     * @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
     * @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
     */
    public boolean attachCardToCustomer(Integer id, String cardCode) throws InvalidCustomerIdException, InvalidCustomerCardException {

        // ensure id is valid
        Customer.validateID(id);

        // ensure card is valid
        LoyaltyCard.validateCode(cardCode);

        // get the loyalty card with the given code from the card list or null if it doesn't exist
        LoyaltyCard loyaltyCard = loyaltyCards.stream().filter(c -> cardCode.equals(c.getCode())).findAny().orElse(null);

        // check that card exists
        if (loyaltyCard == null) {
            return false;
        }

        // return false if another customer already uses this card
        if (customers.stream().anyMatch(c -> loyaltyCard.equals(c.getCard()) && !id.equals(c.getId()))) {
            return false;
        }

        // assign card to customer if customer exists
        Optional<Customer> customer = customers.stream().filter(c -> id.equals(c.getId())).findAny();
        customer.ifPresent(c -> c.setCard(loyaltyCard));

        // return true if customer was found and card was assigned successfully
        return customer.isPresent();
    }

    public boolean modifyPointsOnCard(String cardCode, int pointsToBeAdded) throws InvalidCustomerCardException {

        // ensure card is valid
        LoyaltyCard.validateCode(cardCode);

        // get the loyalty card with the given code from the card list or null if it doesn't exist
        LoyaltyCard loyaltyCard = loyaltyCards.stream().filter(c -> cardCode.equals(c.getCode())).findAny().orElse(null);

        // check that card exists
        if (loyaltyCard == null) {
            return false;
        }

        // try to change points on card, catch exception if result would be negative
        try {
            loyaltyCard.setPoints(loyaltyCard.getPoints() + pointsToBeAdded);
        } catch (IllegalArgumentException e) {
            return false;
        }

        // return successfully
        return true;
    }

    /**
     * Removes the customer with the given ID, returns if true if a customer was removed, false if no customer with
     * that ID existed. Throws an InvalidCustomerIdException if the given ID is not valid.
     *
     * @param id ID of the customer to be deleted
     * @return true if the customer was deleted, false if he doesn't exist
     * @throws InvalidCustomerIdException if the provided ID is not valid
     */
    public boolean removeCustomer(Integer id) throws InvalidCustomerIdException {
        Customer.validateID(id);
        return this.customers.removeIf(c -> id.equals(c.getId()));
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
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException {
        Customer.validateID(id);
        return customers.stream()
                .filter(c -> id.equals(c.getId()))
                .findAny()
                .orElse(null);
    }

    /**
     * Returns the list containing all customers
     *
     * @return the list of all customers
     */
    public List<Customer> getAllCustomers() {
        return customers;
    }

    /**
     * Generate a new unique ID that is not taken by any other customer in this list
     *
     * @return unique ID
     */
    public int generateNewId() {
        List<Integer> currentIds = this.customers.stream()
                .map(Customer::getId)
                .collect(Collectors.toList());
        return generateId(currentIds);
    }

    public String generateNewLoyaltyCard() {

        List<String> takenCodes = loyaltyCards.stream().map(LoyaltyCard::getCode).collect(Collectors.toList());

        String newCode = LoyaltyCard.generateNewCode();
        while (takenCodes.contains(newCode)) {
            newCode = LoyaltyCard.generateNewCode();
        }

        try {
            loyaltyCards.add(new LoyaltyCard(newCode));
        } catch (InvalidCustomerCardException e) {
            // this should never happen if the code generation is correct
            throw new Error("An unexpected error was encountered when generating a new loyalty card", e);
        }

        return newCode;
    }

    /**
     * Check if the name is taken by any other customer.
     *
     * @param name Name to check for if it is taken.
     * @return true if the name is taken, false if not
     */
    private boolean isCustomerNameTaken(String name) {
        return isCustomerNameTaken(name, new ArrayList<>());
    }

    /**
     * Check if the name is taken by any other customer except for the customers with IDs specified in igonreIDs.
     *
     * @param name Name to check for if it is taken.
     * @param ignoreIDs IDs of customers that should be ignored when checking for the name.
     * @return True, if the name is taken by a customer that is not in the ignoreIDs list, false if it isn't.
     */
    private boolean isCustomerNameTaken(String name, List<Integer> ignoreIDs) {
        return this.customers.stream().anyMatch(c -> c.getCustomerName().equals(name) && !ignoreIDs.contains(c.getId()));
    }

    /**
     * Reset the customer list to its initial state (no customers, no loyalty cards)
     */
    public void reset() {
        this.customers.clear();
        this.loyaltyCards.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerList that = (CustomerList) o;
        return Objects.equals(customers, that.customers)
                && Objects.equals(loyaltyCards, that.loyaltyCards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customers, loyaltyCards);
    }
}

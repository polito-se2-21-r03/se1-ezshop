package it.polito.ezshop.credit_card_circuit;

public interface CreditCardCircuit {
    /**
     * This method should initialize the application
     */
    public void init();

    /**
     * This method should validate card with luhn algorithm
     *
     * @param creditCardCode credit card number in the system, should be unique and not empty
     *
     * @return true if credit card is validated
     *         false if it is not
     *
     * */
    public boolean validateCode(String creditCardCode);

    /**
     * This method should check the amount of credit card and return true if there is enough amount of money
     *
     * @param creditCardCode credit card number in the system, should be unique and not empty
     * @param amount the amount which should be checked about availability
     *
     * @return true if credit card's amount is available
     *         false if it is not
     *
     * */
    public boolean checkAvailability(String creditCardCode, Integer amount);

    /**
     * This method should add debit to the credit card
     * and if the adding finish successfully it should return true otherwise not
     *
     * @param creditCardCode credit card number in the system, should be unique and not empty
     * @param amount the amount which should be added as debit
     *
     * @return true if the method finish successfully
     *         false if it is not
     *
     * */
    public boolean addDebit(String creditCardCode, Integer amount);

    /**
     * This method should add credit to the credit card
     * and if the adding finish successfully it should return true other wise not
     *
     * @param creditCardCode credit card number in the system, should be unique and not empty
     * @param amount the amount which should be added as credit
     *
     * @return true if the method finish successfully
     *         false if it is not
     *
     * */
    public boolean addCredit(String creditCardCode, Integer amount);
}

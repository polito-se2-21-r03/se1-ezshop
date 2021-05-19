package it.polito.ezshop.credit_card_circuit;

public class VisaCreditCardCircuitAdapter implements CreditCardCircuit{

    @Override
    public void init() {

    }

    @Override
    public boolean validateCode(String creditCardCode) {
        return false;
    }

    @Override
    public boolean checkAvailability(String creditCardCode, double amount) {
        return false;
    }

    @Override
    public boolean addDebit(String creditCardCode, double amount) {
        return false;
    }

    @Override
    public boolean addCredit(String creditCardCode, double amount) {
        return false;
    }
}

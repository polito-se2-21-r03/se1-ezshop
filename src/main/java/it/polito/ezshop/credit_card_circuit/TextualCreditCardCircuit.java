package it.polito.ezshop.credit_card_circuit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import it.polito.ezshop.utils.Utils;

public class TextualCreditCardCircuit implements CreditCardCircuit{
    String path = "it.polito.ezshop/utils/CreditCards.txt";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public TextualCreditCardCircuit(String path) {
        this.path = path;
    }


    @Override
    public void init() {

    }

    @Override
    public boolean validateCode(String creditCardCode) {
        return Utils.isValidCreditCardNumber(creditCardCode);
    }

    @Override
    public boolean checkAvailability(String creditCardCode, Integer amount) { ;
        Double balance = Utils.readFromFile(this.path, creditCardCode);
        return amount >= balance;
    }

    @Override
    public boolean addDebit(String creditCardCode, Integer amount) throws IOException {
        return Utils.whiteToFile(this.path, creditCardCode, amount);

    }

    @Override
    public boolean addCredit(String creditCardCode, Integer amount) throws IOException {
        return Utils.whiteToFile(this.path, creditCardCode, amount);
    }
}

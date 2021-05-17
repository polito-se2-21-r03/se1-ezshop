package it.polito.ezshop.credit_card_circuit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TextualCreditCardCircuit implements CreditCardCircuit{
    String path;

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
        return false;
    }

    @Override
    public boolean checkAvailability(String creditCardCode, Integer amount) {
        return false;
    }

    @Override
    public boolean addDebit(String creditCardCode, Integer amount) {
        return false;
    }

    @Override
    public boolean addCredit(String creditCardCode, Integer amount) {
        return false;
    }
}

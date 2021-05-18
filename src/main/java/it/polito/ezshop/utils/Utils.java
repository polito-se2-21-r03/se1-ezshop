package it.polito.ezshop.utils;

import it.polito.ezshop.EZShop;
import it.polito.ezshop.credit_card_circuit.CreditCardCircuit;
import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.Arrays;

public class Utils {

    /**
     * Generate a new (random) integer id which is not already
     * present in the provided list.
     *
     * @param ids is the current list of IDs to check for collision.
     * @return a new integer id.
     */
    public static Integer generateId(List<Integer> ids) {
        UUID u = UUID.randomUUID();
        int id = (int) u.getLeastSignificantBits();

        while (id <= 0 || (ids != null && ids.contains(id))) {
            u = UUID.randomUUID();
            id = (int) u.getLeastSignificantBits();
        }

        return id;
    }

    /**
     * Check if given barcode is valid according to GTIN-12, GTIN-13 or GTIN-14.
     * Implementation follows the algorithm described at: https://www.gs1.org/services/how-calculate-check-digit-manually
     *
     * @param barcode the barcode whose conformity to the above mentioned standard is to be verified
     * @return whether the given barcode is valid or not
     */
    public static boolean isValidBarcode(String barcode) {

        // verify that barcode is not null
        if (barcode == null) {
            return false;
        }

        // check that barcode has correct length
        if (barcode.length() != 12 && barcode.length() != 13 && barcode.length() != 14) {
            return false;
        }

        int checksum = 0;

        // sum multiples (1x or 3x depending on position) of each digit except last one in checksum
        for (int i = 0; i < barcode.length()-1; i++) {

            // read digit at position i
            int digit = Character.getNumericValue(barcode.charAt(i));

            // check that parsed character is in fact single digit
            if (digit < 0 || digit > 9) {
                return false;
            }

            // multiply digit by 3 if distance to last digit is uneven, multiply by 1 otherwise
            // sum results of this operation for each digit
            if ((barcode.length() - i) % 2 == 0) {
                checksum += 3 * digit;
            } else {
                checksum += digit;
            }
        }

        int checksumDigit = Character.getNumericValue(barcode.charAt(barcode.length() - 1));

        // check that last character is single digit
        if (checksumDigit < 0 || checksumDigit > 9) {
            return false;
        }

        // return true iff checksum requirement is fulfilled
        return (checksum + checksumDigit) % 10 == 0;
    }

    /**
     * Check if given credit number is valid according to luhn algorithm.
     * Implementation follows the algorithm described at: https://java2blog.com/luhn-algorithm-java/
     *
     * @param cardNumber the barcode whose conformity to the above mentioned standard is to be verified
     * @return whether the given creditcard number is valid or not
     */
    public static boolean isValidCreditCardNumber(String cardNumber) {

        // credit card number may not be null
        if (cardNumber == null) {
            return false;
        }

        // credit card number must have 16 digits
        if (cardNumber.length() != 16) {
            return false;
        }

        // sum all digits, every other digit counts double
        int checksum = 0;
        for (int i=0; i<cardNumber.length(); i++) {

            // get digit at position i
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            // verify parsed digit was actually a numeric digit
            if (digit < 0 || digit > 9) {
                return false;
            }

            // sum digits, counting every other digit double
            if (i%2 == 0) {

                int twiceDigit = 2 * digit;

                // if doubling digit results in double digit number, add digit sum instead
                if (twiceDigit > 9) {
                    int digitSum = twiceDigit/10 + twiceDigit%10;
                    checksum += digitSum;
                } else {
                    checksum += twiceDigit;
                }
            } else {
                checksum += digit;
            }
        }

        // credit card is valid iff computed checksum is divisible by 10
        return checksum%10 == 0;
    }

    public static Double readFromFile(String path, String cardNumber) {
            try {
                FileReader reader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                String  balance = null;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if(parts[0].equals(cardNumber))
                        balance = parts[1];
                }
                assert balance != null;
                reader.close();
                return Double.parseDouble(balance);


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

    }
    public static boolean whiteToFile(String path, String cardNumber, double amount) throws IOException {
        //Instantiating the Scanner class to read the file
        Scanner sc = new Scanner(new File(path));
        //instantiating the StringBuffer class
        StringBuilder buffer = new StringBuilder();
        //Reading lines of the file and appending them to StringBuffer
        while (sc.hasNextLine()) {
            buffer.append(sc.nextLine()).append(System.lineSeparator());
        }
        String fileContents = buffer.toString();
        Double balance = readFromFile(path, cardNumber);

        sc.close();
        assert balance != null;
        double newBalance = balance + amount;
        String oldLine = cardNumber + ";" + balance.toString();
        String newLine = cardNumber + ";" + newBalance;
        //Replacing the old line with new line
        fileContents = fileContents.replaceAll(oldLine, newLine);
        //instantiating the FileWriter class
        FileWriter writer = new FileWriter(path);
        writer.append(fileContents);
        writer.flush();

        return newBalance == balance+amount;
    }


}


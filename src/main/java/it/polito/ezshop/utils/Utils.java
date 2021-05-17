package it.polito.ezshop.utils;

import it.polito.ezshop.EZShop;
import it.polito.ezshop.credit_card_circuit.CreditCardCircuit;
import it.polito.ezshop.credit_card_circuit.TextualCreditCardCircuit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
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
    public static boolean isValidCreditCardNumber(String cardNumber)
    {
        // int array for processing the cardNumber
        int[] cardIntArray=new int[cardNumber.length()];

        for(int i=0;i<cardNumber.length();i++)
        {
            char c= cardNumber.charAt(i);
            cardIntArray[i]=  Integer.parseInt(""+c);
        }

        for(int i=cardIntArray.length-2;i>=0;i=i-2)
        {
            int num = cardIntArray[i];
            num = num * 2;  // step 1
            if(num>9)
            {
                num = num%10 + num/10;  // step 2
            }
            cardIntArray[i]=num;
        }

        int sum = sumDigits(cardIntArray);  // step 3

        System.out.println(sum);

        if(sum%10==0)  // step 4
        {
            return true;
        }

        return false;

    }
    public static int sumDigits(int[] arr)
    {
        return Arrays.stream(arr).sum();
    }

    public static Integer readFromFile() {
            String path = EZShop.main();
            try {
                FileReader reader = new FileReader();
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                int balance;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] parts = line.split(";");
                    balance = Integer.parseInt(parts[1]);
                }
                return balance;
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}


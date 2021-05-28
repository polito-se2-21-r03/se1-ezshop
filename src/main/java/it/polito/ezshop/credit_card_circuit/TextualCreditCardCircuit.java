package it.polito.ezshop.credit_card_circuit;

import it.polito.ezshop.utils.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TextualCreditCardCircuit implements CreditCardCircuit {

    public static final String CLEAN_TEXT_FILE = "CreditCards-original.txt";
    public static final String WORKING_TEXT_FILE = "CreditCards.txt";


    private final String path;

    private final List<CreditCard> creditCards = new ArrayList<>();

    public TextualCreditCardCircuit(String path) {
        this.path = path;
        this.init();
    }

    /**
     * @return an (unmodifiable) list of credit cards registered in the system
     */
    public List<CreditCard> getCreditCards() {
        return Collections.unmodifiableList(creditCards);
    }

    private void init() {
        if (this.creditCards.size() == 0) {
            try {
                this.creditCards.addAll(readFile());
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Clear the current list of credit cards.
     * Replace current file with the clean version.
     * Reload the list of credit cards.
     */
    @Override
    public void reset() {
        // remove the current credit cards
        this.creditCards.clear();

        try {
            // restore the original file
            Files.copy(Paths.get(TextualCreditCardCircuit.CLEAN_TEXT_FILE), Paths.get(path), REPLACE_EXISTING);
        } catch (IOException ignored) {
        }

        // load the credit cards from the file
        this.init();
    }

    @Override
    public boolean validateCode(String creditCardCode) {
        return Utils.isValidCreditCardNumber(creditCardCode);
    }

    @Override
    public boolean checkAvailability(String creditCardCode, double amount) {
        return getCreditCard(creditCardCode)
                .map(x -> x.checkAvailability(amount))
                .orElse(false);
    }

    @Override
    public boolean addDebit(String creditCardCode, double amount) {
        if (amount < 0) {
            return false;
        }
        return updateBalance(creditCardCode, -amount);
    }

    @Override
    public boolean addCredit(String creditCardCode, double amount) {
        if (amount < 0) {
            return false;
        }

        return updateBalance(creditCardCode, amount);
    }

    /**
     * Read the balance of a credit card.
     *
     * @param creditCardCode the code of the credit card
     * @return the balance of the credit card (-1 if the card does not exists)
     */
    public double getBalance(String creditCardCode) {
        return getCreditCard(creditCardCode)
                .map(CreditCard::getBalance)
                .orElse(-1.0);
    }

    /**
     * Update the balance of a credit card.
     *
     * @param code   code of the credit card
     * @param amount amount to add/remove
     * @return true if the operation is successful, false otherwise
     */
    private boolean updateBalance(String code, double amount) {
        CreditCard card = getCreditCard(code).orElse(null);

        if (card == null) {
            return false;
        }

        // return true if the balance update is successful
        // and the operation was correctly recorded in the file
        return card.updateBalance(amount) && updateFile(card);
    }

    /**
     * Find a credit card in the list
     *
     * @param code code of the credit card
     * @return an optional possibly containing the credit card if it exists
     */
    private Optional<CreditCard> getCreditCard(String code) {
        if (code == null) {
            return Optional.empty();
        }

        return this.creditCards.stream()
                .filter(x -> x.getCode().equals(code))
                .findAny();
    }

    /**
     * Update the file.
     *
     * @param cardToUpdate card to update
     * @return true if the operation is successful, false otherwise
     */
    private boolean updateFile(CreditCard cardToUpdate) {
        try {
            // read the lines of the file
            List<String> lines = Files.readAllLines(Paths.get(path))
                    .stream()
                    .map(line -> {
                        // comment line -> return it as it is
                        if (line.startsWith("#")) {
                            return line;
                        }

                        try {
                            // parse the line
                            String[] parts = line.split(";");
                            if (parts.length != 2) {
                                return null;
                            }

                            String code = parts[0];

                            // replace the line
                            if (cardToUpdate.getCode().equals(code)) {
                                return cardToUpdate.toString();
                            }
                        } catch (NumberFormatException ignored) {
                        }

                        return line;
                    }).filter(Objects::nonNull).collect(Collectors.toList());

            // write the changes back to the file
            String fileContent = String.join(System.lineSeparator(), lines);

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
                writer.write(fileContent, 0, fileContent.length());
                return true;
            } catch (IOException ignored) {
            }

        } catch (IOException ignored) {
        }

        return false;
    }

    /**
     * Read credit cards from the file
     *
     * @return a list of credit cards
     */
    private List<CreditCard> readFile() throws IOException {
        return Files.readAllLines(Paths.get(path))
                .stream()
                .filter(line -> !line.startsWith("#"))
                .map(line -> {
                    try {
                        String[] parts = line.split(";");
                        if (parts.length != 2) {
                            return null;
                        }

                        String code = parts[0];
                        double balance = Double.parseDouble(parts[1]);

                        return new CreditCard(code, balance);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}

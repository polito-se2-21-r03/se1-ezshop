package it.polito.ezshop.model.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polito.ezshop.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonInterface {

    /**
     * Path of the users json file
     */
    private final Path usersPath;

    /**
     * Path of the products json file
     */
    private final Path productsPath;

    /**
     * Path of the account book json file
     */
    private final Path accountBookPath;

    /**
     * Path of the customers json file
     */
    private final Path customerListPath;

    /**
     * Instance of the Gson serializer/deserializer
     */
    private final Gson gson;

    /**
     * Construct a new instance of JsonInterfaceImpl.
     * This constructor assumes that the desired path exists.
     *
     * @param path path that should contains the application data
     */
    private JsonInterface(Path path) {
        // create the paths for each file
        this.usersPath = Paths.get(path.toString(), "users.json");
        this.productsPath = Paths.get(path.toString(), "products.json");
        this.accountBookPath = Paths.get(path.toString(), "account_book.json");
        this.customerListPath = Paths.get(path.toString(), "customers.json");

        // see https://jansipke.nl/serialize-and-deserialize-a-list-of-polymorphic-objects-with-gson/
        // create a runtime adapter that instantiate the correct subclass of BalanceOperation
        RuntimeTypeAdapterFactory<BalanceOperation> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(BalanceOperation.class, "_type")
                .registerSubtype(Credit.class, "credit")
                .registerSubtype(Debit.class, "debit")
                .registerSubtype(Order.class, "order")
                .registerSubtype(SaleTransaction.class, "sale")
                .registerSubtype(ReturnTransaction.class, "return");

        this.gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();
    }

    /**
     * Create a new instance of JsonInterface.
     * This methods creates the destination path if it does not exist.
     *
     * @param path is the path of the destination directory
     * @return an instance of JsonInterface
     * @throws IOException if an I/O exception occurs
     */
    public static JsonInterface create(String path) throws IOException {
        Path destinationDir = Paths.get(path);

        if (!Files.exists(destinationDir)) {
            Files.createDirectory(destinationDir);
        }

        return new JsonInterface(destinationDir);
    }

    /**
     * Clear all the persisted data.
     *
     * @throws IOException if an I/O exception occurs
     */
    public void reset() throws IOException {
        for (Path path : Arrays.asList(usersPath, productsPath, accountBookPath, customerListPath)) {
            Files.deleteIfExists(path);
        }
    }

    /**
     * Read a list of users from the persistence layer.
     * If the persistence layer contains no users, an empty list is returned.
     *
     * @return a list of users
     * @throws IOException if an I/O exception occurs
     */
    public List<User> readUsers() throws IOException {
        return readList(usersPath, User.class);
    }

    /**
     * Write a list of users to the persistence layer.
     *
     * @param users list of users to be persisted (null is treated as an empty list)
     * @throws IOException if an I/O exception occurs
     */
    public void writeUsers(List<User> users) throws IOException {
        writeList(usersPath, users);
    }

    /**
     * Read a list of products from the persistence layer.
     * If the persistence layer contains no products, an empty list is returned.
     *
     * @return a list of products
     * @throws IOException if an I/O exception occurs
     */
    public List<ProductType> readProducts() throws IOException {
        return readList(productsPath, ProductType.class);
    }

    /**
     * Write a list of products to the persistence layer.
     *
     * @param products list of products to be persisted (null is treated as an empty list)
     * @throws IOException if an I/O exception occurs
     */
    public void writeProducts(List<ProductType> products) throws IOException {
        writeList(productsPath, products);
    }

    /**
     * Read the customer list from the persistence layer.
     * If the persistence layer contains no customer list,
     * a new clean instance is returned.
     *
     * @return the customer list
     * @throws IOException if an I/O exception occurs
     */
    public CustomerList readCustomerList() throws IOException {
        String json = read(customerListPath);
        if (json == null || json.equals("")) {
            return new CustomerList();
        }

        Type type = new TypeToken<CustomerList>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Write a list of customers to the persistence layer.
     *
     * @param customerList the customer list to be persisted (null is treated as a clean instance)
     * @throws IOException if an I/O exception occurs
     */
    public void writeCustomerList(CustomerList customerList) throws IOException {
        if (customerList == null) {
            customerList = new CustomerList();
        }
        write(customerListPath, gson.toJson(customerList));
    }

    /**
     * Read the account book from the persistence layer.
     * If the persistence layer contains no account book, a clean instance
     * (zero balance, no transactions) is returned.
     *
     * @return the account book
     * @throws IOException if an I/O exception occurs
     */
    public AccountBook readAccountBook() throws IOException {
        String json = read(accountBookPath);
        if (json == null || json.equals("")) {
            return new AccountBook();
        }

        Type type = new TypeToken<AccountBook>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Write the account book to the persistence layer.
     *
     * @param accountBook the account book to be persisted (null is treated as a clean instance)
     * @throws IOException if an I/O exception occurs
     */
    public void writeAccountBook(AccountBook accountBook) throws IOException {
        if (accountBook == null) {
            accountBook = new AccountBook();
        }
        write(accountBookPath, gson.toJson(accountBook));
    }

    /**
     * Write a string to a file.
     *
     * @param path of the file
     * @param json is the string to write
     * @throws IOException if an I/O exceptions occurs while reading the file
     */
    private void write(Path path, String json) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(json, 0, json.length());
        }
    }

    /**
     * Read all the lines of a file.
     *
     * @param path of the file
     * @return a string containing the lines of the file (null if the file does not exist)
     * @throws IOException if an I/O exceptions occurs while reading the file
     */
    private String read(Path path) throws IOException {
        if (Files.notExists(path)) {
            return null;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        }
    }

    /**
     * Write a list of objects to a json file.
     *
     * @param path of the json file to write
     * @param data is the list of objects to write
     * @param <T> type of the objects to write
     * @throws IOException if an I/O exceptions occurs while reading the json file
     */
    private <T> void writeList(Path path, List<T> data) throws IOException {
        if (data == null) {
            data = new ArrayList<>();
        }
        write(path, gson.toJson(data));
    }

    /**
     * Read a list of objects from a json file.
     *
     * @param path of the json file to read
     * @param cls class of the objects to read
     * @param <T> type of the objects to read
     * @return a possibly empty list of the read objects
     * @throws IOException if an I/O exceptions occurs while reading the json file
     */
    private <T> List<T> readList(Path path, Class<T> cls) throws IOException {
        String json = read(path);
        if (json == null || json.equals("")) {
            return new ArrayList<>();
        }

        Type type = TypeToken.getParameterized(List.class, cls).getType();
        return gson.fromJson(json, type);
    }

}

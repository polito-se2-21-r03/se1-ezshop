package it.polito.ezshop;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.EZShopInterface;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

public class TestHelpers {

    public static final double DOUBLE_COMPARISON_THRESHOLD = 0.001;

    /**
     * Invalid values for the product's id parameter
     */
    public static final List<Integer> invalidProductIDs = Arrays.asList(null, -1, 0);
    /**
     * Invalid values for the product's description parameter
     */
    public static final List<String> invalidProductDescriptions = Arrays.asList(null, "");
    /**
     * Invalid values for the product's code parameter
     */
    public static final List<String> invalidProductCodes = Arrays.asList(null, "", "12345678901",
            "123456789012345", "0123456789a12", "123456789011");
    /**
     * Invalid values for the product's price per unit parameter
     */
    public static final List<Double> invalidPricesPerUnit = Arrays.asList(-1.0, 0.0);
    /**
     * Invalid values for the product's amount parameter
     */
    public static final List<Integer> invalidProductAmounts = Arrays.asList(-10, -1);

    /**
     * Invalid values for the transaction's id parameter
     */
    public static final List<Integer> invalidTransactionIDs = Arrays.asList(null, -1, 0);
    /**
     * Invalid values for the discount rate parameters
     */
    public static final List<Double> invalidDiscountRates = Arrays.asList(-1.0, -0.1, 1.0, 1.1);

    /**
     * Invalid values for the customer's name parameter
     */
    public static final List<String> invalidCustomerNames = Arrays.asList(null, "");
    /**
     * Invalid values for the customer's id parameter
     */
    public static final List<Integer> invalidCustomerIDs = Arrays.asList(null, -1, 0);
    /**
     * Invalid values for the customer's card code parameter
     */
    public static final List<String> invalidCustomerCards = Arrays.asList(null, "", "123456789",
            "12345678901", "123456789a", "123456789A");

    /**
     * Invalid values for the payment amount parameter
     */
    public static final List<Integer> invalidPaymentAmounts = Arrays.asList(-1, 0);
    /**
     * Invalid credit card codes
     */
    public static final List<String> invalidCreditCards = Arrays.asList(null, "", "135895499391449a",
            "13589549939144", "135895499391443525", "1358954993914491");

    /**
     * Invalid RFID codes
     */
    public static final List<String> invalidRFIDs = Arrays.asList(null, "", "12345678901a",
            "a23456789012", "12345678901");

    /**
     * Invalid values for the ticket entry amount
     */
    public static final List<Integer> invalidTicketEntryAmounts = Arrays.asList(-10, -1);
    public static final ProductType product1, product2, product3, product4;
    protected static final EZShop shop = new EZShop();

    /**
     * Product 1
     */
    private static final Integer PRODUCT_ID_1 = 1;
    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final Double PRODUCT_PRICE_1 = 15.0;
    private static final Integer PRODUCT_QUANTITY_1 = 10;
    private static final String PRODUCT_DESCRIPTION_1 = "description product 1";
    private static final String PRODUCT_NOTE_1 = "note product 1";
    private static final String PRODUCT_POSITION_1 = "1-1-1";

    /**
     * Product 2
     */
    private static final Integer PRODUCT_ID_2 = 2;
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final Double PRODUCT_PRICE_2 = 25.0;
    private static final Integer PRODUCT_QUANTITY_2 = 10;
    private static final String PRODUCT_DESCRIPTION_2 = "description product 2";
    private static final String PRODUCT_NOTE_2 = "note product 2";
    private static final String PRODUCT_POSITION_2 = "1-1-2";

    /**
     * Product 3
     */
    private static final Integer PRODUCT_ID_3 = 3;
    private static final String PRODUCT_CODE_3 = "123456789012";
    private static final Double PRODUCT_PRICE_3 = 17.50;
    private static final Integer PRODUCT_QUANTITY_3 = 10;
    private static final String PRODUCT_DESCRIPTION_3 = "description product 3";
    private static final String PRODUCT_NOTE_3 = "note product 3";
    private static final String PRODUCT_POSITION_3 = "1-1-3";

    /**
     * Product 4
     */
    private static final Integer PRODUCT_ID_4 = 4;
    private static final String PRODUCT_CODE_4 = "5634567890122";
    private static final Double PRODUCT_PRICE_4 = 3.50;
    private static final Integer PRODUCT_QUANTITY_4 = 20;
    private static final String PRODUCT_DESCRIPTION_4 = "description product 4";
    private static final String PRODUCT_NOTE_4 = "note product 4";
    private static final String PRODUCT_POSITION_4 = "1-1-4";

    /**
     * Admin
     */
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";
    public static User admin;

    // invalid parameters for users
    public static List<Integer> invalidUserIDs = Arrays.asList(null, -1, 0);
    public static List<String> invalidUserUsernames = Arrays.asList(null, "");
    public static List<String> invalidUserPassword = Arrays.asList(null, "");
    public static List<String> invalidUserRoles = Arrays.asList(null, "", "administrator", "CashieR");

    static {
        try {
            admin = new User(1, ADMIN_USERNAME, ADMIN_PASSWORD, Role.ADMINISTRATOR);

            // initialization of product 1
            product1 = new it.polito.ezshop.model.ProductType(
                    PRODUCT_ID_1,
                    PRODUCT_DESCRIPTION_1,
                    PRODUCT_CODE_1,
                    PRODUCT_PRICE_1,
                    PRODUCT_NOTE_1,
                    PRODUCT_QUANTITY_1,
                    new Position(PRODUCT_POSITION_1)
            );

            // initialization of product 2
            product2 = new it.polito.ezshop.model.ProductType(
                    PRODUCT_ID_2,
                    PRODUCT_DESCRIPTION_2,
                    PRODUCT_CODE_2,
                    PRODUCT_PRICE_2,
                    PRODUCT_NOTE_2,
                    PRODUCT_QUANTITY_2,
                    new Position(PRODUCT_POSITION_2)
            );

            // initialization of product 3
            product3 = new it.polito.ezshop.model.ProductType(
                    PRODUCT_ID_3,
                    PRODUCT_DESCRIPTION_3,
                    PRODUCT_CODE_3,
                    PRODUCT_PRICE_3,
                    PRODUCT_NOTE_3,
                    PRODUCT_QUANTITY_3,
                    new Position(PRODUCT_POSITION_3)
            );

            // initialization of product 4
            product4 = new it.polito.ezshop.model.ProductType(
                    PRODUCT_ID_4,
                    PRODUCT_DESCRIPTION_4,
                    PRODUCT_CODE_4,
                    PRODUCT_PRICE_4,
                    PRODUCT_NOTE_4,
                    PRODUCT_QUANTITY_4,
                    new Position(PRODUCT_POSITION_4)
            );

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * This method tests whether the access rights for a given EZShop API function are managed correctly.
     * If an authentication bug is found, Assert.fail() is called.
     * Note: this method resets its EZShop instance for each role test and thus only works if the method raises the
     *        UnauthorizedException before any other exception
     *
     * @param apiMethod    method of the EZShop API
     * @param parameters   parameters that should be passed to the API method
     * @param allowedRoles array of all roles that are allowed to call the API method
     * @throws Throwable throws any Exception that is not an UnauthorizedException
     */
    public static void testAccessRights(Method apiMethod, Object[] parameters, Role[] allowedRoles) throws Throwable {

        // fail if the method can be invoked without any logged in user
        if (!testNoLoggedInUserIsDenied(apiMethod, parameters)) {
            fail("If no user is logged in, calling the method should NOT be allowed");
        }

        // create an EZShop instance
        EZShop shop = new EZShop();
        String username = "user";
        String password = "password";

        // for all possible roles
        for (Role role : Role.values()) {

            // reset the shop to a clean state
            shop.reset();

            // create a user with given roll
            shop.createUser(username, password, role.getValue());

            // login as user
            shop.login(username, password);

            // true iff the role of current loop iteration is in allowedRoles
            boolean userIsAuthorized = Arrays.stream(allowedRoles)
                    .anyMatch(r -> r.getValue().equals(role.getValue()));

            // try to invoke given method and catch only UnauthorizedExceptions, all other exceptions are thrown
            try {
                apiMethod.invoke(shop, parameters);

                // if no UnauthorizedException was thrown, but user should not have rights, fail
                if (!userIsAuthorized) {
                    fail(String.format("Role %s should NOT be allowed to call the method", role.getValue()));
                }

            } catch (InvocationTargetException e) {

                // if the invoked method throws an UnauthorizedException, but should be authorized, fail
                if (e.getCause() instanceof UnauthorizedException) {

                    // fail if UnauthorizedException is caught but user should have access rights
                    if (userIsAuthorized) {
                        fail(String.format("Role %s should be allowed to call the method", role.getValue()));
                    }
                } else {

                    // throw all other errors
                    throw e.getCause();
                }
            }
        }

        // no authentication bugs could be found
    }

    /**
     * Checks whether an UnauthorizedException is thrown in case no User is currently logged in
     *
     * @return return true if an UnauthorizedException is thrown in case no User is currently logged in, false if no exception is thrown
     * @throws Throwable all other Exceptions are thrown
     */
    private static boolean testNoLoggedInUserIsDenied(Method apiMethod, Object[] parameters) throws Throwable {

        // create new EZShop instance
        EZShop shop = new EZShop();
        shop.reset();

        // insert a dummy user
        shop.createUser("username", "password", Role.ADMINISTRATOR.getValue());

        // try to call API method and only catch UnauthorizedException
        try {
            apiMethod.invoke(shop, parameters);
        } catch (InvocationTargetException e) {

            // catch only UnauthorizedException, throw all other
            if (e.getCause() instanceof UnauthorizedException) {

                // return true if method can not be invoked without logged in user
                return true;
            } else {

                // throw all other exceptions
                throw e.getCause();
            }
        }

        // return false if no exception was thrown
        return false;
    }

    public static <T extends Exception, V> void testInvalidValues(Class<T> exceptionCls, List<V> values,
                                                                  ThrowingFunction<V> function) {
        values.forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(exceptionCls, () -> function.apply(value));
        });
    }

    /**
     * Add a product to the shop
     *
     * @param p product to add
     * @return the id of the product
     */
    public static int addProductToShop(EZShopInterface shop, ProductType p) throws Exception {
        int id = shop.createProductType(p.getProductDescription(), p.getBarCode(), p.getPricePerUnit(), p.getNote());
        shop.updatePosition(id, p.getPosition().toString());
        shop.updateQuantity(id, p.getQuantity());

        return id;
    }

    public interface ThrowingFunction<T> {
        void apply(T t) throws Exception;
    }
}

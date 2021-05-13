package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class TestHelpers {

    public static final double DOUBLE_COMPARISON_THRESHOLD = 0.001;

    public static final List<Integer> invalidProductIDs = Arrays.asList(null, -1, 0);
    public static final List<String> invalidProductCodes = Arrays.asList(null, "", "123456789B123A", "12345678901232");
    public static final List<Integer> invalidProductAmounts = Arrays.asList(-10, -1);
    public static final List<Integer> invalidTransactionIDs = Arrays.asList(null, -1, 0);
    public static final List<Double> invalidDiscountRates = Arrays.asList(-1.0, -0.1, 1.0, 1.1);
    public static final List<String> invalidCustomerNames = Arrays.asList(null, "");
    public static final List<Integer> invalidCustomerIDs = Arrays.asList(null, -1, 0);
    public static final List<String> invalidCustomerCards = Arrays.asList(null, "", "123456789", "12345678901", "123456789a", "123456789A");
    public static final List<Integer> invalidPaymentAmounts = Arrays.asList(-1, 0);

    /**
     * This method tests whether the access rights for a given EZShop API function are managed correctly.
     * If an authentication bug is found, Assert.fail() is called.
     *
     * @param apiMethod    method of the EZShop API
     * @param parameters   parameters that should be passed to the API method
     * @param allowedRoles array of all roles that are allowed to call the API method
     *
     * @throws Throwable throws any Exception that is not an UnauthorizedException
     */
    // TODO: this method resets its EZShop instance for each role test and thus only works if the method checks user
    //  access rights before any other action is take due to potentially invalid parameters
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

    /**
     * This method mimics the behaviour of assertThrows available in jUnit 4.13+.
     * This method calls the .run() method of the runnable parameter and catches its exception.
     * If the exception raised matches the provided class, nothing is done.
     * Otherwise, fail is called and an error message is reported.
     * <p>
     * If the runnable does not raise an exception, fail is called and an error
     * message is reported.
     *
     * @param expectedException class of the expected exception
     * @param runnable          runnable that should throw the exception
     * @param <T>               type of the expected exception
     */
    public static <T extends Exception> void assertThrows(Class<T> expectedException, ThrowingRunnable runnable) {
        if (expectedException == null) {
            fail("Got a null expectedException.");
        }

        if (runnable == null) {
            fail("Got a null runnable.");
        }

        boolean exceptionThrown = false;

        try {
            runnable.run();
        } catch (Exception ex) {
            exceptionThrown = true;

            // check if the caught exception is the expected one
            if (!ex.getClass().equals(expectedException)) {
                // if not, fail with an error message
                String message = String.format(
                        "Expected exception %s. Got %s.",
                        expectedException.getSimpleName(),
                        ex.getClass().getSimpleName()
                );
                fail(message);
            }
        }

        if (!exceptionThrown) {
            // an exception was expected
            String message = String.format("Expected exception %s. Got none.", expectedException.getSimpleName());
            fail(message);
        }
    }

    public static <T extends Exception, V> void testInvalidValues(Class<T> exceptionCls, List<V> values,
                                                                  ThrowingFunction<V> function) {
        values.forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(exceptionCls, () -> function.apply(value));
        });
    }

    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public interface ThrowingFunction<T> {
        void apply(T t) throws Exception;
    }
}

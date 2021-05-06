package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestHelpers {

    /**
     * This method tests whether the access rights for a given EZShop API function are managed correctly
     *
     * @param apiMethod method of the EZShop API
     * @param parameters parameters that should be passed to the API method
     * @param allowedRoles array of all roles that are allowed to call the API method
     *
     * @return true if access rights are managed correctly, false otherwise
     * @throws Throwable throws any Exception that is not an UnauthorizedException
     */
    public static boolean testAccessRights(Method apiMethod, Object[] parameters, Role[] allowedRoles) throws Throwable {

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

                // if no UnauthorizedException was thrown, but user should not have rights, return false
                if (!userIsAuthorized) {
                    return false;
                }

            } catch (InvocationTargetException e) {

                // if the invoked method throws an UnauthorizedException, but should be authorized, return false
                if (e.getCause() instanceof UnauthorizedException) {
                    if (userIsAuthorized) {
                        return false;
                    }
                } else {
                    // throw all other errors
                    throw e.getCause();
                }
            }
        }
        return true;
    }
}

package it.polito.ezshop.apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.*;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static it.polito.ezshop.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

public class EZShopTestGetAllOrders {
    private static final EZShop shop = new EZShop();
    private static  User admin;

    static {
        try {
            admin = new User(1, "Admin", "123", Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private ProductType product;
    private Integer order1;
    private Integer order2;
    private Integer order3;
    private Integer order4;
    @Before
    public void beforeEach() throws
            InvalidProductCodeException, InvalidProductDescriptionException, InvalidQuantityException, InvalidPricePerUnitException,
            InvalidProductIdException, InvalidOrderIdException, UnauthorizedException,
            InvalidUsernameException, InvalidPasswordException, InvalidLocationException, InvalidRoleException {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // insert a new product that will be updated later in the tests
        String barcode = "12345678901231";
        shop.createProductType("desc", "12345678901231", 10.0, "note");
        product = shop.getProductTypeByBarCode(barcode);
        order1 = shop.issueOrder(product.getBarCode(), 10, 1.0);
        order2 = shop.issueOrder(product.getBarCode(), 20, 2.0);
        order3 = shop.issueOrder(product.getBarCode(), 30, 3.0);
        order4 = shop.issueOrder(product.getBarCode(), 40, 4.0);
        shop.recordBalanceUpdate(4000.0);

        // order1 and order2 are paid, order3 and order4 stay unpaid
        shop.payOrder(order1);
        shop.payOrder(order2);
    }
    /**
     * Tests that access rights are handled correctly by issueOrder.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("getAllOrders");
        Object[] params = {};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER};

        testAccessRights(targetMethod, params, allowedRoles);
    }
    /**
     * Tests that a list of customers is returned successfully
     */
    @Test
    public void testGetCustomersSuccessfully() throws Exception {

        // login with sufficient rights
        shop.login(admin.getUsername(), admin.getPassword());

        // verify that correct amount of orders have been returned
        List<it.polito.ezshop.data.Order> orders = shop.getAllOrders();
        assertEquals(4, orders.size());

        // get returned customers
        it.polito.ezshop.data.Order returnedOrder1 = orders.stream()
                .filter(o -> o.getOrderId().equals(order1))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Order returnedOrder2 =  orders.stream()
                .filter(o -> o.getOrderId().equals(order2))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Order returnedOrder3 =  orders.stream()
                .filter(o -> o.getOrderId().equals(order3))
                .findAny()
                .orElse(null);
        it.polito.ezshop.data.Order returnedOrder4 =  orders.stream()
                .filter(o -> o.getOrderId().equals(order4))
                .findAny()
                .orElse(null);

        assertNotNull(returnedOrder1);
        assertEquals(order1, returnedOrder1.getOrderId());

        assertNotNull(returnedOrder2);
        assertEquals(order2, returnedOrder2.getOrderId());

        assertNotNull(returnedOrder3);
        assertEquals(order3, returnedOrder3.getOrderId());

        assertNotNull(returnedOrder4);
        assertEquals(order4, returnedOrder4.getOrderId());
    }
}

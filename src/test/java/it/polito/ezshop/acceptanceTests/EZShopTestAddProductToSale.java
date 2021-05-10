package it.polito.ezshop.acceptanceTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.SaleTransaction;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static it.polito.ezshop.acceptanceTests.TestHelpers.assertThrows;
import static it.polito.ezshop.acceptanceTests.TestHelpers.testAccessRights;
import static org.junit.Assert.*;

/**
 * Tests on the EZShop.addProductToSale() method.
 */
public class EZShopTestAddProductToSale {

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final Integer PRODUCT_QUANTITY_1 = 10;
    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final Integer PRODUCT_QUANTITY_2 = 20;
    private static final String PRODUCT_CODE_NOT_EXISTENT_1 = "123456789012";
    private static final String PRODUCT_CODE_NOT_EXISTENT_2 = "457122431814";

    private static final EZShop shop = new EZShop();
    private static final User admin = new User(0, "Admin", "123", Role.ADMINISTRATOR);

    private Integer transactionId;

    @Before
    public void beforeEach() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());

        // add product with code PRODUCT_CODE_1 to the shop
        int id1 = shop.createProductType("desc", PRODUCT_CODE_1, 10.0, "note");
        shop.updatePosition(id1, "1-1-1");
        shop.updateQuantity(id1, PRODUCT_QUANTITY_1);

        // add product with code PRODUCT_CODE_2 to the shop
        int id2 = shop.createProductType("desc", PRODUCT_CODE_2, 20.0, "note");
        shop.updatePosition(id2, "1-1-2");
        shop.updateQuantity(id2, PRODUCT_QUANTITY_2);

        transactionId = shop.startSaleTransaction();
    }

    /**
     * Tests that access rights are handled correctly by addProductToSale.
     */
    @Test
    public void testAuthorization() throws Throwable {
        Method targetMethod = EZShop.class.getMethod("addProductToSale", Integer.class, String.class, int.class);
        Object[] params = {transactionId, PRODUCT_CODE_1, 1};
        Role[] allowedRoles = new Role[]{Role.ADMINISTRATOR, Role.SHOP_MANAGER, Role.CASHIER};

        testAccessRights(targetMethod, params, allowedRoles);
    }

    /**
     * If the id is null|negative|zero, the method should throw InvalidTransactionIdException
     */
    @Test()
    public void testInvalidId() {
        // boundary values for the id parameter
        Arrays.asList(null, -1, 0).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidTransactionIdException.class, () -> {
                // try to update a product with the boundary value
                shop.addProductToSale(value, PRODUCT_CODE_1, 1);
            });
        });
    }

    /**
     * If the code is null|empty|NaN|invalid, the method should throw InvalidProductCodeException
     */
    @Test()
    public void testInvalidProductCode() {
        // test values for the product code parameter
        // "12345678901232" is an invalid product code (wrong check digit)
        Arrays.asList(null, "", "123456789B123A", "12345678901232").forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidProductCodeException.class, () -> {
                // try to update a product with the boundary value
                shop.addProductToSale(transactionId, value, 1);
            });
        });
    }

    /**
     * If the amount is less than 0, the method should throw InvalidQuantityException
     * TODO: what if the amount is zero?
     */
    @Test()
    public void testInvalidAmount() {
        // test values for the amount parameter
        Arrays.asList(-10, -1).forEach((value) -> {
            // for each boundary value check that the correct exception is thrown
            assertThrows(InvalidQuantityException.class, () -> {
                // try to update a product with the boundary value
                shop.addProductToSale(transactionId, PRODUCT_CODE_1, value);
            });
        });
    }

    /**
     * If the product does not exist, the method should return false
     */
    @Test()
    public void testProductDoesNotExist() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_NOT_EXISTENT_1, 1));
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_NOT_EXISTENT_2, 1));
    }

    /**
     * If the quantity of product cannot satisfy the request, the method should return false
     */
    @Test()
    public void testAmountAboveQuantity() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_1, PRODUCT_QUANTITY_1 + 1));
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_2, PRODUCT_QUANTITY_2 + 1));
    }

    /**
     * If the transaction is not open, the method should return false
     */
    @Test()
    public void testClosedTransaction() throws InvalidTransactionIdException, UnauthorizedException,
            InvalidProductCodeException, InvalidQuantityException {
        shop.endSaleTransaction(transactionId);
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_1, PRODUCT_QUANTITY_1));
    }

    /**
     * Add products to a sale transaction successfully
     */
    @Test
    public void testAddProductsToSaleSuccessfully() throws InvalidQuantityException, InvalidTransactionIdException,
            InvalidProductCodeException, UnauthorizedException {
        int amount = PRODUCT_QUANTITY_1 / 2;

        // 1. add amount of PRODUCT_CODE_1
        assertTrue(shop.addProductToSale(transactionId, PRODUCT_CODE_1, amount));

        // 1.1 verify that the quantity of PRODUCT_CODE_1 in the inventory is correctly updated
        ProductType p1 = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        assertEquals((Integer) (PRODUCT_QUANTITY_1 - amount), p1.getQuantity());

        // 2. add a new amount of PRODUCT_CODE_1
        assertTrue(shop.addProductToSale(transactionId, PRODUCT_CODE_1, PRODUCT_QUANTITY_1 - amount));
        // 2.1 verify that the quantity in the inventory is correctly updated
        p1 = shop.getProductTypeByBarCode(PRODUCT_CODE_1);
        assertEquals((Integer) 0, p1.getQuantity());

        // 3. add PRODUCT_QUANTITY_2 of PRODUCT_CODE_2
        assertFalse(shop.addProductToSale(transactionId, PRODUCT_CODE_2, PRODUCT_QUANTITY_2));
        // 3.1 verify that the quantity of PRODUCT_CODE_2 in the inventory is correctly updated
        p1 = shop.getProductTypeByBarCode(PRODUCT_CODE_2);
        assertEquals((Integer) 0, p1.getQuantity());

        // 3. verify the final status of the transaction
        shop.endSaleTransaction(transactionId);
        SaleTransaction saleTransaction = shop.getSaleTransaction(transactionId);

        // 3.1 check amount of product 1 in the transaction
        Integer amountP1 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(PRODUCT_CODE_1))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(-1);
        assertEquals(PRODUCT_QUANTITY_1, amountP1);

        // 3.2 check amount of product 2 in the transaction
        Integer amountP2 = saleTransaction.getEntries().stream()
                .filter(x -> x.getBarCode().equals(PRODUCT_CODE_2))
                .findAny()
                .map(TicketEntry::getAmount)
                .orElse(0);
        assertEquals(PRODUCT_QUANTITY_2, amountP2);
    }

}

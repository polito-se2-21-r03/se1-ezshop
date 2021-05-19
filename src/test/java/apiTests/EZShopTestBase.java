package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;

public class EZShopTestBase {

    protected static final ProductType product1, product2, product3, product4;
    protected static final EZShop shop = new EZShop();

    private static final String PRODUCT_CODE_1 = "12345678901231";
    private static final Double PRODUCT_PRICE_1 = 15.0;
    private static final Integer PRODUCT_QUANTITY_1 = 10;
    private static final String PRODUCT_DESCRIPTION_1 = "description product 1";
    private static final String PRODUCT_NOTE_1 = "note product 1";
    private static final String PRODUCT_POSITION_1 = "1-1-1";

    private static final String PRODUCT_CODE_2 = "1234567890128";
    private static final Double PRODUCT_PRICE_2 = 25.0;
    private static final Integer PRODUCT_QUANTITY_2 = 10;
    private static final String PRODUCT_DESCRIPTION_2 = "description product 2";
    private static final String PRODUCT_NOTE_2 = "note product 2";
    private static final String PRODUCT_POSITION_2 = "1-1-2";

    private static final String PRODUCT_CODE_3 = "123456789012";
    private static final Double PRODUCT_PRICE_3 = 17.50;
    private static final Integer PRODUCT_QUANTITY_3 = 10;
    private static final String PRODUCT_DESCRIPTION_3 = "description product 3";
    private static final String PRODUCT_NOTE_3 = "note product 3";
    private static final String PRODUCT_POSITION_3 = "1-1-3";

    private static final String PRODUCT_CODE_4 = "5634567890122";
    private static final Double PRODUCT_PRICE_4 = 3.50;
    private static final Integer PRODUCT_QUANTITY_4 = 20;
    private static final String PRODUCT_DESCRIPTION_4 = "description product 4";
    private static final String PRODUCT_NOTE_4 = "note product 4";
    private static final String PRODUCT_POSITION_4 = "1-1-4";

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";
    protected static User admin;

    static {
        try {
            admin = new User(1, ADMIN_USERNAME, ADMIN_PASSWORD, Role.ADMINISTRATOR);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // initialization of product 1
            product1 = new it.polito.ezshop.model.ProductType(1, PRODUCT_DESCRIPTION_1, PRODUCT_CODE_1, PRODUCT_PRICE_1,
                    PRODUCT_NOTE_1, PRODUCT_QUANTITY_1, new Position(PRODUCT_POSITION_1));

            // initialization of product 2
            product2 = new it.polito.ezshop.model.ProductType(2, PRODUCT_DESCRIPTION_2, PRODUCT_CODE_2, PRODUCT_PRICE_2,
                    PRODUCT_NOTE_2, PRODUCT_QUANTITY_2, new Position(PRODUCT_POSITION_2));

            // initialization of product 3
            product3 = new it.polito.ezshop.model.ProductType(3, PRODUCT_DESCRIPTION_3, PRODUCT_CODE_3, PRODUCT_PRICE_3,
                    PRODUCT_NOTE_3, PRODUCT_QUANTITY_3, new Position(PRODUCT_POSITION_3));

            // initialization of product 4
            product4 = new it.polito.ezshop.model.ProductType(4, PRODUCT_DESCRIPTION_4, PRODUCT_CODE_4, PRODUCT_PRICE_4,
                    PRODUCT_NOTE_4, PRODUCT_QUANTITY_4, new Position(PRODUCT_POSITION_4));

        } catch (InvalidLocationException | InvalidProductDescriptionException | InvalidProductCodeException | InvalidPricePerUnitException | InvalidProductIdException | InvalidQuantityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Reset the status of the shop, create an admin user and log in
     */
    protected void reset() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole().getValue());
        // and log in with that user
        shop.login(admin.getUsername(), admin.getPassword());
    }

    /**
     * Add multiple products to the shop
     *
     * @param products list of products to add
     */
    protected void addProducts(ProductType... products) throws Exception {
        for (ProductType p : products) {
            addProduct(p);
        }
    }

    /**
     * Add a product to the shop
     *
     * @param p product to add
     * @return the id of the product
     */
    private int addProduct(ProductType p) throws Exception {
        int id = shop.createProductType(p.getProductDescription(), p.getBarCode(), p.getPricePerUnit(), p.getNote());
        shop.updatePosition(id, p.getPosition().toString());
        shop.updateQuantity(id, p.getQuantity());

        return id;
    }

    /**
     * Compute the value of a product as
     * (1 - transactionDiscountRate) * (1 - productDiscountRate) * quantity * pricePerUnit
     *
     * @param pricePerUnit            unitary price of the product
     * @param quantity                quantity of the product
     * @param productDiscountRate     product's discount rate
     * @param transactionDiscountRate sale transaction's discount rate
     * @return the computed value
     */
    protected double computeValue(double pricePerUnit, int quantity, double productDiscountRate, double transactionDiscountRate) {
        return (1 - transactionDiscountRate) * (1 - productDiscountRate) * quantity * pricePerUnit;
    }

}

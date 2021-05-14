package apiTests;

import it.polito.ezshop.data.EZShop;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.Role;
import it.polito.ezshop.model.User;

public class EZShopTestBase {

    protected static final ProductType product1, product2, product3, product4;
    protected static final User admin;
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

    static {
        // initialization of product 1
        product1 = new it.polito.ezshop.model.ProductType(PRODUCT_NOTE_1, PRODUCT_DESCRIPTION_1, PRODUCT_CODE_1, PRODUCT_PRICE_1, -1);
        product1.setLocation(PRODUCT_POSITION_1);
        product1.setQuantity(PRODUCT_QUANTITY_1);

        // initialization of product 2
        product2 = new it.polito.ezshop.model.ProductType(PRODUCT_NOTE_2, PRODUCT_DESCRIPTION_2, PRODUCT_CODE_2, PRODUCT_PRICE_2, -1);
        product2.setLocation(PRODUCT_POSITION_2);
        product2.setQuantity(PRODUCT_QUANTITY_2);

        // initialization of product 3
        product3 = new it.polito.ezshop.model.ProductType(PRODUCT_NOTE_3, PRODUCT_DESCRIPTION_3, PRODUCT_CODE_3, PRODUCT_PRICE_3, -1);
        product3.setLocation(PRODUCT_POSITION_3);
        product3.setQuantity(PRODUCT_QUANTITY_3);

        // initialization of product 4
        product4 = new it.polito.ezshop.model.ProductType(PRODUCT_NOTE_4, PRODUCT_DESCRIPTION_4, PRODUCT_CODE_4, PRODUCT_PRICE_4, -1);
        product4.setLocation(PRODUCT_POSITION_4);
        product4.setQuantity(PRODUCT_QUANTITY_4);

        // initialization of the admin user
        admin = new User(0, ADMIN_USERNAME, ADMIN_PASSWORD, Role.ADMINISTRATOR);
    }

    /**
     * Reset the status of the shop, create an admin user and log in
     */
    protected void reset() throws Exception {
        // reset the state of EZShop
        shop.reset();
        // create a new user
        shop.createUser(admin.getUsername(), admin.getPassword(), admin.getRole());
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
        shop.updatePosition(id, p.getLocation());
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

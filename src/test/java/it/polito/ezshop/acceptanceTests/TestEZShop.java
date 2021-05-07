package it.polito.ezshop.acceptanceTests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EZShopTestCreateProductType.class,
        EZShopTestUpdateProduct.class,
        EZShopTestGetAllProductTypes.class,
        EZShopTestDeleteProductType.class,
        EZShopTestGetProductTypeByBarCode.class,
        EZShopTestGetProductTypesByDescription.class,
        EZShopTestDefineCustomer.class
})
public class TestEZShop {


}

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
        EZShopTestDefineCustomer.class,
        EZShopTestStartSaleTransaction.class,
        EZShopTestAddProductToSale.class,
        EZShopTestDeleteProductFromSale.class,
        EZShopTestApplyDiscountRateToProduct.class,
        EZShopTestApplyDiscountRateToSale.class,
        EZShopTestEndSaleTransaction.class,
        EZShopTestDeleteSaleTransaction.class,
        EZShopTestGetSaleTransaction.class,
        EZShopTestStartReturnTransaction.class,
        EZShopTestReturnProduct.class
})
public class TestEZShop {


}

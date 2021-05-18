import apiTests.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import unitTests.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // FR 1
        // FR 3
        EZShopTestCreateProductType.class,
        EZShopTestUpdateProduct.class,
        EZShopTestGetAllProductTypes.class,
        EZShopTestDeleteProductType.class,
        EZShopTestGetProductTypeByBarCode.class,
        EZShopTestGetProductTypesByDescription.class,
        // FR 4
        // FR 5
        EZShopTestDefineCustomer.class,
        EZShopTestModifyCustomer.class,
        EZShopTestDeleteCustomer.class,
        EZShopTestGetCustomer.class,
        EZShopTestGetAllCustomers.class,
        EZShopTestCreateCard.class,
        EZShopTestAttachCardToCustomer.class,
        EZShopTestModifyPointsOnCard.class,
        // FR 6
        EZShopTestStartSaleTransaction.class,
        EZShopTestAddProductToSale.class,
        EZShopTestDeleteProductFromSale.class,
        EZShopTestApplyDiscountRateToProduct.class,
        EZShopTestApplyDiscountRateToSale.class,
        EZShopTestComputePointsForSale.class,
        EZShopTestEndSaleTransaction.class,
        EZShopTestDeleteSaleTransaction.class,
        EZShopTestGetSaleTransaction.class,
        EZShopTestStartReturnTransaction.class,
        EZShopTestReturnProduct.class,
        EZShopTestEndReturnTransaction.class,
        // FR 7
        EZShopTestReceiveCashPayment.class,
        EZShopTestReturnCashPayment.class,
        // FR 8
        EZShopTestRecordBalanceUpdate.class,
        EZShopTestGetCreditsAndDebits.class,
        EZShopTestComputeBalance.class,
        // Unit tests
        TestAccountBook.class,
        TestIsValidCreditCardNumber.class,
        TestUtils.class,
        TestPosition.class,
        TestProductType.class,
        TestProductTypeAdapter.class,
        TestUser.class,
        TestUserAdapter.class
})

public class TestEZShop {
}

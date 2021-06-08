package it.polito.ezshop.apiTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EZShopTestReset.class,
        // FR 1
        EZShopTestCreateUser.class,
        EZShopTestDeleteUser.class,
        EZShopTestGetAllUsers.class,
        EZShopTestGetUser.class,
        EZShopTestUpdateUserRights.class,
        EZShopTestLogin.class,
        EZShopTestLogout.class,
        // FR 3
        EZShopTestCreateProductType.class,
        EZShopTestUpdateProduct.class,
        EZShopTestGetAllProductTypes.class,
        EZShopTestDeleteProductType.class,
        EZShopTestGetProductTypeByBarCode.class,
        EZShopTestGetProductTypesByDescription.class,
        // FR 4
        EZShopTestUpdateQuantity.class,
        EZShopTestUpdatePosition.class,
        EZShopTestIssueOrder.class,
        EZShopTestPayOrderFor.class,
        EZShopTestPayOrder.class,
        EZShopTestRecordOrderArrival.class,
        EZShopTestGetAllOrders.class,

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
        EZShopTestAddProductToSaleRFID.class,
        EZShopTestDeleteProductFromSale.class,
        EZShopTestDeleteProductFromSaleRFID.class,
        EZShopTestApplyDiscountRateToProduct.class,
        EZShopTestApplyDiscountRateToSale.class,
        EZShopTestComputePointsForSale.class,
        EZShopTestEndSaleTransaction.class,
        EZShopTestDeleteSaleTransaction.class,
        EZShopTestGetSaleTransaction.class,
        EZShopTestStartReturnTransaction.class,
        EZShopTestReturnProduct.class,
        EZShopTestEndReturnTransaction.class,
        EZShopTestDeleteReturnTransaction.class,
        // FR 7
        EZShopTestReceiveCashPayment.class,
        EZShopTestReceiveCreditCardPayment.class,
        EZShopTestReturnCashPayment.class,
        EZShopTestReturnCreditCardPayment.class,
        // FR 8
        EZShopTestRecordBalanceUpdate.class,
        EZShopTestGetCreditsAndDebits.class,
        EZShopTestComputeBalance.class,
})
public class APITests {
}

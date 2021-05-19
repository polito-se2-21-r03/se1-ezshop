package unitTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Customer, CustomerAdapter, LoyaltyCard
        TestCustomer.class,
        TestCustomerAdapter.class,
        TestLoyaltyCard.class,
        // User, UserAdapter
        TestUser.class,
        TestUserAdapter.class,
        // ProductType and Position
        TestProductType.class,
        TestProductTypeAdapter.class,
        TestPosition.class,
        // transaction
        TestBalanceOperationAdapter.class,
        TestOperationStatus.class,
        TestOrderAdapter.class,
        TestTicketEntry.class,
        TestTicketEntryAdapter.class,
        TestSaleTransaction.class,
        TestSaleTransactionAdapter.class,
        TestReturnTransactionItem.class,
        // Utils
        TestUtils.class,
        TestIsValidCreditCardNumber.class
})
public class UnitTests {
}

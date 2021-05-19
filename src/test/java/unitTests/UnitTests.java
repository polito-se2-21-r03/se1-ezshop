package unitTests;

import it.polito.ezshop.model.adapters.TicketEntryAdapter;
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
        TestTicketEntry.class,
        TestTicketEntryAdapter.class,
        TestSaleTransaction.class,
        TestSaleTransactionAdapter.class,
        TestReturnTransactionItem.class,
        // AccountBook
        TestAccountBook.class,
        // Json interface
        TestJsonInterface.class,
        // Utils
        TestUtils.class,
        TestIsValidCreditCardNumber.class
})
public class UnitTests {
}

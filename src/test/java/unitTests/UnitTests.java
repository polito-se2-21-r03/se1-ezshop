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
        TestUserAdapter.class
})
public class UnitTests {
}

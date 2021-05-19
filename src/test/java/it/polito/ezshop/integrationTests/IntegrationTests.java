package it.polito.ezshop.integrationTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestAccountBook.class,
        TestCustomerList.class,
        TestJsonInterface.class
})
public class IntegrationTests {
}

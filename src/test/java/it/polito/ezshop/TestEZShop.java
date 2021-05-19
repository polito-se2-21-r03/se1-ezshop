package it.polito.ezshop;

import it.polito.ezshop.apiTests.APITests;
import it.polito.ezshop.integrationTests.IntegrationTests;
import it.polito.ezshop.unitTests.UnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UnitTests.class,
        IntegrationTests.class,
        APITests.class
})
public class TestEZShop {
}

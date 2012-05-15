package ch.bazaruto.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BazarutoTests.class, NanoHTTPDTests.class })
public class AllTests {

}

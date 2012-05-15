package ch.bazaruto.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	BasicHTTPTests.class, 
	NanoHTTPDTests.class,
	ZipStorageTest.class,
})
public class AllTests {

}

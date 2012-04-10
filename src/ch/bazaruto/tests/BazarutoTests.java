package ch.bazaruto.tests;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import ch.bazaruto.Bazaruto;
import ch.bazaruto.Request;
import ch.bazaruto.Response;
import ch.bazaruto.tests.controllers.SimpleController1;

public class BazarutoTests {
	
	public Bazaruto bazaruto = new Bazaruto();

	@Before
	public void setUp() throws Exception {		
		bazaruto.addController(SimpleController1.class);
	}

	@Test
	public void testIndex() {
		Request req = new Request("/sc1/", "get", null, null, null);
		Response res = bazaruto.dispatch(req);
		assertEquals("SC1:index", res.getData());
	}

	@Test
	public void testDetail() {
		Properties parms = new Properties();
		parms.put("id", 1);
		Request req = new Request("/sc1/details", "get", null, parms, null);
		Response res = bazaruto.dispatch(req);
		assertEquals("SC1:detail:1", res.getData());
	}
	
	@Test
	public void testTimesTwo() {
		Properties parms = new Properties();
		parms.put("number", 21);
		Request req = new Request("/sc1/timestwo", "post", null, parms, null);
		Response res = bazaruto.dispatch(req);
		assertEquals("SC1:timestwo:42", res.getData());
	}
}

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
    public void testSimpleGet() {
        Request req = new Request("GET", "/sc1/");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:get", res.toString());
    }

    @Test
    public void testIntArg() {
        Request req = new Request("GET", "/sc1/intarg/1/");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:intarg:1", res.toString());
    }
    
    @Test
    public void testFloatArg() {
        Request req = new Request("GET", "/sc1/floatarg/2.0/");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:floatarg:2.0", res.toString());
    }
    
    @Test
    public void testStringArg() {
        Request req = new Request("GET", "/sc1/stringarg/Hallo");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:stringarg:Hallo", res.toString());
    }
    
    @Test
    public void testPostArg() {
        Properties parms = new Properties();
        parms.put("number", 42);
        Request req = new Request("POST", "/sc1/postarg", parms);
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:postarg:42", res.toString());
    }
    
    @Test
    public void testPut() {
        Request req = new Request("PUT", "/sc1/put");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:put", res.toString());
    }
    
    @Test
    public void testDelete() {
        Request req = new Request("DELETE", "/sc1/delete");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:delete", res.toString());
    }
}

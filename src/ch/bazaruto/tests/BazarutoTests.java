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
        Request req = new Request("GET", "/sc1/");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:index", res.getData());
    }

    @Test
    public void testDetail() {
        Properties parms = new Properties();
        parms.put("id", 1);
        Request req = new Request("GET", "/sc1/details", parms);
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:detail:1", res.getData());
    }
    
    @Test
    public void testTimesTwo() {
        Properties parms = new Properties();
        parms.put("number", 21);
        Request req = new Request("POST", "/sc1/timestwo", parms);
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:timestwo:42", res.getData());
    }
    
    @Test
    public void testPut() {
        Request req = new Request("PUT", "/sc1/put");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:put", res.getData());
    }
    
    @Test
    public void testDelete() {
        Request req = new Request("DELETE", "/sc1/delete");
        Response res = bazaruto.dispatch(req);
        assertEquals("SC1:delete", res.getData());
    }
}

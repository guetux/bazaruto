package ch.bazaruto.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;

import ch.bazaruto.NanoHTTPD;
import ch.bazaruto.Request;
import ch.bazaruto.Response;

public class NanoHTTPDTests {

    HttpClient httpclient = new DefaultHttpClient();
    NanoHTTPD httpd;
    String response;
    
    @Before
    public void setUp() throws Exception {
        response = "";
    }
    
    @Test
    public void testFullRequest() {
        
        class TestHTTPD extends NanoHTTPD {
            @Override
            public Response serve(Request req) {
                return new Response("TestMSG", HTTP_OK, MIME_HTML);
            }
        }
        
        try {
            httpd = new TestHTTPD();
            httpd.start();
            HttpGet httpget = new HttpGet("http://localhost:9000/");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            fail("Could not perform full request!");
        } finally {
            httpclient.getConnectionManager().shutdown();
            httpd.stop();
        }
        
        assertEquals(response, "TestMSG");
    }

}

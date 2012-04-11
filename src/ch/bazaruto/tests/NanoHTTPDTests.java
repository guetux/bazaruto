package ch.bazaruto.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
    public void testGetParameterIsInt() {
        class TimesTwoHTTPD extends NanoHTTPD {
            @Override
            public Response serve(Request req) {
                return new Response(""+(Integer)req.parms.get("i") * 2, HTTP_OK, MIME_HTML);
            }
        }
        
        try {
            httpd = new TimesTwoHTTPD();
            httpd.startServer();
            HttpGet httpget = new HttpGet("http://localhost:9000/?i=21");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            fail("Could not perform full request!");
        } finally {
            httpclient.getConnectionManager().shutdown();
            httpd.stop();
        }
        
        assertEquals(response, "42");        
    }
    
    @Test
    public void testPostParameterIsInt() {
        class TimesTwoHTTPD extends NanoHTTPD {
            @Override
            public Response serve(Request req) {
                return new Response(""+(Integer)req.parms.get("i") * 2, HTTP_OK, MIME_HTML);
            }
        }
        
        try {
            httpd = new TimesTwoHTTPD();
            httpd.startServer();
            HttpPost httppost = new HttpPost("http://localhost:9000/");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("i","21"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);
        } catch (IOException e) {
            fail("Could not perform full request!");
        } finally {
            httpclient.getConnectionManager().shutdown();
            httpd.stop();
        }
        
        assertEquals(response, "42");        
    }
    
    @Test
    public void testGetParameterIsDouble() {
        class TimesTwoHTTPD extends NanoHTTPD {
            @Override
            public Response serve(Request req) {
                return new Response(""+(Double)req.parms.get("i") * 5, HTTP_OK, MIME_HTML);
            }
        }
        
        try {
            httpd = new TimesTwoHTTPD();
            httpd.startServer();
            HttpGet httpget = new HttpGet("http://localhost:9000/?i=8.4");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpget, responseHandler);
        } catch (IOException e) {
            fail("Could not perform full request!");
        } finally {
            httpclient.getConnectionManager().shutdown();
            httpd.stop();
        }
        
        assertEquals(response, "42.0");        
    }
    
    @Test
    public void testPostParameterIsDouble() {
        class TimesTwoHTTPD extends NanoHTTPD {
            @Override
            public Response serve(Request req) {
                return new Response(""+(Double)req.parms.get("i") * 5, HTTP_OK, MIME_HTML);
            }
        }
        
        try {
            httpd = new TimesTwoHTTPD();
            httpd.startServer();
            HttpPost httppost = new HttpPost("http://localhost:9000/");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("i","8.4"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);
        } catch (IOException e) {
            fail("Could not perform full request!");
        } finally {
            httpclient.getConnectionManager().shutdown();
            httpd.stop();
        }
        
        assertEquals(response, "42.0");        
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
            httpd.startServer();
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

package ch.bazaruto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Request {

    /*
     * The HTTP method, one of ["GET", "POST", "PUT", "DELETE"]
     */
    public String method;
	
    /*
     * The request uri 
     */
    public String uri;
    
    /*
     * HTTP Version
     */
    public String version;
    
    /* 
     * Local part of the uri that matched the method
     */
    public String path;
    
    /*
     * Request headers
     */
    public Properties header = new Properties();
    
    /*
     * Request parameters, either from POST or GET
     * TODO: Refactor this!
     */
    public Properties parms = new Properties();
    
    /*
     * Attached files for multipart/form-data requests
     */
    public Properties files = new Properties();
    
    /*
     * The raw request body as it was sent over the wire
     */
    public byte[] rawBody;
    
    /*
     * Default basic constructor
     */
    public Request() {}
    
    /*
     * Extended Constructors
     */
    public Request(String method, String uri, String version, Properties parms, Properties header, Properties files) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.parms = parms;
        this.header = header;
        this.files = files;
    }
    
    public Request(String method, String uri, String version, Properties parms, Properties header) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.parms = parms;
        this.header = header;
    }
    
    public Request(String method, String uri, String version, Properties parms) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.parms = parms;
    }
    
    public Request(String method, String uri,  String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }
    
    public Request(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String getRequestLine() {
    	return method + " " + uri + " " + version;
    }
    
    public String getRequestBody() {
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(rawBody);
            BufferedReader in = new BufferedReader(new InputStreamReader(bin));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                body.append(line);
                body.append("\n");
            }
            return body.toString();
        } catch (IOException e) {
            return null;
        }
    }
}

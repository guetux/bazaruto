package ch.bazaruto;

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
}

package ch.bazaruto;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * HTTP response. Return one of these from serve().
 */
public class Response {
    
    /**
     * HTTP status code after processing, e.g. "200 OK", HTTP_OK
     */
    public String status = NanoHTTPD.HTTP_OK;

    /**
     * MIME type of content, e.g. "text/html"
     */
    public String mimeType = NanoHTTPD.MIME_HTML;

    /**
     * Data of the response, may be null.
     */
    public InputStream data;

    /**
     * Headers for the HTTP response. Use addHeader() to add lines.
     */
    public Properties header = new Properties();
    
    /**
     * Default constructor: response = HTTP_OK, mime = "text/html", data = null
     */
    public Response() {
        this.data = toInputStream("");
    }

    /* Basic Constructors */
    
    public Response(InputStream data) {
        this.data = data;
    }
    
    public Response(String txt) {
        this.data = toInputStream(txt);
    }
    
    public Response(InputStream data, String status) {
        this.data = data;
        this.status = status;
    }

    public Response(String txt, String status) {
        this.data = toInputStream(txt);
        this.status = status;
    }

    public Response(InputStream data, String status, String mimeType) {
        this.data = data;
        this.mimeType = mimeType;
        this.status = status;
    }

    public Response(String txt, String status, String mimeType) {
        this.data = toInputStream(txt);
        this.mimeType = mimeType;
        this.status = status;
    }

    private InputStream toInputStream(String txt) {
        return new ByteArrayInputStream(txt.getBytes());
    }
    
    public String toString() {
        try {
            return new java.util.Scanner(this.data).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
    
    /**
     * Adds given line to the header.
     */
    public void addHeader(String name, String value) {
        header.put(name, value);
    }
}
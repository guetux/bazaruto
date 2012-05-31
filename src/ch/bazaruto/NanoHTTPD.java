package ch.bazaruto;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.bazaruto.storage.FileStorage;
import ch.bazaruto.storage.Storage;

/*            
Copyright (C) 2001,2005-2011 by Jarno Elonen <elonen@iki.fi>
and Copyright (C) 2010 by Konstantinos Togias <info@ktogias.gr>

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer. Redistributions in
binary form must reproduce the above copyright notice, this list of
conditions and the following disclaimer in the documentation and/or other
materials provided with the distribution. The name of the author may not
be used to endorse or promote products derived from this software without
specific prior written permission. 
 
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 (partially 1.1) server in Java
 * 
 * <p>
 * NanoHTTPD version 1.25, Copyright &copy; 2001,2005-2012 Jarno Elonen
 * (elonen@iki.fi, http://iki.fi/elonen/) and Copyright &copy; 2010 Konstantinos
 * Togias (info@ktogias.gr, http://ktogias.gr)
 * 
 * <p>
 * <b>Features + limitations: </b>
 * <ul>
 * 
 * <li>Only one Java file</li>
 * <li>Java 1.1 compatible</li>
 * <li>Released as open source, Modified BSD licence</li>
 * <li>No fixed config files, logging, authorization etc. (Implement yourself if
 * you need them.)</li>
 * <li>Supports parameter parsing of GET and POST methods (+ rudimentary PUT
 * support in 1.25)</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Supports file upload (since version 1.2, 2010)</li>
 * <li>Supports partial content (streaming)</li>
 * <li>Supports ETags</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Default code serves files and shows all HTTP parameters and headers</li>
 * <li>File server supports directory listing, index.html and index.htm</li>
 * <li>File server supports partial content (streaming)</li>
 * <li>File server supports ETags</li>
 * <li>File server does the 301 redirection trick for directories without '/'</li>
 * <li>File server supports simple skipping for files (continue download)</li>
 * <li>File server serves also very long files without memory overhead</li>
 * <li>Contains a built-in list of most common mime types</li>
 * <li>All header names are converted lowercase so they don't vary between
 * browsers/clients</li>
 * 
 * </ul>
 * 
 * <p>
 * <b>Ways to use: </b>
 * <ul>
 * 
 * <li>Run as a standalone app, serves files and shows requests</li>
 * <li>Subclass serve() and embed to your own program</li>
 * <li>Call serveFile() from serve() with your own base directory</li>
 * 
 * </ul>
 * 
 * See the top of the source file for distribution license 
 * (Modified BSD licence)
 */
public class NanoHTTPD {
    // Some HTTP response status codes
    public static final String 
            HTTP_OK = "200 OK",
            HTTP_PARTIALCONTENT = "206 Partial Content",
            HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable",
            HTTP_MOVED = "301 Moved Permanently",
            HTTP_REDIRECT = "302 Found",
            HTTP_NOTMODIFIED = "304 Not Modified",
            HTTP_FORBIDDEN = "403 Forbidden", HTTP_NOTFOUND = "404 Not Found",
            HTTP_BADREQUEST = "400 Bad Request",
            HTTP_INTERNALERROR = "500 Internal Server Error",
            HTTP_NOTIMPLEMENTED = "501 Not Implemented",
    		HTTP_BADGATEWAY = "502 Bad Gateway";

    // Common mime types for response content
    public static final String 
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml",
    		MIME_JSON= "application/json";
    
    // Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
    private static Hashtable<String, String> mimeTypes = new Hashtable<String, String>();
    static {
        StringTokenizer st = new StringTokenizer(
                  "css        text/css "
                + "htm        text/html " 
                + "html       text/html " 
                + "xml        text/xml "
                + "xhtml      application/xhtml+xml "
                + "txt        text/plain " 
                + "asc        text/plain " 
                + "gif        image/gif "
                + "jpg        image/jpeg " 
                + "jpeg       image/jpeg " 
                + "png        image/png "
                + "mp3        audio/mpeg " 
                + "m3u        audio/mpeg-url "
                + "mp4        video/mp4 " 
                + "ogv        video/ogg " 
                + "flv        video/x-flv "
                + "mov        video/quicktime "
                + "swf        application/x-shockwave-flash "
                + "js         application/javascript " 
                + "pdf        application/pdf "
                + "doc        application/msword " 
                + "ogg        application/x-ogg "
                + "zip        application/octet-stream "
                + "exe        application/octet-stream "
                + "class      application/octet-stream ");
        while (st.hasMoreTokens())
            mimeTypes.put(st.nextToken(), st.nextToken());
    }

    private static int bufferSize = 16 * 1024;
    private int tcpPort = 9000;
    private ServerSocket serverSocket;
    public int maxConcurrentRequests = 4;
    Thread dispatcherThread;
    private ExecutorService execSvc;

    
    /**
     * GMT date formatter
     */
    private static final ThreadLocal<SimpleDateFormat> gmtFrmt = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat gmtFrmt = new SimpleDateFormat( "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            return gmtFrmt;
        };
    };
    
    // ==================================================
    // API parts
    // ==================================================

    /**
     * Override this to customize the server.
     * <p>
     * 
     * (By default, this delegates to serveFile() and allows directory listing.)
     * 
     * @param uri
     *            Percent-decoded URI without parameters, for example
     *            "/index.cgi"
     * @param method
     *            "GET", "POST" etc.
     * @param parms
     *            Parsed, percent decoded parameters from URI and, in case of
     *            POST, data.
     * @param header
     *            Header entries, percent decoded
     * @return HTTP response, see class Response for details
     */
    public Response serve(Request req) {
        PrintStream out = System.out;
        out.println(req.method + " '" + req.uri + "' ");

        Enumeration<?> e = req.header.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            out.println("  HDR: '" + value + "' = '"
                    + req.header.getProperty(value) + "'");
        }
        e = req.parms.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            out.println("  PRM: '" + value + "' = '"
                    + req.parms.getProperty(value) + "'");
        }
        e = req.files.propertyNames();
        while (e.hasMoreElements()) {
            String value = (String) e.nextElement();
            out.println("  UPLOADED: '" + value + "' = '"
                    + req.files.getProperty(value) + "'");
        }

        Storage root = new FileStorage(".");
        return serveFile(req, root, true);
    }

    
    
    // ==================================================
    // Socket & server code
    // ==================================================
    
    public void start() {
    	start(tcpPort);
    }
    
    public void start(int port) {
    	tcpPort = port;
        if (serverSocket != null && !serverSocket.isClosed()) 
            return;
        
        try {
            execSvc = Executors.newFixedThreadPool(maxConcurrentRequests);
            serverSocket = new ServerSocket(tcpPort);
            
            dispatcherThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true)
                            execSvc.execute(new HTTPSession(serverSocket.accept()));
                    } catch (IOException ioe) {}
                }
            });
            //dispatcherThread.setDaemon(true);
            dispatcherThread.setName("NanoHTTPD Dispatcher");
            dispatcherThread.start();
    		System.out.println("Server started on " + 
    				serverSocket.getInetAddress().getHostAddress() + ":" + 
    				serverSocket.getLocalPort());
        } catch (IOException ioe) {
            System.err.println("Cannot bind to port " + tcpPort + "!");
        }
    }
    
    /**
     * Stops the server.
     */
    public void stop() {
        try {
            serverSocket.close();
            dispatcherThread.join();
            this.execSvc.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles one session, i.e. parses the HTTP request and returns the
     * response.
     */
    private class HTTPSession implements Runnable {
        private Socket socket;
        
        public HTTPSession(Socket s) {
            socket = s;
        }

        public void run() {
            try {
                InputStream is = socket.getInputStream();
                if (is == null)
                    return;
                
                // Read the first 8192 bytes.
                // The full header should fit in here.
                // Apache's default header limit is 8KB.
                int bufsize = 8192;
                byte[] buf = new byte[bufsize];
                int rlen = is.read(buf, 0, bufsize);
                if (rlen <= 0)
                    return;

                // Create a BufferedReader for parsing the header.
                ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
                BufferedReader hin = new BufferedReader(new InputStreamReader(hbis));
                Properties pre = new Properties();
                Properties parms = new Properties();
                Properties header = new Properties();
                Properties files = new Properties();

                // Decode the header into parms and header java properties
                decodeHeader(hin, pre, parms, header);
                String method = pre.getProperty("method");
                String uri = pre.getProperty("uri");
                String version = pre.getProperty("version");

                long size = 0x7FFFFFFFFFFFFFFFl;
                String contentLength = header.getProperty("content-length");
                if (contentLength != null) {
                    try {
                        size = Integer.parseInt(contentLength);
                    } catch (NumberFormatException ex) {
                    }
                }

                // We are looking for the byte separating header from body.
                // It must be the last byte of the first two sequential new
                // lines.
                int splitbyte = 0;
                boolean sbfound = false;
                while (splitbyte < rlen) {
                    if (buf[splitbyte] == '\r' && buf[++splitbyte] == '\n'
                            && buf[++splitbyte] == '\r'
                            && buf[++splitbyte] == '\n') {
                        sbfound = true;
                        break;
                    }
                    splitbyte++;
                }
                splitbyte++;

                // Write the part of body already read to ByteArrayOutputStream
                // f
                ByteArrayOutputStream f = new ByteArrayOutputStream();
                if (splitbyte < rlen)
                    f.write(buf, splitbyte, rlen - splitbyte);

                // While Firefox sends on the first read all the data fitting
                // our buffer, Chrome and Opera sends only the headers even if
                // there is data for the body. So we do some magic here to find
                // out whether we have already consumed part of body, if we
                // have reached the end of the data to be sent or we should
                // expect the first byte of the body at the next read.
                if (splitbyte < rlen)
                    size -= rlen - splitbyte + 1;
                else if (!sbfound || size == 0x7FFFFFFFFFFFFFFFl)
                    size = 0;

                // Now read all the body and write it to f
                buf = new byte[512];
                while (rlen >= 0 && size > 0) {
                    rlen = is.read(buf, 0, 512);
                    size -= rlen;
                    if (rlen > 0)
                        f.write(buf, 0, rlen);
                }

                // Get the raw body as a byte []
                byte[] fbuf = f.toByteArray();

                // Create a BufferedReader for easily reading it as string.
                ByteArrayInputStream bin = new ByteArrayInputStream(fbuf);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        bin));

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (method.equalsIgnoreCase("POST")) {
                    String contentType = "";
                    String contentTypeHeader = header.getProperty("content-type");
                    
                    // Don't process header if not content-type is null
                    try {
                        StringTokenizer st = new StringTokenizer(contentTypeHeader, "; ");
                        if (st.hasMoreTokens())
                            contentType = st.nextToken();
    
                        if (contentType.equalsIgnoreCase("multipart/form-data")) {
                            // Handle multipart/form-data
                            if (!st.hasMoreTokens())
                                sendError("BAD REQUEST: Content type is multipart/form-data " +
                                          "but boundary missing. Usage: GET /example/file.html",
                                          HTTP_BADREQUEST);
                            String boundaryExp = st.nextToken();
                            st = new StringTokenizer(boundaryExp, "=");
                            if (st.countTokens() != 2)
                                sendError("BAD REQUEST: Content type is multipart/form-data but " +
                                        "boundary syntax error. Usage: GET /example/file.html", 
                                        HTTP_BADREQUEST);
                            st.nextToken();
                            String boundary = st.nextToken();
    
                            decodeMultipartData(boundary, fbuf, in, parms, files);
                        } else {
                            // Handle application/x-www-form-urlencoded
                            String postLine = "";
                            char pbuf[] = new char[512];
                            int read = in.read(pbuf);
                            while (read >= 0 && !postLine.endsWith("\r\n")) {
                                postLine += String.valueOf(pbuf, 0, read);
                                read = in.read(pbuf);
                            }
                            postLine = postLine.trim();
                            decodeParms(postLine, parms);
                        }
                    } catch (NullPointerException npe) {
                        // content-type was null and headers ignored
                    }
                    
                    
                }
                
                if (method.equalsIgnoreCase("PUT"))
                    files.put("content", saveTmpFile(fbuf, 0, f.size()));
                
                // Build request
                Request req = new Request(method, uri, version, parms, header, files);

                // Ok, now do the serve()
                Response res = serve(req);
                
                if (res != null) {
                    sendResponse(res);
                } else {
                    sendError("SERVER INTERNAL ERROR: " + 
                            "Serve() returned a null response.", 
                            HTTP_INTERNALERROR);                    
                }
                
                in.close();
                is.close();
            } catch (IOException ioe) {
                try {
                    sendError("SERVER INTERNAL ERROR: " + 
                            " IOException: " + ioe.getMessage(),
                            HTTP_INTERNALERROR);
                } catch (Throwable t) {
                }
            } catch (InterruptedException ie) {
                // Thrown by sendError, ignore and exit the thread.
            }
        }

        /**
         * Decodes the sent headers and loads the data into java Properties' key
         * - value pairs
         **/
        private void decodeHeader(BufferedReader in, Properties pre,
                Properties parms, Properties header)
                throws InterruptedException {
            try {
                // Read the request line
                String inLine = in.readLine();
                if (inLine == null)
                    return;
                StringTokenizer st = new StringTokenizer(inLine);
                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST,
                            "BAD REQUEST: Syntax error. Usage: GET /example/file.html");

                String method = st.nextToken();
                pre.put("method", method);

                if (!st.hasMoreTokens())
                    sendError(HTTP_BADREQUEST,
                            "BAD REQUEST: Missing URI. Usage: GET /example/file.html");

                String uri = st.nextToken();

                // Decode parameters from the URI
                int qmi = uri.indexOf('?');
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms);
                    uri = decodePercent(uri.substring(0, qmi));
                } else
                    uri = decodePercent(uri);

                // If there's another token, it's protocol version,
                // followed by HTTP headers. Ignore version but parse headers.
                // NOTE: this now forces header names lowercase since they are
                // case insensitive and vary by client.
                if (st.hasMoreTokens()) {
                	String version = st.nextToken();
                	pre.put("version", version);
                	
                    String line = in.readLine();
                    while (line != null && line.trim().length() > 0) {
                        int p = line.indexOf(':');
                        if (p >= 0) {
                            String varname = line.substring(0, p).trim().toLowerCase();
                            String value = line.substring(p + 1).trim();
                            header.put(varname, value);
                        }
                        line = in.readLine();
                    }
                }

                pre.put("uri", uri.trim());
            } catch (IOException ioe) {
                sendError(
                        HTTP_INTERNALERROR,
                        "SERVER INTERNAL ERROR: IOException: "
                                + ioe.getMessage());
            }
        }

        /**
         * Decodes the Multipart Body data and put it into java Properties' key
         * - value pairs.
         **/
        private void decodeMultipartData(String boundary, byte[] fbuf,
                BufferedReader in, Properties parms, Properties files)
                throws InterruptedException {
            try {
                int[] bpositions = getBoundaryPositions(fbuf,
                        boundary.getBytes());
                int boundarycount = 1;
                String mpline = in.readLine();
                while (mpline != null) {
                    if (mpline.indexOf(boundary) == -1)
                        sendError(
                                HTTP_BADREQUEST,
                                "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                    boundarycount++;
                    Properties item = new Properties();
                    mpline = in.readLine();
                    while (mpline != null && mpline.trim().length() > 0) {
                        int p = mpline.indexOf(':');
                        if (p != -1)
                            item.put(mpline.substring(0, p).trim()
                                    .toLowerCase(), mpline.substring(p + 1)
                                    .trim());
                        mpline = in.readLine();
                    }
                    if (mpline != null) {
                        String contentDisposition = item
                                .getProperty("content-disposition");
                        if (contentDisposition == null) {
                            sendError(
                                    HTTP_BADREQUEST,
                                    "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                        }
                        StringTokenizer st = new StringTokenizer(
                                contentDisposition, "; ");
                        Properties disposition = new Properties();
                        while (st.hasMoreTokens()) {
                            String token = st.nextToken();
                            int p = token.indexOf('=');
                            if (p != -1)
                                disposition.put(token.substring(0, p).trim()
                                        .toLowerCase(), token.substring(p + 1)
                                        .trim());
                        }
                        String pname = disposition.getProperty("name");
                        pname = pname.substring(1, pname.length() - 1);

                        String value = "";
                        if (item.getProperty("content-type") == null) {
                            while (mpline != null
                                    && mpline.indexOf(boundary) == -1) {
                                mpline = in.readLine();
                                if (mpline != null) {
                                    int d = mpline.indexOf(boundary);
                                    if (d == -1)
                                        value += mpline;
                                    else
                                        value += mpline.substring(0, d - 2);
                                }
                            }
                        } else {
                            if (boundarycount > bpositions.length)
                                sendError(HTTP_INTERNALERROR,
                                        "Error processing request");
                            int offset = stripMultipartHeaders(fbuf,
                                    bpositions[boundarycount - 2]);
                            String path = saveTmpFile(fbuf, offset,
                                    bpositions[boundarycount - 1] - offset - 4);
                            files.put(pname, path);
                            value = disposition.getProperty("filename");
                            value = value.substring(1, value.length() - 1);
                            do {
                                mpline = in.readLine();
                            } while (mpline != null
                                    && mpline.indexOf(boundary) == -1);
                        }
                        parms.put(pname, value);
                    }
                }
            } catch (IOException ioe) {
                sendError(
                        HTTP_INTERNALERROR,
                        "SERVER INTERNAL ERROR: IOException: "
                                + ioe.getMessage());
            }
        }

        /**
         * Find the byte positions where multipart boundaries start.
         **/
        public int[] getBoundaryPositions(byte[] b, byte[] boundary) {
            int matchcount = 0;
            int matchbyte = -1;
            Vector<Integer> matchbytes = new Vector<Integer>();
            for (int i = 0; i < b.length; i++) {
                if (b[i] == boundary[matchcount]) {
                    if (matchcount == 0)
                        matchbyte = i;
                    matchcount++;
                    if (matchcount == boundary.length) {
                        matchbytes.addElement(matchbyte);
                        matchcount = 0;
                        matchbyte = -1;
                    }
                } else {
                    i -= matchcount;
                    matchcount = 0;
                    matchbyte = -1;
                }
            }
            int[] ret = new int[matchbytes.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ((Integer) matchbytes.elementAt(i)).intValue();
            }
            return ret;
        }

        /**
         * Retrieves the content of a sent file and saves it to a temporary
         * file. The full path to the saved file is returned.
         **/
        private String saveTmpFile(byte[] b, int offset, int len) {
            String path = "";
            if (len > 0) {
                String tmpdir = System.getProperty("java.io.tmpdir");
                try {
                    File temp = File.createTempFile("NanoHTTPD", "", new File(
                            tmpdir));
                    OutputStream fstream = new FileOutputStream(temp);
                    fstream.write(b, offset, len);
                    fstream.close();
                    path = temp.getAbsolutePath();
                } catch (Exception e) { // Catch exception if any
                    System.err.println("Error: " + e.getMessage());
                }
            }
            return path;
        }

        /**
         * It returns the offset separating multipart file headers from the
         * file's data.
         **/
        private int stripMultipartHeaders(byte[] b, int offset) {
            int i = 0;
            for (i = offset; i < b.length; i++) {
                if (b[i] == '\r' && b[++i] == '\n' && b[++i] == '\r'
                        && b[++i] == '\n')
                    break;
            }
            return i + 1;
        }

        /**
         * Decodes the percent encoding scheme. <br/>
         * For example: "an+example%20string" -> "an example string"
         */
        private String decodePercent(String str) throws InterruptedException {
            try {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    switch (c) {
                    case '+':
                        sb.append(' ');
                        break;
                    case '%':
                        sb.append((char) Integer.parseInt(
                                str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;
                    default:
                        sb.append(c);
                        break;
                    }
                }
                return sb.toString();
            } catch (Exception e) {
                sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
                return null;
            }
        }

        /**
         * Decodes parameters in percent-encoded URI-format ( e.g.
         * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
         * Properties. NOTE: this doesn't support multiple identical keys due to
         * the simplicity of Properties -- if you need multiples, you might want
         * to replace the Properties with a Hashtable of Vectors or such.
         */
        private void decodeParms(String parms, Properties p)
                throws InterruptedException {
            if (parms == null)
                return;

            StringTokenizer st = new StringTokenizer(parms, "&");
            while (st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf('=');
                if (sep >= 0) {
                    String varname = decodePercent(e.substring(0, sep)).trim();
                    String value = decodePercent(e.substring(sep + 1));
                    putParam(p, varname, value);
                }
            }
        }

        /**
         * Checks a parameter if it's a number or string and puts it into the paramlist
         */
        private void putParam(Properties parms, String varname, String value) {
            try { 
                Double double_value = Double.parseDouble(value);
                parms.put(varname, double_value);
            } catch (NumberFormatException nfe) {};
            
            try {
                Integer int_value = Integer.parseInt(value);
                parms.put(varname, int_value);
            } catch (NumberFormatException nfe) {};
            
            if (!parms.containsKey(varname)) {
                parms.put(varname, value);
            }
        }
        
        /**
         * Returns an error message as a HTTP response and throws
         * InterruptedException to stop further request processing.
         */
        private void sendError(String msg, String status) throws InterruptedException {
            Response res = new Response(msg, status, MIME_PLAINTEXT);
            sendResponse(res);
            throw new InterruptedException();
        }

        /**
         * Sends given response to the socket.
         */
        private void sendResponse(Response res) {
            String status = res.status;
            String mime = res.mimeType;
            Properties header = res.header;
            InputStream data = res.data;
            
            try {
                if (status == null)
                    throw new Error("sendResponse(): Status can't be null.");

                OutputStream out = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(out);
                pw.print("HTTP/1.0 " + status + " \r\n");

                if (mime != null)
                    pw.print("Content-Type: " + mime + "\r\n");

                if (header == null || header.getProperty("Date") == null)
                    pw.print("Date: " + gmtFrmt.get().format(new Date()) + "\r\n");

                if (header != null) {
                    Enumeration<?> e = header.keys();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        String value = header.getProperty(key);
                        pw.print(key + ": " + value + "\r\n");
                    }
                }

                pw.print("\r\n");
                pw.flush();

                if (data != null) {
                    int pending = data.available(); // This is to support
                                                    // partial sends, see
                                                    // serveFile()
                    byte[] buff = new byte[bufferSize];
                    while (pending > 0) {
                        int read = data.read(buff, 0,
                                ((pending > bufferSize) ? bufferSize
                                        : pending));
                        if (read <= 0)
                            break;
                        out.write(buff, 0, read);
                        pending -= read;
                    }
                }
                out.flush();
                out.close();
                if (data != null)
                    data.close();
            } catch (IOException ioe) {
                // Couldn't write? No can do.
                try {
                    socket.close();
                } catch (Throwable t) {}
            }
        }
    }

    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
     * instead of '+'.
     */
    public static String encodeUri(String uri) {
        String newUri = "";
        StringTokenizer st = new StringTokenizer(uri, "/ ", true);
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals("/"))
                newUri += "/";
            else if (tok.equals(" "))
                newUri += "%20";
            else {
            	try {
            		newUri += URLEncoder.encode(tok, "UTF-8"); 
            	} catch (UnsupportedEncodingException e) {}
            }
        }
        return newUri;
    }
    
    // ==================================================
    // File server code
    // ==================================================

    /**
     * Serves file from a Storage implementation. Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    public static Response serveFile(Request req, Storage storage, boolean allowDirectoryListing) {
        
        String url = req.uri;
        String path = req.path;
        
        // Get file path
        path = path.replace(File.separatorChar, '/');
        if (path.contains("?"))
            path = path.substring(0, path.indexOf('?'));
        
        // Prohibit getting out of current directory
        if (path.contains("../")) {
            return new Response(
                    "FORBIDDEN: Won't serve ../ for security reasons.",
                    HTTP_FORBIDDEN, MIME_PLAINTEXT);
        }

        if (!storage.exists(path))
            return new Response(
                    "Error 404, file not found.",
                    HTTP_NOTFOUND, MIME_PLAINTEXT);

        // List the directory, if necessary
        if (storage.isDirectory(path)) {
            // Browsers get confused without '/' after the
            // directory, send a redirect.
            if (!url.endsWith("/")) {
                return Response.redirect(url + "/");
            }

            // First try index.html and index.htm
            if (storage.exists(path+"index.html"))
                return deliverFile(req, storage, path+"index.html");
            else if (storage.exists(path+"index.htm"))
                return deliverFile(req, storage, path+"index.htm");
            // No index file, list the directory if it is readable
            else if (allowDirectoryListing) {
                String[] files = storage.list(path);
                String msg = "<html><body><h1>Directory " + path
                        + "</h1><br/>";

                if (files != null) {
                    for (int i = 0; i < files.length; ++i) {
                        boolean dir = storage.isDirectory(url+files[i]);
                        if (dir) { 
                            msg += "<b>"; 
                            files[i] += "/"; 
                        }

                        msg += "<a href=\"" + encodeUri(files[i])
                                + "\">" + files[i] + "</a>";
                        
                        msg += "<br/>";
                        if (dir) { msg += "</b>"; }
                    }
                }
                
                msg += "</body></html>";
                return new Response(msg, HTTP_OK, MIME_HTML);
            } else {
                return new Response("FORBIDDEN: No directory listing.", HTTP_FORBIDDEN, MIME_PLAINTEXT);
            }
        }
        
        return deliverFile(req, storage, path);

    }
    
    public static Response deliverFile(Request req, Storage storage, String path) {       
        // Get MIME type from file name extension, if possible
        String mime = null;
        int dot = path.lastIndexOf('.');
        if (dot >= 0)
            mime = (String) mimeTypes.get(path
                    .substring(dot + 1).toLowerCase());
        if (mime == null)
            mime = MIME_DEFAULT_BINARY;

        // Calculate last modified
        Date modified = new Date(storage.lastModified(path));
        
        // Calculate etag
        String etag = Integer.toHexString((path
                + storage.lastModified(path) + "" + storage.length(path)).hashCode());
        
        //if (gmtFrmt.format(modified).equals(req.header.getProperty("if-modified-since")))
        //    return new Response("", HTTP_NOTMODIFIED, mime);
        
        if (etag.equals(req.header.getProperty("if-none-match")))
            return new Response("", HTTP_NOTMODIFIED, mime);
            
        try {
                Response res = new Response(storage.open(path), HTTP_OK, mime);
                res.addHeader("Content-Length", "" + storage.length(path));
                res.addHeader("Last-Modified", gmtFrmt.get().format(modified));
                res.addHeader("ETag", etag);
                return res;
        } catch (FileNotFoundException fnfe) {
                return new Response("INTERNAL ERROR: Couldn't open file " + path,
                    HTTP_INTERNALERROR, MIME_PLAINTEXT);
        }
    }

}
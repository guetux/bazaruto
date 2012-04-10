package ch.bazaruto;

import java.util.Properties;

public class Request {

	/*
	 * The request uri representing 
	 */
	public String uri;
	
	/*
	 * The HTTP method, one of ["GET", "POST", "PUT", "DELETE"]
	 */
	public String method;
	
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
	 * 
	 */
	public Request(String uri, String method, Properties header, Properties parms, Properties files) {
		this.uri = uri;
		this.method = method;
		this.header = header;
		this.parms = parms;
		this.files = files;
	}
}

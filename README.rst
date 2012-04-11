
The Bazaruto Framework
======================

Bazaruto is a very, very simple and stupid way of creating 
Webapps in Java. It's intended to run on the most basic hardware
and in any Java environment. It uses a NanoHTTPD Server, but in
a slightly rewritten fashion. It follows the MVC pattern. Bla
bla bla....

A Controller is written like this

::

	@Route("/books")
	public class BooksController {
		@GET("/")
		public Response index(Request req) {
			return new Response("List books...");
		}
		
		@GET("/details/(\\d+)/")
		public Response detail(Request req, int id) {
			return new Response("Detail of book id: "+id);
		}
		
		@POST("/update")
		public Response timestwo(Request req) {
			Double price = (Double)req.parms.get("price");
			return new Response("New book price: " + price);
		}
	}
	
Starting the server is as easy as:

::

	Bazaruto myapp = new Bazaruto();
	myapp.addController(BooksController.class);
	myapp.startServer();
	

	
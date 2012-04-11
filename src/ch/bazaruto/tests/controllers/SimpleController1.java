package ch.bazaruto.tests.controllers;

import ch.bazaruto.Bazaruto.POST;
import ch.bazaruto.Request;
import ch.bazaruto.Response;
import ch.bazaruto.Bazaruto.GET;
import ch.bazaruto.Bazaruto.Route;

@Route("/sc1")
public class SimpleController1 {
	@GET("/")
	public Response index(Request req) {
		return new Response("SC1:index");
	}
	
	@GET("/details")
	public Response detail(Request req) {
		int id = (Integer)req.parms.get("id");
		return new Response("SC1:detail:"+id);
	}
	
	@POST("/timestwo")
	public Response timestwo(Request req) {
		int number = (Integer)req.parms.get("number");
		return new Response("SC1:timestwo:"+number*2);
	}
}
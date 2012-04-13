package ch.bazaruto.tests.controllers;

import ch.bazaruto.Bazaruto.DELETE;
import ch.bazaruto.Bazaruto.POST;
import ch.bazaruto.Bazaruto.PUT;
import ch.bazaruto.Request;
import ch.bazaruto.Response;
import ch.bazaruto.Bazaruto.GET;
import ch.bazaruto.Bazaruto.Route;

@Route("/sc1")
public class SimpleController1 {
    @GET("/")
    public Response index(Request req) {
        return new Response("SC1:get");
    }
    
    @GET("/intarg/(\\d)/")
    public Response intarg(Request req, int i) {
        return new Response("SC1:intarg:" + i);
    }
    
    @GET("/floatarg/(\\d\\.\\d)/")
    public Response floatarg(Request req, double f) {
        return new Response("SC1:floatarg:"+f);
    }
    
    @GET("/stringarg/(\\w+)")
    public Response stringarg(Request req, String str) {
        return new Response("SC1:stringarg:"+ str);
    }
    
    @POST("/postarg")
    public Response postarg(Request req) {
        return new Response("SC1:postarg:"+req.parms.get("number"));
    }
    
    @PUT("/put")
    public Response put(Request req) {
        return new Response("SC1:put");
    }
    
    @DELETE("/delete")
    public Response delete(Request req) {
        return new Response("SC1:delete");
    }

}
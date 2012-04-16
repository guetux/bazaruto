package ch.bazaruto.example;

import ch.bazaruto.Request;
import ch.bazaruto.Response;
import ch.bazaruto.Bazaruto.GET;
import ch.bazaruto.Bazaruto.Route;

@Route("^$")
public class Redirector {
    @GET("^/$")
    public Response redirect(Request req) {
        return Response.redirect("/chat/");
    }
}
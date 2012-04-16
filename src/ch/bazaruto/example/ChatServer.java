package ch.bazaruto.example;

import java.io.File;
import java.util.LinkedList;
import java.util.Properties;

import ch.bazaruto.Bazaruto;
import ch.bazaruto.Bazaruto.GET;
import ch.bazaruto.Bazaruto.POST;
import ch.bazaruto.Bazaruto.Route;
import ch.bazaruto.NanoHTTPD;
import ch.bazaruto.Request;
import ch.bazaruto.Response;
import ch.bazaruto.templates.Template;

@Route("/chat")
public class ChatServer {
    
    public static 
    
    class Message {
        public String username;
        public String message;
    }
    
    public static LinkedList<Message> messages = new LinkedList<Message>();

    @GET("/")
    public Response redirect(Request req) {
        return Response.redirect("/chat/user/");
    }
    
    @GET("/user/$")
    public Response username(Request req) {
        Template t = new Template("example/user_form.html");
        return new Response(t.render());
    }
    
    @POST("/user/$")
    public Response username_redirect(Request req) {
        try {
            String username = req.parms.getProperty("username");
            return Response.redirect("/chat/user/"+username+"/");
        } catch (Exception e) {
            return new Response("Parameter fehler");
        }
        
    }
    
    @GET("/user/(\\w+)/$")
    public Response chat_form(Request req, String username) {
        Properties context = new Properties();
        context.put("username", username);
        Template t = new Template("example/chat_form.html", context);
        return new Response(t.render());
    }
    
    @GET("/messages/$")
    public Response messages(Request req) {
        StringBuilder s = new StringBuilder();
        s.append("[");
        for(Message m : messages) {
            s.append("{\"username\":\""+m.username+"\"," +
            		"\"message\":\""+m.message+"\"}");
            if (messages.getLast() != m)
                s.append(",");
        }
        s.append("]");
        return new Response(s.toString(), NanoHTTPD.HTTP_OK, "application/json");
    }
    
    @POST("/messages/$")
    public Response post(Request req) {
        Message m = new Message();
        m.username = req.parms.getProperty("username");
        m.message = req.parms.getProperty("message");
        messages.add(m);
        return messages(req);
    }
    
    public static void main(String[] args) {
        Bazaruto server = new Bazaruto();
        server.addController(Redirector.class);
        server.addController(ChatServer.class);
        server.addStaticPath("/static/", new File("static"));
        server.startServer();
        try { System.in.read(); } catch( Throwable t ) {}
    }
}

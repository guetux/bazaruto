package ch.bazaruto;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

// This is not a typedef!
class ControllerMap extends HashMap<String, Class> {
	private static final long serialVersionUID = 1L;
}

/* The main class to create Bazaruto apps */
@SuppressWarnings("rawtypes") 
public class Bazaruto extends NanoHTTPD {

	/* Annotation for Controller routes */
	public @Retention(RetentionPolicy.RUNTIME) @interface Route {
		String value();
	}
	
	/* Annotation for GET Methods */
	public @Retention(RetentionPolicy.RUNTIME) @interface GET {
		String value();
	}
	
	/* Annotation for GET Methods */
	public @Retention(RetentionPolicy.RUNTIME) @interface POST {
		String value();
	}	
	
	private ControllerMap controllers = new ControllerMap();
	
	public void addController(Class controller) {
		@SuppressWarnings("unchecked")
		Annotation annotation = controller.getAnnotation(Route.class);
    	if(annotation instanceof Route){
    		Route route = (Route)annotation;
    		controllers.put(route.value(), controller);
    	}
	}
	
	public Response dispatch(Request req) {
		for (Map.Entry<String, Class> entry: controllers.entrySet()) {
			if (req.uri.startsWith(entry.getKey())) {
				String path = req.uri.replaceFirst(entry.getKey(), "");
				return dispatchToMethod(req, entry.getValue(), path);
			}
		}
		
		return new Response("Page not found: No Controller registered to this uri",
				NanoHTTPD.HTTP_NOTFOUND);
	}
	
	private Response dispatchToMethod(Request req, Class controller, String path) {
		Method methods[] = controller.getDeclaredMethods();
		
		
        for (Method method : methods) {
        	// Dispatch GET
        	Annotation get_annotation = method.getAnnotation(GET.class);
        	if(get_annotation instanceof GET && req.method.equalsIgnoreCase("GET")){
        	    GET get = (GET)get_annotation;
        	    String request_path = get.value();
        	    if (request_path.startsWith(path)) {
        	    	return executeRequest(req, controller, method);
        	    }
        	}
        	
        	// Dispatch POST
        	Annotation post_annotation = method.getAnnotation(POST.class);
        	if(post_annotation instanceof POST && req.method.equalsIgnoreCase("POST")){
        	    POST post = (POST)post_annotation;
        	    String request_path = post.value();
        	    if (request_path.startsWith(path)) {
        	    	return executeRequest(req, controller, method);
        	    }
        	}
        }
        
        return new Response("Page not found: Controller registered no corresponding method",
				NanoHTTPD.HTTP_NOTFOUND);
	}
	
	private Response executeRequest(Request req, Class controller, Method method) {
		try {
			Object instance = controller.newInstance();
			Response res = (Response)method.invoke(instance, req);
			return res;
			
		// TODO: Better error handling here would be helpfull
		} catch (InstantiationException e) {
			e.printStackTrace();
			return new Response("Error: Could not instantiate controller " + 
					controller.getName() + ". ?" +
					e.getMessage(),
					NanoHTTPD.HTTP_INTERNALERROR);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return new Response("Error: Could not instantiate controller "
					+ controller.getName() + ". " + e.getMessage(),
					NanoHTTPD.HTTP_INTERNALERROR);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return new Response("Error: Could not invoke method: Illegal argument\n" +
					e.getMessage(),
					NanoHTTPD.HTTP_INTERNALERROR);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return new Response("Error: Could not invoke method: Target exception\n" +
					e.getMessage(),
					NanoHTTPD.HTTP_INTERNALERROR);
		} catch (SecurityException e) {
			e.printStackTrace();
			return new Response("Error: Could not instantiate controller: Security Exception\n" + 
					e.getMessage(),
					NanoHTTPD.HTTP_INTERNALERROR);
		}
	}
	
	@Override
	public Response serve(Request req) {
		return this.dispatch(req);
	}
}

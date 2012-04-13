package ch.bazaruto.templates;

import java.io.InputStream;

public class StreamTemplateLoader implements TemplateLoader {
 
    public String loadTemplate(Object inputstream) {
        return readStream((InputStream)inputstream);
    }
    
    public String readStream(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

}

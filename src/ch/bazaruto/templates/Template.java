package ch.bazaruto.templates;

import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;


/*
 * Absolut minimalistic template approach
 */
public class Template {
    
    public static TemplateLoader default_loader = new FileTemplateLoader();
    public TemplateLoader loader;
    
    public String template;
    public Properties context;

    public Template() {
        loader = default_loader;
    }
    
    public Template(Object template) {
        loader = default_loader;
        this.template = loader.loadTemplate(template);
        this.context = new Properties();
    }
    
    public Template(Object template, Properties context) {
        loader = default_loader;
        this.template = loader.loadTemplate(template);
        this.context = context;
    }
    
    public void loadTemplate(Object template) {
        this.template = loader.loadTemplate(template);
    }

    public String render() {
        String result = this.template;

        for (Enumeration e = this.context.propertyNames(); e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value = this.context.getProperty(key);
            result = result.replaceAll("@" + key, value);
        }
            
        // Remove all @string notices
        result = result.replaceAll("@\\w+", "");

        return result;
    }
}
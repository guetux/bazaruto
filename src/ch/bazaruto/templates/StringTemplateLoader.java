package ch.bazaruto.templates;

public class StringTemplateLoader implements TemplateLoader {

    public String loadTemplate(Object template) {
        return (String)template;
    }
}

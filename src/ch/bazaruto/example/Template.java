package ch.bazaruto.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*
 * Absolut minimalistic template approach
 */
public class Template {

    public String template;
    public Properties context;

    public Template(String template) {
        this.template = template;
        this.context = new Properties();
    }
    
    public Template(File file) {
        this.template = readFile(file);
        this.context = new Properties();
    }

    public Template(String template, Properties context) {
        this.template = template;
        this.context = context;
    }

    public Template(File file, Properties context) {
        this.template = readFile(file);
        this.context = context;
    }
    
    public String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                result.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return result.toString();
    }

    public String render() {
        String result = this.template;

        for (String key : this.context.stringPropertyNames()) {
            String property = this.context.getProperty(key);
            result = result.replaceAll("@" + key, property);
        }
        
        // Remove all @string notices
        result = result.replaceAll("@\\w+", "");

        return result;
    }
}
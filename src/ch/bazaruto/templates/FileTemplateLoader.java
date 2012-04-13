package ch.bazaruto.templates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileTemplateLoader implements TemplateLoader {

    public String loadTemplate(Object filepath) {
        return readFile(new File("static/"+filepath));
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
}

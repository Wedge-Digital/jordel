package com.bloodbowlclub.lib.services.email_service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TemplateEngine {

    public String processTemplate(String templateContent, Map<String, Object> variables) {
        if (templateContent == null || variables == null) {
            return templateContent;
        }

        String result = templateContent;
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(result);

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            Object value = variables.get(placeholder);
            if (value != null) {
                result = result.replace("{{" + placeholder + "}}", value.toString());
            }
        }

        return result;
    }

    public String processTemplateFromFile(String templateName, Map<String, Object> variables) {
        String templateContent = null;
        try {
            ClassPathResource resource = new ClassPathResource("email_template/" + templateName);
            if (resource.exists()) {
                templateContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            }
        } catch (IOException e) {
            // Fall through to default content
        }

        if (templateContent == null) {
            templateContent = "<html><body><h1>Hello {{name}}</h1><p>Your order {{username}} is {{var_1}}.</p></body></html>";
        }

        return processTemplate(templateContent, variables);
    }
}

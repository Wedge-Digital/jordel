package com.bloodbowlclub.lib.services.email_service;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // In a real implementation, this would read from file system or database
        // For now, returning a simple template based on name
        String templateContent = "<html><body><h1>Hello {{name}}</h1><p>Your order {{order_id}} is {{status}}.</p></body></html>";
        return processTemplate(templateContent, variables);
    }
}

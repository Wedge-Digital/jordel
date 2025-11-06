package com.bloodbowlclub.lib.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Simple controller to redirect root paths to the Swagger UI page
 * configured via springdoc.
 */
@Controller
public class SwaggerUiRedirectController {

    // Default landing page -> Swagger UI
    @GetMapping({"/", "/swagger", "/docs", "/api", "/openapi"})
    public String index() {
        // The path is configured in application.yml under springdoc.swagger-ui.path
        // Keep it in sync if you change that value.
        return "redirect:/swagger-ui-custom.html";
    }
}

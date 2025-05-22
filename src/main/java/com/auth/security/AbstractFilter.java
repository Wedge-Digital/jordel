package com.auth.security;

import com.td.auth.services.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

public abstract class AbstractFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String[] routeToBypass = getBypassRoutes();
        Result<String> requestPathAnalysis = ServletPathService.getServletPath(request);
        if (!requestPathAnalysis.isSuccess()) {
            throw new ServletException("Impossible to determine servlet path");
        }
        String servletTarget = requestPathAnalysis.getValue();
        return Arrays.stream(routeToBypass).anyMatch(e -> new AntPathMatcher().match(e, servletTarget));
    }

    protected abstract String[] getBypassRoutes();
}

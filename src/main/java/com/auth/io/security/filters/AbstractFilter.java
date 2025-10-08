package com.auth.io.security.filters;

import com.shared.services.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AbstractFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        if (Objects.equals(request.getMethod(), "OPTIONS")) {
            logger.info("filtering skipped for OPTIONS request");
            return true;
        }

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

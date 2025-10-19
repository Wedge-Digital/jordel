package com.lib.auth.filters;

import com.auth.io.security.filters.ServletPathService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceFilter extends OncePerRequestFilter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info("=========== LOG FILTER ==========");
        logger.trace("doFilterInternal");
        logger.info(request.getRequestURI());
        logger.info(ServletPathService.getServletPath(request).getValue());
        filterChain.doFilter(request, response);
        logger.info("=========== LOG FILTER EXIT ==========");
    }
}

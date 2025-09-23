package com.auth.security;

import com.auth.services.errors.NotFoundError;
import com.auth.services.Result;
import jakarta.servlet.http.HttpServletRequest;

public class ServletPathService {
    public static Result<String> getServletPath(HttpServletRequest request) {
        String requestServletPath = request.getServletPath();
        String requestUri = request.getRequestURI();
        if (requestServletPath != null && !requestServletPath.isEmpty()) {
            return Result.success(requestServletPath);
        }
        if (requestUri != null && !requestUri.isEmpty()) {
            return Result.success(requestUri);
        }

        return Result.failure(new NotFoundError("impossible to get servlet target"));
    }
}

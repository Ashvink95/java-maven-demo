package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Guards protected endpoints (registered for /api/todos/**). Requires a valid
 * "Authorization: Bearer <token>" header; otherwise responds 401 before the
 * controller runs. CORS preflight (OPTIONS) requests are allowed through.
 */
@Component
public class TokenAuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public TokenAuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        String token = (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;

        if (tokenService.validate(token).isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Unauthorized\"}");
            return false;
        }
        return true;
    }
}

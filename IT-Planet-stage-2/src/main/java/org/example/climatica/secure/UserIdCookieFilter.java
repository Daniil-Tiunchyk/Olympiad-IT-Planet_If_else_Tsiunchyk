package org.example.climatica.secure;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class UserIdCookieFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();

        if (path.startsWith("/login") || path.startsWith("/registration") || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (request.getCookies() == null || Arrays.stream(request.getCookies())
                .noneMatch(cookie -> "userId".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: No valid userId cookie provided");
            return;
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
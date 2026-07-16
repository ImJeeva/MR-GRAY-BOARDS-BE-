package com.invoice.invoice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpaFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();
        String contextPath = req.getContextPath();
        String relativePath = path.substring(contextPath.length());

        boolean isApi = relativePath.startsWith("/api");
        boolean isAsset = relativePath.startsWith("/assets/") || relativePath.startsWith("/favicon") || relativePath.startsWith("/icons");
        boolean hasExtension = relativePath.contains(".");

        if (!isApi && !isAsset && !hasExtension && !relativePath.equals("/")) {
            req.getRequestDispatcher("/index.html").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}

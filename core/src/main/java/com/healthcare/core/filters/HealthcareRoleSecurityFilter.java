package com.healthcare.core.filters;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;

import javax.servlet.*;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Enforces strict role isolation between /content/healthcare/doctor/* and /content/healthcare/patient/*
 */
@Component(
    service = Filter.class,
    property = {
        "sling.filter.scope=REQUEST",
        "service.ranking:Integer=700"
    }
)
public class HealthcareRoleSecurityFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SlingHttpServletRequest slingReq = (SlingHttpServletRequest) request;
        SlingHttpServletResponse slingRes = (SlingHttpServletResponse) response;

        String uri = slingReq.getRequestURI();
        HttpSession session = slingReq.getSession(false);
        String userRole = session != null ? (String) session.getAttribute("HEALTHCARE_USER_ROLE") : null;

        // Strict Doctor Area Protection: Patient cannot access /doctor/*
        if (uri.startsWith("/doctor") || uri.contains("/content/healthcare/doctor")) {
            if (!"doctor".equalsIgnoreCase(userRole)) {
                slingRes.sendError(SlingHttpServletResponse.SC_FORBIDDEN, "Access Denied: Doctor credentials required.");
                return;
            }
        }

        // Strict Patient Area Protection: Doctor cannot access private patient dashboard
        if (uri.startsWith("/patient") || uri.contains("/content/healthcare/patient")) {
            if (!"patient".equalsIgnoreCase(userRole)) {
                slingRes.sendError(SlingHttpServletResponse.SC_FORBIDDEN, "Access Denied: Patient credentials required.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}

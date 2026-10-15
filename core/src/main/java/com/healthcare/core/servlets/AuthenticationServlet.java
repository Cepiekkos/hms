package com.healthcare.core.servlets;

import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/healthcare/auth-login",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST
    }
)
public class AuthenticationServlet extends SlingAllMethodsServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Map<String, Object> payload = gson.fromJson(sb.toString(), Map.class);
        String role = payload.get("role") != null ? payload.get("role").toString() : "patient";
        String username = payload.get("username") != null ? payload.get("username").toString() : "";

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("authenticated", true);
        result.put("role", role);

        if ("doctor".equalsIgnoreCase(role)) {
            result.put("redirectUrl", "/portal/doctor");
            result.put("user", Map.of(
                "name", "Dr. Sarah Jenkins, MD",
                "id", "DOC-001",
                "department", "Cardiology & Vascular Medicine",
                "hospital", "Metropolitan Health Center"
            ));
        } else {
            result.put("redirectUrl", "/portal/patient");
            result.put("user", Map.of(
                "name", "Eleanor Vance",
                "id", "PAT-90821",
                "dob", "1988-04-12",
                "bloodGroup", "O+",
                "primaryDoctor", "Dr. Sarah Jenkins, MD"
            ));
        }

        response.getWriter().write(gson.toJson(result));
    }
}

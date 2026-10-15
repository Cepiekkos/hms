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
import java.util.UUID;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/healthcare/doctor-action",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST
    }
)
public class DoctorActionServlet extends SlingAllMethodsServlet {

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
        String action = payload.get("action") != null ? payload.get("action").toString() : "unknown";

        Map<String, Object> res = new HashMap<>();
        res.put("status", "SUCCESS");
        res.put("action", action);
        res.put("timestamp", System.currentTimeMillis());

        if ("e-prescribe".equalsIgnoreCase(action)) {
            res.put("rxNumber", "RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            res.put("message", "Prescription signed by Dr. Jenkins and sent to pharmacy.");
        } else if ("save-notes".equalsIgnoreCase(action)) {
            res.put("encounterId", "ENC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            res.put("message", "Clinical encounter assessment appended to patient FHIR record.");
        } else if ("reply-message".equalsIgnoreCase(action)) {
            res.put("messageId", "REPLY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            res.put("message", "Secure reply transmitted to patient.");
        }

        response.getWriter().write(gson.toJson(res));
    }
}

package com.healthcare.core.servlets;

import com.google.gson.Gson;
import com.healthcare.core.services.FhirIntegrationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/healthcare/book-appointment",
        "sling.servlet.methods=" + HttpConstants.METHOD_POST
    }
)
public class AppointmentBookingServlet extends SlingAllMethodsServlet {

    @Reference
    private FhirIntegrationService fhirService;

    private final Gson gson = new Gson();

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Map<String, Object> payload;
            if (sb.length() > 0) {
                payload = gson.fromJson(sb.toString(), Map.class);
            } else {
                // Fallback to request parameters
                payload = Map.of(
                    "patientName", request.getParameter("patientName") != null ? request.getParameter("patientName") : "Anonymous",
                    "patientEmail", request.getParameter("patientEmail") != null ? request.getParameter("patientEmail") : "",
                    "doctorId", request.getParameter("doctorId") != null ? request.getParameter("doctorId") : "DOC-001",
                    "department", request.getParameter("department") != null ? request.getParameter("department") : "General",
                    "date", request.getParameter("date") != null ? request.getParameter("date") : "2026-10-15",
                    "timeSlot", request.getParameter("timeSlot") != null ? request.getParameter("timeSlot") : "10:00 AM"
                );
            }

            Map<String, Object> result = fhirService.bookAppointment(payload);
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(result));

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}

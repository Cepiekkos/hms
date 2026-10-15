package com.healthcare.core.servlets;

import com.google.gson.Gson;
import com.healthcare.core.services.FhirIntegrationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/healthcare/patient-records",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET
    }
)
public class PatientRecordServlet extends SlingSafeMethodsServlet {

    @Reference
    private FhirIntegrationService fhirService;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String patientId = request.getParameter("patientId");
        if (patientId == null || patientId.isBlank()) {
            patientId = "PAT-90821";
        }

        Map<String, Object> record = fhirService.getPatientRecord(patientId);
        response.setStatus(SlingHttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(record));
    }
}

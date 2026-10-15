package com.healthcare.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.paths=/bin/healthcare/export-ehr-pdf",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET
    }
)
public class ExportEhrPdfServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String testName = request.getParameter("test");
        if (testName == null || testName.isBlank()) {
            testName = "Comprehensive Metabolic Panel";
        }

        String patientId = request.getParameter("patientId");
        if (patientId == null || patientId.isBlank()) {
            patientId = "PAT-90821";
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"EHR_DiagnosticReport_" + patientId + ".pdf\"");

        // Minimal binary PDF stream representation
        String pdfContent = "%PDF-1.4\n1 0 obj\n<< /Title (EHR Diagnostic Report) /Author (AuraHealth AEM) >>\nendobj\n%%EOF";
        response.getOutputStream().write(pdfContent.getBytes());
    }
}

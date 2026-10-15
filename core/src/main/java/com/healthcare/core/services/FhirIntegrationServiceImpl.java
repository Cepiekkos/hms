package com.healthcare.core.services;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = FhirIntegrationService.class, immediate = true)
@Designate(ocd = FhirIntegrationServiceImpl.Config.class)
public class FhirIntegrationServiceImpl implements FhirIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(FhirIntegrationServiceImpl.class);

    @ObjectClassDefinition(name = "Healthcare FHIR / EHR Integration Service Configuration")
    public @interface Config {
        @AttributeDefinition(name = "FHIR Base URL", description = "Base URL for HL7 FHIR RESTful server")
        String fhir_base_url() default "https://fhir.example-health.org/r4";

        @AttributeDefinition(name = "FHIR API Key / Token", description = "Bearer auth token for EHR integration")
        String fhir_api_key() default "DEMO_FHIR_API_KEY_SECURE";

        @AttributeDefinition(name = "Mock Mode Enabled", description = "Enable mock EHR responses for local AEM development")
        boolean mock_mode() default true;
    }

    private Config config;

    @Activate
    protected void activate(Config config) {
        this.config = config;
        LOG.info("FhirIntegrationServiceImpl activated with Base URL: {}", config.fhir_base_url());
    }

    @Override
    public Map<String, Object> getPatientRecord(String patientId) {
        LOG.info("Fetching patient record for ID: {}", patientId);
        Map<String, Object> record = new HashMap<>();
        record.put("patientId", patientId);
        record.put("fullName", "Eleanor Vance");
        record.put("dob", "1988-04-12");
        record.put("gender", "Female");
        record.put("bloodGroup", "O+");
        record.put("primaryPhysician", "Dr. Sarah Jenkins (Cardiology)");

        List<Map<String, String>> vitals = new ArrayList<>();
        vitals.add(Map.of("type", "Blood Pressure", "value", "120/78 mmHg", "status", "Normal"));
        vitals.add(Map.of("type", "Heart Rate", "value", "72 bpm", "status", "Normal"));
        vitals.add(Map.of("type", "Oxygen Saturation (SpO2)", "value", "99%", "status", "Optimal"));
        vitals.add(Map.of("type", "Glucose (Fasting)", "value", "92 mg/dL", "status", "Normal"));
        record.put("vitals", vitals);

        List<Map<String, String>> labReports = new ArrayList<>();
        labReports.add(Map.of("test", "Comprehensive Metabolic Panel", "date", "2026-09-15", "result", "Normal", "downloadUrl", "#"));
        labReports.add(Map.of("test", "Lipid Panel", "date", "2026-08-01", "result", "Desirable", "downloadUrl", "#"));
        labReports.add(Map.of("test", "HbA1c Glycated Hemoglobin", "date", "2026-05-10", "result", "5.4% (Normal)", "downloadUrl", "#"));
        record.put("labReports", labReports);

        return record;
    }

    @Override
    public List<Map<String, Object>> getPractitioners(String department) {
        LOG.info("Retrieving practitioners with department filter: {}", department);
        List<Map<String, Object>> doctors = new ArrayList<>();

        doctors.add(Map.of(
            "id", "DOC-001",
            "name", "Dr. Sarah Jenkins, MD",
            "department", "Cardiology",
            "experience", "15 years",
            "rating", "4.9 / 5.0",
            "availability", "Mon, Wed, Fri (09:00 - 15:00)"
        ));

        doctors.add(Map.of(
            "id", "DOC-002",
            "name", "Dr. Marcus Chen, DO",
            "department", "Neurology",
            "experience", "12 years",
            "rating", "4.8 / 5.0",
            "availability", "Tue, Thu, Sat (10:00 - 16:00)"
        ));

        doctors.add(Map.of(
            "id", "DOC-003",
            "name", "Dr. Aliyah Patel, MD",
            "department", "Pediatrics",
            "experience", "9 years",
            "rating", "5.0 / 5.0",
            "availability", "Mon through Fri (08:30 - 14:00)"
        ));

        doctors.add(Map.of(
            "id", "DOC-004",
            "name", "Dr. Robert Rodriguez, MD",
            "department", "Orthopedics & Sports Medicine",
            "experience", "18 years",
            "rating", "4.9 / 5.0",
            "availability", "Wed, Thu, Fri (11:00 - 17:00)"
        ));

        if (department != null && !department.isBlank()) {
            return doctors.stream()
                .filter(d -> d.get("department").toString().toLowerCase().contains(department.toLowerCase()))
                .toList();
        }

        return doctors;
    }

    @Override
    public Map<String, Object> bookAppointment(Map<String, Object> appointmentData) {
        LOG.info("Registering appointment with EHR: {}", appointmentData);
        Map<String, Object> response = new HashMap<>();
        String confirmationCode = "APT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        response.put("status", "CONFIRMED");
        response.put("confirmationCode", confirmationCode);
        response.put("details", appointmentData);
        response.put("timestamp", new Date().toString());
        return response;
    }

    @Override
    public boolean submitConsultationRequest(Map<String, Object> consultationData) {
        LOG.info("Dispatching secure consultation inquiry: {}", consultationData);
        return true;
    }
}

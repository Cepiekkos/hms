package com.healthcare.core.models;

import com.healthcare.core.services.FhirIntegrationService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DoctorPortalModel {

    @ValueMapValue
    private String doctorId;

    @OSGiService
    private FhirIntegrationService fhirService;

    private Map<String, Object> doctorProfile;
    private List<Map<String, Object>> scheduledPatients;
    private List<Map<String, Object>> patientMessages;

    @PostConstruct
    protected void init() {
        if (doctorId == null || doctorId.isBlank()) {
            doctorId = "DOC-001"; // Dr. Sarah Jenkins
        }

        doctorProfile = new HashMap<>();
        doctorProfile.put("id", doctorId);
        doctorProfile.put("name", "Dr. Sarah Jenkins, MD");
        doctorProfile.put("department", "Cardiology");
        doctorProfile.put("hospital", "Metropolitan Health Medical Center");
        doctorProfile.put("license", "MD-NY-894102");

        // Scheduled patient consultations
        scheduledPatients = new ArrayList<>();
        scheduledPatients.add(Map.of(
            "appointmentId", "APT-88219",
            "patientName", "Eleanor Vance",
            "patientId", "PAT-90821",
            "age", 38,
            "time", "09:00 AM",
            "type", "Follow-up (Hypertension & Lipid Review)",
            "status", "Checked In",
            "vitalsSummary", "BP: 120/78 | HR: 72 bpm"
        ));
        scheduledPatients.add(Map.of(
            "appointmentId", "APT-88220",
            "patientName", "Liam Montgomery",
            "patientId", "PAT-90822",
            "age", 52,
            "time", "10:30 AM",
            "type", "Telemetry Arrhythmia Evaluation",
            "status", "Confirmed",
            "vitalsSummary", "BP: 135/88 | HR: 86 bpm"
        ));
        scheduledPatients.add(Map.of(
            "appointmentId", "APT-88221",
            "patientName", "Grace Thorne",
            "patientId", "PAT-90823",
            "age", 29,
            "time", "02:00 PM",
            "type", "Post-op Echocardiogram Review",
            "status", "Confirmed",
            "vitalsSummary", "BP: 118/74 | HR: 68 bpm"
        ));

        // Incoming direct messages from patients
        patientMessages = new ArrayList<>();
        patientMessages.add(Map.of(
            "messageId", "MSG-301",
            "patientName", "Eleanor Vance",
            "patientId", "PAT-90821",
            "subject", "Medication Refill & Prescription Query",
            "snippet", "Doctor, can you please refill my Lisinopril 10mg prescription before the weekend?",
            "time", "Today at 08:15 AM",
            "status", "Pending Reply"
        ));
        patientMessages.add(Map.of(
            "messageId", "MSG-302",
            "patientName", "Liam Montgomery",
            "patientId", "PAT-90822",
            "subject", "Lab Test Clarification",
            "snippet", "I noticed a slight bump in my fasting glucose from the recent metabolic panel...",
            "time", "Yesterday at 04:30 PM",
            "status", "Replied"
        ));
    }

    public Map<String, Object> getDoctorProfile() {
        return doctorProfile;
    }

    public List<Map<String, Object>> getScheduledPatients() {
        return scheduledPatients;
    }

    public List<Map<String, Object>> getPatientMessages() {
        return patientMessages;
    }
}

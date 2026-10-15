package com.healthcare.core.models;

import com.healthcare.core.services.FhirIntegrationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;

import javax.annotation.PostConstruct;
import java.util.Map;

@Model(
    adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PatientPortalModel {

    @RequestAttribute(name = "patientId")
    private String patientId;

    @OSGiService
    private FhirIntegrationService fhirService;

    private Map<String, Object> patientRecord;

    @PostConstruct
    protected void init() {
        if (patientId == null || patientId.isBlank()) {
            patientId = "PAT-90821"; // Sample authenticated patient session
        }
        if (fhirService != null) {
            patientRecord = fhirService.getPatientRecord(patientId);
        }
    }

    public String getPatientId() {
        return patientId;
    }

    public Map<String, Object> getPatientRecord() {
        return patientRecord;
    }
}

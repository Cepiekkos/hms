package com.healthcare.core.models;

import com.healthcare.core.services.FhirIntegrationService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DoctorDirectoryModel {

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String defaultDepartment;

    @OSGiService
    private FhirIntegrationService fhirService;

    private List<Map<String, Object>> doctors;

    @PostConstruct
    protected void init() {
        if (title == null || title.isBlank()) {
            title = "Specialist & Physician Directory";
        }
        if (fhirService != null) {
            doctors = fhirService.getPractitioners(defaultDepartment);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDefaultDepartment() {
        return defaultDepartment;
    }

    public List<Map<String, Object>> getDoctors() {
        return doctors;
    }
}

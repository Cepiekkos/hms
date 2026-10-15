package com.healthcare.core.services;

import java.util.List;
import java.util.Map;

/**
 * Service to interface with EHR / HL7 FHIR servers (e.g. HAPI FHIR, Epic, Cerner)
 */
public interface FhirIntegrationService {

    /**
     * Retrieve patient medical records by Patient ID
     * @param patientId Unique Patient Identifier
     * @return Map containing demographic and clinical records
     */
    Map<String, Object> getPatientRecord(String patientId);

    /**
     * Fetch list of available doctors and schedules
     * @param department Optional department filter
     * @return List of practitioners
     */
    List<Map<String, Object>> getPractitioners(String department);

    /**
     * Create appointment booking resource in FHIR EHR
     * @param appointmentData Details of appointment (date, doctorId, patientId, notes)
     * @return Confirmation ID and status
     */
    Map<String, Object> bookAppointment(Map<String, Object> appointmentData);

    /**
     * Submit secure consultation request / clinical message
     * @param consultationData Data payload
     * @return Submission confirmation
     */
    boolean submitConsultationRequest(Map<String, Object> consultationData);
}

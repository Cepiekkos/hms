/**
 * Shared in-memory data store for live mutual interaction between Doctors, Patients, and Staff
 */
window.HealthcareStore = (function() {
    'use strict';

    const STORAGE_KEY = 'aurahealth_app_state_v2';

    const DIRECTORY = {
        doctors: [
            { id: "DOC-001", name: "Dr. Sarah Jenkins, MD", department: "Cardiology & Vascular", hospital: "Metropolitan Medical Center", role: "doctor" },
            { id: "DOC-002", name: "Dr. Marcus Chen, DO", department: "Neurology & Brain Sciences", hospital: "Metropolitan Medical Center", role: "doctor" },
            { id: "DOC-003", name: "Dr. Aliyah Patel, MD", department: "Pediatrics & Child Health", hospital: "St. Jude Children's Wing", role: "doctor" },
            { id: "DOC-004", name: "Dr. Robert Rodriguez, MD", department: "Orthopedics & Sports Medicine", hospital: "Sports Health Pavilion", role: "doctor" }
        ],
        patients: [
            { id: "PAT-90821", name: "Eleanor Vance", email: "eleanor.vance@example.com", primaryDoctor: "Dr. Sarah Jenkins, MD", role: "patient" },
            { id: "PAT-90822", name: "Liam Montgomery", email: "liam.montgomery@example.com", primaryDoctor: "Dr. Marcus Chen, DO", role: "patient" },
            { id: "PAT-90823", name: "Grace Thorne", email: "grace.thorne@example.com", primaryDoctor: "Dr. Sarah Jenkins, MD", role: "patient" }
        ]
    };

    const defaultState = {
        currentUser: null,
        directory: DIRECTORY,
        appointments: [
            {
                id: "APT-88219",
                patientName: "Eleanor Vance",
                patientId: "PAT-90821",
                age: 38,
                doctorName: "Dr. Sarah Jenkins, MD",
                doctorId: "DOC-001",
                department: "Cardiology",
                time: "09:00 AM",
                date: "2026-10-15",
                type: "Hypertension & Lipid Review",
                status: "Confirmed",
                vitals: "BP: 120/78 • HR: 72 bpm"
            },
            {
                id: "APT-88220",
                patientName: "Liam Montgomery",
                patientId: "PAT-90822",
                age: 52,
                doctorName: "Dr. Sarah Jenkins, MD",
                doctorId: "DOC-001",
                department: "Cardiology",
                time: "10:30 AM",
                date: "2026-10-15",
                type: "Telemetry Arrhythmia Evaluation",
                status: "Confirmed",
                vitals: "BP: 135/88 • HR: 86 bpm"
            }
        ],
        messages: [
            {
                id: "MSG-101",
                fromId: "PAT-90821",
                fromName: "Eleanor Vance",
                fromRole: "patient",
                toId: "DOC-001",
                toName: "Dr. Sarah Jenkins, MD",
                toRole: "doctor",
                subject: "Medication Refill & Prescription Query",
                body: "Good morning Dr. Jenkins, can you please refill my Lisinopril 10mg prescription before the weekend? I have 3 tablets remaining.",
                timestamp: "Today at 08:15 AM",
                category: "Patient Inquiry",
                reply: null
            },
            {
                id: "MSG-102",
                fromId: "DOC-002",
                fromName: "Dr. Marcus Chen, DO",
                fromRole: "doctor",
                toId: "DOC-001",
                toName: "Dr. Sarah Jenkins, MD",
                toRole: "doctor",
                subject: "Cardio-Neurology Clinical Consultation regarding Liam Montgomery",
                body: "Sarah, Liam presented with transient lightheadedness during his arrhythmia check. Could we co-review his Holter telemetry alongside his EEG?",
                timestamp: "Yesterday at 03:40 PM",
                category: "Peer Physician Referral",
                reply: {
                    doctorName: "Dr. Sarah Jenkins, MD",
                    text: "Marcus, reviewing the Holter readout now. His telemetry shows brief sinus pauses during nocturnal hours. Let's schedule a joint consult tomorrow.",
                    timestamp: "Yesterday at 05:10 PM"
                }
            }
        ],
        clinicalRecords: {
            "PAT-90821": {
                history: { primaryDiagnosis: "", allergies: "", updatedBy: "", updatedAt: "" },
                historyEntries: [],
                vitals: [{ date: "2026-09-15", bloodPressure: "120/78", heartRate: "72", spo2: "99", glucose: "92", recordedBy: "Dr. Sarah Jenkins, MD" }],
                labReports: [
                    { test: "Comprehensive Metabolic Panel (CMP)", date: "2026-09-15", result: "All Values in Normal Range", status: "Final", doctorName: "Dr. Sarah Jenkins, MD" },
                    { test: "Lipid Panel with Chol/HDL Ratio", date: "2026-08-01", result: "Desirable (Total Chol: 182)", status: "Final", doctorName: "Dr. Sarah Jenkins, MD" },
                    { test: "HbA1c Glycated Hemoglobin", date: "2026-05-10", result: "5.4% (Non-Diabetic)", status: "Final", doctorName: "Dr. Sarah Jenkins, MD" }
                ]
            }
        },
        prescriptions: [
            {
                rxNumber: "RX-551029",
                patientName: "Eleanor Vance",
                patientId: "PAT-90821",
                doctorName: "Dr. Sarah Jenkins, MD",
                medication: "Lisinopril 10mg Oral Tablet",
                dosage: "Take 1 tablet daily in morning",
                quantity: "30 Tablets",
                refills: "3 Refills",
                pharmacy: "CVS Pharmacy #4190 (Main St)",
                date: "2026-09-15",
                status: "Active / Filled"
            }
        ]
    };

    function getState() {
        try {
            const data = localStorage.getItem(STORAGE_KEY);
            if (data) {
                const state = JSON.parse(data);
                if (!state.clinicalRecords) {
                    state.clinicalRecords = JSON.parse(JSON.stringify(defaultState.clinicalRecords));
                    saveState(state);
                }
                return state;
            }
        } catch (e) {
            console.warn('Storage error, falling back to default', e);
        }
        return JSON.parse(JSON.stringify(defaultState));
    }

    function saveState(state) {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
        } catch (e) {
            console.error('Failed to save state', e);
        }
    }

    function addAppointment(appt) {
        const state = getState();
        state.clinicalRecords = state.clinicalRecords || {};
        if (!state.clinicalRecords[appt.patientId]) {
            state.clinicalRecords[appt.patientId] = {
                history: { primaryDiagnosis: '', allergies: '', updatedBy: '', updatedAt: '' },
                historyEntries: [],
                vitals: [],
                labReports: []
            };
        }
        state.appointments.unshift(appt);
        saveState(state);
    }

    function updateAppointmentStatus(appointmentId, newStatus) {
        const state = getState();
        const appt = state.appointments.find(a => a.id === appointmentId);
        if (appt) {
            appt.status = newStatus;
            saveState(state);
        }
    }

    function getClinicalRecord(patientId) {
        const state = getState();
        const record = state.clinicalRecords[patientId] || {};
        const historyEntries = getClinicalHistoryEntries(record);
        return {
            history: historyEntries.length ? historyEntries[historyEntries.length - 1] : (record.history || { primaryDiagnosis: '', allergies: '', updatedBy: '', updatedAt: '' }),
            historyEntries: historyEntries,
            vitals: record.vitals || [],
            labReports: record.labReports || []
        };
    }

    function getClinicalHistoryEntries(record) {
        if (Array.isArray(record.historyEntries) && record.historyEntries.length) {
            return record.historyEntries;
        }

        const legacyHistory = record.history || {};
        if (!legacyHistory.primaryDiagnosis && !legacyHistory.allergies) return [];

        return [{
            primaryDiagnosis: legacyHistory.primaryDiagnosis || '',
            allergies: legacyHistory.allergies || '',
            updatedBy: legacyHistory.updatedBy || '',
            updatedAt: legacyHistory.updatedAt || ''
        }];
    }

    function saveClinicalHistory(patientId, history) {
        const state = getState();
        state.clinicalRecords = state.clinicalRecords || {};
        const record = state.clinicalRecords[patientId] || { vitals: [], labReports: [] };
        const entry = {
            primaryDiagnosis: history.primaryDiagnosis || '',
            allergies: history.allergies || '',
            updatedBy: history.updatedBy || '',
            updatedAt: new Date().toISOString()
        };
        record.historyEntries = [...getClinicalHistoryEntries(record), entry];
        record.history = entry;
        state.clinicalRecords[patientId] = record;
        saveState(state);
    }

    function savePatientVitals(patientId, vitals) {
        const state = getState();
        const record = state.clinicalRecords[patientId] || { vitals: [], labReports: [] };
        record.vitals.push({ ...vitals, recordedAt: new Date().toISOString() });
        state.clinicalRecords[patientId] = record;
        saveState(state);
    }

    function addLabResult(patientId, labResult) {
        const state = getState();
        const record = state.clinicalRecords[patientId] || { vitals: [], labReports: [] };
        record.labReports.unshift(labResult);
        state.clinicalRecords[patientId] = record;
        saveState(state);
    }

    function getActiveMedications(patientId) {
        const prescriptions = getState().prescriptions.filter(prescription =>
            prescription.patientId === patientId && !/discontinued|cancelled|canceled/i.test(prescription.status || '')
        );
        const medicationGroups = new Map();

        prescriptions.forEach(prescription => {
            const key = `${(prescription.medication || '').trim().toLowerCase()}|${(prescription.dosage || '').trim().toLowerCase()}`;
            const medication = medicationGroups.get(key);
            if (medication) {
                medication.prescriptionCount += 1;
            } else {
                medicationGroups.set(key, { ...prescription, prescriptionCount: 1 });
            }
        });

        return Array.from(medicationGroups.values());
    }

    function addMessage(msg) {
        const state = getState();
        msg.threadId = msg.threadId || msg.id;
        msg.replies = Array.isArray(msg.replies) ? msg.replies : [];
        state.messages.unshift(msg);
        saveState(state);
    }

    function getMessageReplies(message) {
        if (Array.isArray(message.replies) && message.replies.length) return message.replies;
        if (!message.reply) return [];

        return [{
            authorName: message.reply.authorName || message.reply.doctorName || 'Care Team',
            authorRole: message.reply.authorRole || 'doctor',
            fromId: message.reply.fromId,
            text: message.reply.text || '',
            timestamp: message.reply.timestamp || ''
        }];
    }

    function replyMessage(messageId, replyData) {
        const state = getState();
        const msg = state.messages.find(m => m.id === messageId);
        if (!msg) return;

        const currentUser = state.currentUser || { role: 'doctor', id: 'DOC-001', name: 'Dr. Sarah Jenkins, MD' };
        const normalizedReply = typeof replyData === 'string'
            ? {
                fromName: currentUser.name,
                fromId: currentUser.id,
                fromRole: currentUser.role,
                text: replyData,
                timestamp: 'Just now'
            }
            : replyData;

        const reply = {
            authorName: normalizedReply.fromName || 'Care Team',
            authorRole: normalizedReply.fromRole || (normalizedReply.fromId && normalizedReply.fromId.startsWith('DOC') ? 'doctor' : 'patient'),
            fromId: normalizedReply.fromId || currentUser.id,
            text: normalizedReply.text || '',
            timestamp: normalizedReply.timestamp || 'Just now'
        };

        msg.threadId = msg.threadId || msg.id;
        msg.replies = [...getMessageReplies(msg), reply];
        msg.reply = {
            ...reply,
            doctorName: reply.authorRole === 'doctor' ? reply.authorName : (msg.toName || 'Care Team')
        };
        state.messages = state.messages.filter(message => message.id !== messageId);
        state.messages.unshift(msg);
        saveState(state);
    }

    function addPrescription(rx) {
        const state = getState();
        state.prescriptions.unshift(rx);
        saveState(state);
    }

    function setCurrentUser(user) {
        const state = getState();
        state.currentUser = user;
        saveState(state);
    }

    function getCurrentUser() {
        return getState().currentUser;
    }

    function getDirectory() {
        return DIRECTORY;
    }

    return {
        getState: getState,
        getDirectory: getDirectory,
        getMessageReplies: getMessageReplies,
        getClinicalRecord: getClinicalRecord,
        saveClinicalHistory: saveClinicalHistory,
        addAppointment: addAppointment,
        updateAppointmentStatus: updateAppointmentStatus,
        savePatientVitals: savePatientVitals,
        addLabResult: addLabResult,
        getActiveMedications: getActiveMedications,
        addMessage: addMessage,
        replyMessage: replyMessage,
        addPrescription: addPrescription,
        setCurrentUser: setCurrentUser,
        getCurrentUser: getCurrentUser
    };
})();

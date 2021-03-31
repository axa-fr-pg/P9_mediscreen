package mediscreen.report.service;

import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientData;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface AssessmentService {

    String RISK_NONE = "None";
    String RISK_BORDERLINE = "Borderline";
    String RISK_IN_DANDER = "In danger";
    String RISK_EARLY_ONSET = "Early onset";

    PatientAssessmentDTO get(PatientData patientData) throws DoctorUnavailableException, IOException;
    PatientAssessmentDTO get(long patientId) throws IOException, PatientNotFoundException, DoctorUnavailableException;
    PatientAssessmentDTO get(String family) throws PatientNotUniqueException, PatientNotFoundException, IOException, DoctorUnavailableException;
    Page<PatientRiskDTO> get(Pageable pageRequest) throws IOException, DoctorUnavailableException;
    Page<PatientRiskDTO> get(Pageable pageRequest, String filterId, String filterFamily) throws IOException, DoctorUnavailableException;
}

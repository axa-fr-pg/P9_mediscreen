package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface PatientService {
    PatientData get(long patientId) throws IOException, PatientNotFoundException;
    PatientData get(String family) throws PatientNotFoundException, PatientNotUniqueException;
    Page<PatientData> getPage(Pageable pageRequest);
    Page<PatientData> getPage(Pageable pageRequest, String filterId, String filterFamily);
}

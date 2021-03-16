package mediscreen.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import mediscreen.report.client.PatientClient;
import mediscreen.report.model.PatientData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    PatientClient client;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public PatientData getByPatientId(long patientId)
            throws JsonProcessingException, PatientNotFoundException {
        Response response = client.get(patientId);
        if (response.status() == HttpStatus.OK.value()) {
            return objectMapper.readValue(response.body().toString(), PatientData.class);
        }
        throw new PatientNotFoundException(
                "Could not find patient with id " + patientId +
                " : received return code " + response.status() + " from API."
        );
    }

    @Override
    public PatientData getByFamily(String family) throws PatientNotFoundException, PatientNotUniqueException {
        Page<PatientData> patientDataPage = client.getPage(
                PageRequest.of(0,10),
                "",
                family,
                "");
        int size = patientDataPage.getSize();
        switch (size) {
            case 0:
                throw new PatientNotFoundException(
                        "Could not find patient with name " + family +
                        " : received empty page as response from API."
                );
            case 1:
                return patientDataPage.getContent().get(0);
            default:
                throw new PatientNotUniqueException(
                        "Could not find patient with name " + family +
                        " : received " + size + " matches from API."
                );
        }
    }
}

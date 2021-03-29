package mediscreen.report.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="doctor", url="http://doctor:8082/notes")
public interface DoctorClient {
    @GetMapping("/{patientId}")
    Response getPatientNotes(@PathVariable Long patientId);
}

package mediscreen.report.client;

import feign.Response;
import mediscreen.report.model.PatientData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="patient", url="http://patient:8081")
public interface PatientClient {

    @GetMapping("/{patientId}")
    Response get(@PathVariable Long patientId);

    @GetMapping("")
    Page<PatientData> getPage(Pageable pageRequest,
                              @RequestParam(required = false) String id,
                              @RequestParam(required = false) String family,
                              @RequestParam(required = false) String dob);
}

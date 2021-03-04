package mediscreen.report.controller;

import mediscreen.report.model.PatientRiskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/reports")
@CrossOrigin
public class ReportController {

    @GetMapping("/patients")
    public ResponseEntity<Page<PatientRiskDTO>> getPage(
            Pageable pageRequest) {

        return new ResponseEntity<>(
                new PageImpl<>(Collections.singletonList(new PatientRiskDTO()), pageRequest, 0),
                HttpStatus.OK);
    }

}

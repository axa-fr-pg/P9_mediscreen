package mediscreen.report.controller;

import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientRiskDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/reports")
@CrossOrigin
public class ReportController {

    @GetMapping("/patients")
    public ResponseEntity<Object> getPatientOrPage(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String family,
            Pageable pageRequest) throws RequestParamConflictException {
        if (! isNull(id) && ! isNull(family)) {
            throw new RequestParamConflictException();
        }
        if (! isNull(id)) {
            return new ResponseEntity<>(
                    new PatientAssessmentDTO("Sample response by id : Patient: Test TestNone (age 52) diabetes assessment is: None"),
                    HttpStatus.OK);
        }
        if (! isNull(family)) {
            return new ResponseEntity<>(
                    new PatientAssessmentDTO("Sample response by family : Patient: Test TestNone (age 52) diabetes assessment is: None"),
                    HttpStatus.OK);
        }
        return new ResponseEntity<>(
                new PageImpl<>(Collections.singletonList(new PatientRiskDTO()), pageRequest, 0),
                HttpStatus.OK);
    }
}

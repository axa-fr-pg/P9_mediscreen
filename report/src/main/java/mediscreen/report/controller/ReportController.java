package mediscreen.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import mediscreen.report.model.PatientAssessmentDTO;
import mediscreen.report.model.PatientRiskDTO;
import mediscreen.report.service.AssessmentService;
import mediscreen.report.service.DoctorUnavailableException;
import mediscreen.report.service.PatientNotFoundException;
import mediscreen.report.service.PatientNotUniqueException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    AssessmentService assessmentService;

    @GetMapping("/patients")
    public ResponseEntity<Object> get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String family,
            Pageable pageRequest)
            throws Throwable {
        if (! isNull(id) && ! isNull(family)) {
            throw new RequestParamConflictException("You cannot assess at the same time by patient id and by family");
        }
        if (! isNull(id)) {
            return new ResponseEntity<>(assessmentService.get(id), HttpStatus.OK);
        }
        if (! isNull(family)) {
            return new ResponseEntity<>(assessmentService.get(family), HttpStatus.OK);
        }
        return new ResponseEntity<>(assessmentService.get(pageRequest), HttpStatus.OK);
    }
}

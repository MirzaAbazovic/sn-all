package de.bitconex.agreement.api;

import de.bitconex.agreement.model.AgreementSpecification;
import de.bitconex.agreement.model.AgreementSpecificationCreate;
import de.bitconex.agreement.model.AgreementSpecificationUpdate;
import de.bitconex.agreement.service.AgreementSpecificationService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AgreementSpecificationApiController implements AgreementSpecificationApi {

    private final AgreementSpecificationService agreementSpecificationService;

    public AgreementSpecificationApiController(AgreementSpecificationService agreementSpecificationService) {
        this.agreementSpecificationService = agreementSpecificationService;
    }

    @PostMapping("/agreementSpecification")
    public ResponseEntity<AgreementSpecification> createAgreementSpecification(@ApiParam(value = "The AgreementSpecification to be created", required = true) @Valid @RequestBody AgreementSpecificationCreate agreementSpecification) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agreementSpecificationService.createAgreementSpecification(agreementSpecification));
    }

    @DeleteMapping("/agreementSpecification/{id}")
    public ResponseEntity<Void> deleteAgreementSpecification(@ApiParam(value = "Identifier of the AgreementSpecification", required = true) @PathVariable("id") String id) {
        agreementSpecificationService.deleteAgreementSpecification(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agreementSpecification")
    public ResponseEntity<List<AgreementSpecification>> listAgreementSpecification(@ApiParam(value = "Comma-separated properties to be provided in response") @Valid @RequestParam(value = "fields", required = false) String fields, @ApiParam(value = "Requested index for start of resources to be provided in response") @Valid @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "Requested number of resources to be provided in response") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(agreementSpecificationService.getAllAgreementSpecifications());
    }

    @PatchMapping("/agreementSpecification/{id}")
    public ResponseEntity<AgreementSpecification> patchAgreementSpecification(@ApiParam(value = "Identifier of the AgreementSpecification", required = true) @PathVariable("id") String id, @ApiParam(value = "The AgreementSpecification to be updated", required = true) @Valid @RequestBody AgreementSpecificationUpdate agreementSpecification) {
        return ResponseEntity.ok(agreementSpecificationService.patchAgreementSpecification(id, agreementSpecification));
    }

    @GetMapping("/agreementSpecification/{id}")
    public ResponseEntity<AgreementSpecification> retrieveAgreementSpecification(@ApiParam(value = "Identifier of the AgreementSpecification", required = true) @PathVariable("id") String id, @ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false) String fields) {
        return ResponseEntity.ok(agreementSpecificationService.getAgreementSpecification(id));
    }

}

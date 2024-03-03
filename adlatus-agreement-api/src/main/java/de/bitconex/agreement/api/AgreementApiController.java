package de.bitconex.agreement.api;

import de.bitconex.agreement.model.Agreement;
import de.bitconex.agreement.model.AgreementCreate;
import de.bitconex.agreement.model.AgreementUpdate;
import de.bitconex.agreement.service.AgreementService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/agreement")
public class AgreementApiController implements AgreementApi {

    private final AgreementService agreementService;

    public AgreementApiController(AgreementService agreementService) {
        this.agreementService = agreementService;
    }

    @PostMapping()
    public ResponseEntity<Agreement> createAgreement(@ApiParam(value = "The Agreement to be created", required=true ) @Valid @RequestBody AgreementCreate agreement) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                agreementService.createAgreement(agreement)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id) {
        agreementService.deleteAgreement(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<List<Agreement>> listAgreement(@ApiParam(value = "Comma-separated properties to be provided in response") @Valid @RequestParam(value = "fields", required = false) String fields,@ApiParam(value = "Requested index for start of resources to be provided in response") @Valid @RequestParam(value = "offset", required = false) Integer offset,@ApiParam(value = "Requested number of resources to be provided in response") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
        return ResponseEntity.status(HttpStatus.OK).body(agreementService.getAllAgreements());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Agreement> patchAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id,@ApiParam(value = "The Agreement to be updated" ,required=true )  @Valid @RequestBody AgreementUpdate agreement) {
        return ResponseEntity.ok(agreementService.patchAgreement(id, agreement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agreement> retrieveAgreement(@ApiParam(value = "Identifier of the Agreement",required=true) @PathVariable("id") String id,@ApiParam(value = "Comma-separated properties to provide in response") @Valid @RequestParam(value = "fields", required = false) String fields) {
        Agreement agreement = agreementService.getAgreement(id);
        if (agreement != null) {
            return ResponseEntity.ok(agreement);
        }

        return ResponseEntity.notFound().build();
    }

}

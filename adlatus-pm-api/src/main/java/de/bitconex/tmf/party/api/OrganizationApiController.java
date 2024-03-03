package de.bitconex.tmf.party.api;

import de.bitconex.tmf.party.models.Organization;
import de.bitconex.tmf.party.models.OrganizationCreate;
import de.bitconex.tmf.party.models.OrganizationUpdate;
import de.bitconex.tmf.party.service.OrganizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organization")
public class OrganizationApiController implements OrganizationApi {
    private final OrganizationService organizationService;

    public OrganizationApiController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping
    public ResponseEntity<Organization> createOrganization(OrganizationCreate organization) {
        return new ResponseEntity<>(organizationService.createOrganization((organization)), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable String id) {
        var deleted = organizationService.deleteOrganization(id);
        if (!deleted) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Organization>> listOrganization(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(organizationService.listOrganization(fields, offset, limit, allParams));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Organization> patchOrganization(@PathVariable String id, OrganizationUpdate organization) {
        var updated = organizationService.patchOrganization(id, organization);
        if (updated == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> retrieveOrganization(@PathVariable String id, String fields) {
        var organization = organizationService.retrieveOrganization(id, fields);
        if (organization == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(organization);
    }
}

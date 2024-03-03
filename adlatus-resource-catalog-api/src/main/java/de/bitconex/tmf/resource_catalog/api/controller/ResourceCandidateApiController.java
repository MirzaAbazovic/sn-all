package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ResourceCandidateApi;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateUpdate;
import de.bitconex.tmf.resource_catalog.service.ResourceCandidateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Controller
@RequestMapping("")
public class ResourceCandidateApiController implements ResourceCandidateApi {

    private final NativeWebRequest request;

    private final ResourceCandidateService resourceCandidateService;

    @Autowired
    public ResourceCandidateApiController(NativeWebRequest request, ResourceCandidateService resourceCandidateService) {
        this.request = request;
        this.resourceCandidateService = resourceCandidateService;
    }

    @Override
    public ResponseEntity<ResourceCandidate> createResourceCandidate(ResourceCandidateCreate resourceCandidate) {
        return ResponseEntity.created(null).body(resourceCandidateService.createResourceCandidate(resourceCandidate));
    }

    @Override
    public ResponseEntity<Void> deleteResourceCandidate(String id) {
        var deleted = resourceCandidateService.deleteResourceCandidate(id);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ResourceCandidate>> listResourceCandidate(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(resourceCandidateService.listResourceCandidate(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ResourceCandidate> patchResourceCandidate(String id, ResourceCandidateUpdate resourceCandidate) {
        var resCand = resourceCandidateService.patchResourceCandidate(id, resourceCandidate);
        if (resCand == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resCand);
    }

    @Override
    public ResponseEntity<ResourceCandidate> retrieveResourceCandidate(String id, String fields) {
        var resCand = resourceCandidateService.retrieveResourceCandidate(id, fields);
        if (resCand == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resCand);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

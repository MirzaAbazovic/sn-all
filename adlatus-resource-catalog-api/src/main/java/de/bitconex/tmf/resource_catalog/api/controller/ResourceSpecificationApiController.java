package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ResourceSpecificationApi;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecification;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationUpdate;
import de.bitconex.tmf.resource_catalog.service.ResourceSpecificationService;
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
public class ResourceSpecificationApiController implements ResourceSpecificationApi {
    private final ResourceSpecificationService resourceSpecificationService;
    private final NativeWebRequest request;

    public ResourceSpecificationApiController(NativeWebRequest request, ResourceSpecificationService resourceSpecificationService) {
        this.request = request;
        this.resourceSpecificationService = resourceSpecificationService;
    }

    @Override
    public ResponseEntity<ResourceSpecification> createResourceSpecification(ResourceSpecificationCreate resourceSpecification) {
        return ResponseEntity.created(null).body(resourceSpecificationService.createResourceSpecification(resourceSpecification));
    }

    @Override
    public ResponseEntity<Void> deleteResourceSpecification(String id) {
        var deleted = resourceSpecificationService.deleteResourceSpecification(id);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ResourceSpecification>> listResourceSpecification(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(resourceSpecificationService.listResourceSpecification(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ResourceSpecification> patchResourceSpecification(String id, ResourceSpecificationUpdate resourceSpecification) {
        var updatedResSpec = resourceSpecificationService.patchResourceSpecification(id, resourceSpecification);
        if (updatedResSpec == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedResSpec);
    }

    @Override
    public ResponseEntity<ResourceSpecification> retrieveResourceSpecification(String id, String fields) {
        var resSpec = resourceSpecificationService.retrieveResourceSpecification(id, fields);
        if (resSpec == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resSpec);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

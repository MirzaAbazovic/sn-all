package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ResourceCatalogApi;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalog;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCatalogUpdate;
import de.bitconex.tmf.resource_catalog.service.ResourceCatalogService;
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
public class ResourceCatalogApiController implements ResourceCatalogApi {
    private final ResourceCatalogService resourceCatalogService;
    private final NativeWebRequest request;

    public ResourceCatalogApiController(NativeWebRequest request, ResourceCatalogService resourceCatalogService) {
        this.request = request;
        this.resourceCatalogService = resourceCatalogService;
    }

    @Override
    public ResponseEntity<ResourceCatalog> createResourceCatalog(ResourceCatalogCreate resourceCatalog) {
        return ResponseEntity.created(null).body(resourceCatalogService.createResourceCatalog(resourceCatalog));
    }

    @Override
    public ResponseEntity<Void> deleteResourceCatalog(String id) {
        var deleted = resourceCatalogService.deleteResourceCatalog(id);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ResourceCatalog>> listResourceCatalog(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(resourceCatalogService.listResourceCatalog(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ResourceCatalog> patchResourceCatalog(String id, ResourceCatalogUpdate resourceCatalog) {
        var updatedResCat = resourceCatalogService.patchResourceCatalog(id, resourceCatalog);
        if (updatedResCat == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedResCat);
    }

    @Override
    public ResponseEntity<ResourceCatalog> retrieveResourceCatalog(String id, String fields) {
        var resCat = resourceCatalogService.retrieveResourceCatalog(id, fields);
        if (resCat == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resCat);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

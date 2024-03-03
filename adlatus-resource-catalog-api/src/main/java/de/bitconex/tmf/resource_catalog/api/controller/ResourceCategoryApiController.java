package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ResourceCategoryApi;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapper;
import de.bitconex.tmf.resource_catalog.mapper.ResourceCategoryMapperImpl;
import de.bitconex.tmf.resource_catalog.model.ResourceCategory;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryUpdate;
import de.bitconex.tmf.resource_catalog.service.ResourceCategoryService;
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
public class ResourceCategoryApiController implements ResourceCategoryApi {
    private final ResourceCategoryService resourceCategoryService;
    private final ResourceCategoryMapper resourceCategoryMapper = new ResourceCategoryMapperImpl();

    private final NativeWebRequest request;


    public ResourceCategoryApiController(NativeWebRequest request, ResourceCategoryService resourceCategoryService) {
        this.request = request;
        this.resourceCategoryService = resourceCategoryService;
    }

    @Override
    public ResponseEntity<ResourceCategory> createResourceCategory(ResourceCategoryCreate resourceCategory) {
        return ResponseEntity.created(null).body(resourceCategoryService.createResourceCategory(resourceCategory));
    }

    @Override
    public ResponseEntity<Void> deleteResourceCategory(String id) {
        var deleted = resourceCategoryService.deleteResourceCategory(id);
        if (!deleted)
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ResourceCategory>> listResourceCategory(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(resourceCategoryService.listResourceCategory(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ResourceCategory> patchResourceCategory(String id, ResourceCategoryUpdate resourceCategory) {
        var updatedResCat = resourceCategoryService.patchResourceCategory(id, resourceCategory);
        if (updatedResCat == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedResCat);
    }

    @Override
    public ResponseEntity<ResourceCategory> retrieveResourceCategory(String id, String fields) {
        var resCat = resourceCategoryService.retrieveResourceCategory(id, fields);
        if (resCat == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(resCat);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

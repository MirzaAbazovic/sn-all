package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.model.ResourceCategory;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryRef;
import de.bitconex.tmf.resource_catalog.model.ResourceCategoryUpdate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ResourceCategoryService {
    ResourceCategory createResourceCategory(ResourceCategoryCreate resourceCategory);

    ResourceCategory createResourceCategory(ResourceCategoryRef resourceCategory);


    Boolean deleteResourceCategory(String id);


    List<ResourceCategory> listResourceCategory(String fields, Integer offset, Integer limit, Map<String, String> allParams);


    ResourceCategory patchResourceCategory(String id, ResourceCategoryUpdate resourceCategory);


    ResourceCategory retrieveResourceCategory(String id, String fields);
}

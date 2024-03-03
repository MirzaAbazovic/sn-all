package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.api.ResourceCatalogApi;
import de.bitconex.tmf.resource_catalog.api.ResourceCategoryApi;
import de.bitconex.tmf.resource_catalog.model.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ResourceCatalogService {

    ResourceCatalog createResourceCatalog(ResourceCatalogCreate resourceCatalog);


    Boolean deleteResourceCatalog(String id);


    List<ResourceCatalog> listResourceCatalog(String fields, Integer offset, Integer limit, Map<String, String> allParams);


    ResourceCatalog patchResourceCatalog(String id, ResourceCatalogUpdate resourceCatalog);


    ResourceCatalog retrieveResourceCatalog(String id, String fields);


}

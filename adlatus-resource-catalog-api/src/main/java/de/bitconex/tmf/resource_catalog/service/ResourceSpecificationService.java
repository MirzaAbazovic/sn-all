package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.model.ResourceSpecification;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationRef;
import de.bitconex.tmf.resource_catalog.model.ResourceSpecificationUpdate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ResourceSpecificationService {

    ResourceSpecification createResourceSpecification(ResourceSpecificationCreate resourceSpecification);

    ResourceSpecification createResourceSpecification(ResourceSpecificationRef resourceSpecification);

    Boolean deleteResourceSpecification(String id);


    List<ResourceSpecification> listResourceSpecification(String fields, Integer offset, Integer limit, Map<String, String> allParams);


    ResourceSpecification patchResourceSpecification(String id, ResourceSpecificationUpdate resourceSpecification);


    ResourceSpecification retrieveResourceSpecification(String id, String fields);
}

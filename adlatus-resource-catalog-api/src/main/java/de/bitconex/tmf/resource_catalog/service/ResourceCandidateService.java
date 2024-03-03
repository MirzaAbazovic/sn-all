package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.api.ResourceCandidateApi;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateRef;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateUpdate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ResourceCandidateService {
    ResourceCandidate createResourceCandidate(ResourceCandidateCreate resourceCandidate);

    ResourceCandidate createResourceCandidate(ResourceCandidateRef resourceCandidate);

    Boolean deleteResourceCandidate(String id);

    List<ResourceCandidate> listResourceCandidate(String fields, Integer offset, Integer limit, Map<String, String> allParams);

    ResourceCandidate patchResourceCandidate(String id, ResourceCandidateUpdate resourceCandidate);

    ResourceCandidate retrieveResourceCandidate(String id, String fields);
}

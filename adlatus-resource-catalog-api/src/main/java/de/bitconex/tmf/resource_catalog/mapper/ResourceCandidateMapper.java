package de.bitconex.tmf.resource_catalog.mapper;

import de.bitconex.tmf.resource_catalog.model.ResourceCandidate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateCreate;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateRef;
import de.bitconex.tmf.resource_catalog.model.ResourceCandidateUpdate;
import org.mapstruct.Mapper;

@Mapper
public interface ResourceCandidateMapper {

    ResourceCandidateRef toResourceCandidateRef(ResourceCandidate resourceCandidate);

    ResourceCandidate toResourceCandidate(ResourceCandidateCreate dto);

    ResourceCandidate toResourceCandidate(ResourceCandidateRef dto);

    ResourceCandidate toResourceCandidate(ResourceCandidateUpdate dto);
}

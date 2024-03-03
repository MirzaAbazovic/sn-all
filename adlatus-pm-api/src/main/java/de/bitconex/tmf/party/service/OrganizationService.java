package de.bitconex.tmf.party.service;

import de.bitconex.tmf.party.models.Organization;
import de.bitconex.tmf.party.models.OrganizationCreate;
import de.bitconex.tmf.party.models.OrganizationRef;
import de.bitconex.tmf.party.models.OrganizationUpdate;

import java.util.List;
import java.util.Map;

public interface OrganizationService {
    Organization createOrganization(OrganizationCreate organization);

    Organization createOrganization(OrganizationRef organization);

    Boolean deleteOrganization(String id);

    List<Organization> listOrganization(String fields, Integer offset, Integer limit, Map<String, String> allParams);

    Organization patchOrganization(String id, OrganizationUpdate organization);

    Organization retrieveOrganization(String id, String fields);
}

package de.bitconex.tmf.party.service.impl;

import de.bitconex.tmf.party.mapper.OrganizationMapper;
import de.bitconex.tmf.party.mapper.OrganizationMapperImpl;
import de.bitconex.tmf.party.models.Organization;
import de.bitconex.tmf.party.models.OrganizationCreate;
import de.bitconex.tmf.party.models.OrganizationRef;
import de.bitconex.tmf.party.models.OrganizationUpdate;
import de.bitconex.tmf.party.repository.OrganizationRepository;
import de.bitconex.tmf.party.service.OrganizationService;
import de.bitconex.tmf.party.utility.HrefTypes;
import de.bitconex.tmf.party.utility.ModelUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper = new OrganizationMapperImpl();

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization createOrganization(OrganizationCreate organizationCreate) {
        var org = organizationMapper.toOrganization(organizationCreate);
        ModelUtils.setIdAndHref(org, HrefTypes.Organization.getHrefType());
        processNestedFields(org);
        return organizationRepository.save(org);
    }

    @Override
    public Organization createOrganization(OrganizationRef organization) {
        var org = organizationMapper.toOrganization(organization);
        ModelUtils.setIdAndHref(org, HrefTypes.Organization.getHrefType());
        return organizationRepository.save(org);
    }

    @Override
    public Boolean deleteOrganization(String id) {
        var organization = organizationRepository.findById(id).orElse(null);
        if (organization == null) return false;
        organization.setIsDeleted(true);
        organizationRepository.save(organization);
        return true;
    }

    @Override
    public List<Organization> listOrganization(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ModelUtils.getCollectionItems(Organization.class, fields, offset, limit, allParams);
    }

    @Override
    public Organization patchOrganization(String id, OrganizationUpdate organizationUpdate) {
        var organization = organizationRepository.findById(id).orElse(null);
        if (organization == null) return null;
        var updatedOrganization = organizationMapper.toOrganization(organizationUpdate);
        ModelUtils.patchEntity(organization, updatedOrganization);
        processNestedFields(updatedOrganization);
        organizationRepository.save(updatedOrganization);
        return updatedOrganization;
    }

    @Override
    public Organization retrieveOrganization(String id, String fields) {
        return ModelUtils.getCollectionItem(Organization.class, id, fields);
    }

    private void processNestedFields(Organization organization) {
        if (organization.getOrganizationParentRelationship() != null && organization.getOrganizationParentRelationship().getOrganization() != null) {
            var orgReq = organization.getOrganizationParentRelationship().getOrganization();
            var foundOrg = organizationRepository.findById(orgReq.getId()).orElse(null);
            if (foundOrg == null)
                foundOrg = createOrganization(orgReq);
            organization.getOrganizationParentRelationship().setOrganization(organizationMapper.toOrganizationRef(foundOrg));
        }
        if (organization.getOrganizationChildRelationship() != null) {
            for (var orgChildRed : organization.getOrganizationChildRelationship()) {
                var orgReq = orgChildRed.getOrganization();
                var foundOrg = organizationRepository.findById(orgReq.getId()).orElse(null);
                if (foundOrg == null)
                    foundOrg = createOrganization(orgReq);
                orgChildRed.setOrganization(organizationMapper.toOrganizationRef(foundOrg));
            }
        }
    }

}

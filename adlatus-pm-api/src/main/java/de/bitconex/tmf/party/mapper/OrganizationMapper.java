package de.bitconex.tmf.party.mapper;

import de.bitconex.tmf.party.models.Organization;
import de.bitconex.tmf.party.models.OrganizationCreate;
import de.bitconex.tmf.party.models.OrganizationRef;
import de.bitconex.tmf.party.models.OrganizationUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrganizationMapper {
    OrganizationRef toOrganizationRef(Organization organization);

    @Mapping(target = "isDeleted", constant = "false")
    Organization toOrganization(OrganizationCreate organizationCreate);

    Organization toOrganization(OrganizationUpdate dto);

    @Mapping(target = "isDeleted", constant = "false")
    Organization toOrganization(OrganizationRef dto);
}

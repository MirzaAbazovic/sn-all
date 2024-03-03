package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

/**
 * The event data structure
 */
@ApiModel(description = "The event data structure")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OrganizationCreateEventPayload {
    @JsonProperty("organization")
    private Organization organization;

    public OrganizationCreateEventPayload organization(Organization organization) {
        this.organization = organization;
        return this;
    }

    /**
     * Get organization
     *
     * @return organization
     */
    @ApiModelProperty(value = "")

    @Valid

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrganizationCreateEventPayload organizationCreateEventPayload = (OrganizationCreateEventPayload) o;
        return Objects.equals(this.organization, organizationCreateEventPayload.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organization);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrganizationCreateEventPayload {\n");

        sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}


package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Parent references of an organization in a structure of organizations.
 */
@ApiModel(description = "Parent references of an organization in a structure of organizations.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OrganizationParentRelationship {
    @JsonProperty("relationshipType")
    private String relationshipType;

    @JsonProperty("organization")
    @DocumentReference(collection = "organization")
    private OrganizationRef organization;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public OrganizationParentRelationship relationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
        return this;
    }

    /**
     * Type of the relationship. Could be juridical, hierarchical, geographical, functional for example.
     *
     * @return relationshipType
     */
    @ApiModelProperty(value = "Type of the relationship. Could be juridical, hierarchical, geographical, functional for example.")


    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public OrganizationParentRelationship organization(OrganizationRef organization) {
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

    public OrganizationRef getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationRef organization) {
        this.organization = organization;
    }

    public OrganizationParentRelationship atBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class
     *
     * @return atBaseType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the super-class")


    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public OrganizationParentRelationship atSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
        return this;
    }

    /**
     * A URI to a JSON-Schema file that defines additional attributes and relationships
     *
     * @return atSchemaLocation
     */
    @ApiModelProperty(value = "A URI to a JSON-Schema file that defines additional attributes and relationships")

    @Valid

    public URI getAtSchemaLocation() {
        return atSchemaLocation;
    }

    public void setAtSchemaLocation(URI atSchemaLocation) {
        this.atSchemaLocation = atSchemaLocation;
    }

    public OrganizationParentRelationship atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name
     *
     * @return atType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")


    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrganizationParentRelationship organizationParentRelationship = (OrganizationParentRelationship) o;
        return Objects.equals(this.relationshipType, organizationParentRelationship.relationshipType) &&
                Objects.equals(this.organization, organizationParentRelationship.organization) &&
                Objects.equals(this.atBaseType, organizationParentRelationship.atBaseType) &&
                Objects.equals(this.atSchemaLocation, organizationParentRelationship.atSchemaLocation) &&
                Objects.equals(this.atType, organizationParentRelationship.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationshipType, organization, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrganizationParentRelationship {\n");

        sb.append("    relationshipType: ").append(toIndentedString(relationshipType)).append("\n");
        sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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


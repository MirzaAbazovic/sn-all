package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

/**
 * Related Entity reference. A related party defines party or party role linked to a specific entity.
 */
@ApiModel(description = "Related Entity reference. A related party defines party or party role linked to a specific entity.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class RelatedParty {
    @JsonProperty("id")
    private String id;

    @JsonProperty("href")
    private String href;

    @JsonProperty("name")
    private String name;

    @JsonProperty("role")
    private String role;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    @JsonProperty("@referredType")
    private String atReferredType;

    public RelatedParty id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of a related entity.
     *
     * @return id
     */
    @ApiModelProperty(required = true, value = "Unique identifier of a related entity.")
    @NotNull


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelatedParty href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Reference of the related entity.
     *
     * @return href
     */
    @ApiModelProperty(value = "Reference of the related entity.")


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public RelatedParty name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the related entity.
     *
     * @return name
     */
    @ApiModelProperty(value = "Name of the related entity.")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RelatedParty role(String role) {
        this.role = role;
        return this;
    }

    /**
     * Role played by the related party
     *
     * @return role
     */
    @ApiModelProperty(value = "Role played by the related party")


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public RelatedParty atBaseType(String atBaseType) {
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

    public RelatedParty atSchemaLocation(URI atSchemaLocation) {
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

    public RelatedParty atType(String atType) {
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

    public RelatedParty atReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
        return this;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     *
     * @return atReferredType
     */
    @ApiModelProperty(required = true, value = "The actual type of the target instance when needed for disambiguation.")
    @NotNull


    public String getAtReferredType() {
        return atReferredType;
    }

    public void setAtReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RelatedParty relatedParty = (RelatedParty) o;
        return Objects.equals(this.id, relatedParty.id) &&
                Objects.equals(this.href, relatedParty.href) &&
                Objects.equals(this.name, relatedParty.name) &&
                Objects.equals(this.role, relatedParty.role) &&
                Objects.equals(this.atBaseType, relatedParty.atBaseType) &&
                Objects.equals(this.atSchemaLocation, relatedParty.atSchemaLocation) &&
                Objects.equals(this.atType, relatedParty.atType) &&
                Objects.equals(this.atReferredType, relatedParty.atReferredType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, name, role, atBaseType, atSchemaLocation, atType, atReferredType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RelatedParty {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    role: ").append(toIndentedString(role)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
        sb.append("    atReferredType: ").append(toIndentedString(atReferredType)).append("\n");
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


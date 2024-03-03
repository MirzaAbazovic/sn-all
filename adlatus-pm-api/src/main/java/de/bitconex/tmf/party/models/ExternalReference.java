package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * External reference of the individual or reference in other system
 */
@ApiModel(description = "External reference of the individual or reference in other system")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class ExternalReference {
    @JsonProperty("externalReferenceType")
    private String externalReferenceType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public ExternalReference externalReferenceType(String externalReferenceType) {
        this.externalReferenceType = externalReferenceType;
        return this;
    }

    /**
     * Type of the external reference
     *
     * @return externalReferenceType
     */
    @ApiModelProperty(value = "Type of the external reference")


    public String getExternalReferenceType() {
        return externalReferenceType;
    }

    public void setExternalReferenceType(String externalReferenceType) {
        this.externalReferenceType = externalReferenceType;
    }

    public ExternalReference name(String name) {
        this.name = name;
        return this;
    }

    /**
     * External reference name
     *
     * @return name
     */
    @ApiModelProperty(value = "External reference name")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExternalReference atBaseType(String atBaseType) {
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

    public ExternalReference atSchemaLocation(URI atSchemaLocation) {
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

    public ExternalReference atType(String atType) {
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
        ExternalReference externalReference = (ExternalReference) o;
        return Objects.equals(this.externalReferenceType, externalReference.externalReferenceType) &&
                Objects.equals(this.name, externalReference.name) &&
                Objects.equals(this.atBaseType, externalReference.atBaseType) &&
                Objects.equals(this.atSchemaLocation, externalReference.atSchemaLocation) &&
                Objects.equals(this.atType, externalReference.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalReferenceType, name, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExternalReference {\n");

        sb.append("    externalReferenceType: ").append(toIndentedString(externalReferenceType)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
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


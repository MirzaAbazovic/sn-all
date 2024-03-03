package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Indicates the contact medium that could be used to contact the party.
 */
@ApiModel(description = "Indicates the contact medium that could be used to contact the party.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class ContactMedium {
    @JsonProperty("mediumType")
    private String mediumType;

    @JsonProperty("preferred")
    private Boolean preferred;

    @JsonProperty("characteristic")
    private MediumCharacteristic characteristic;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public ContactMedium mediumType(String mediumType) {
        this.mediumType = mediumType;
        return this;
    }

    /**
     * Type of the contact medium, such as: email address, telephone number, postal address
     *
     * @return mediumType
     */
    @ApiModelProperty(value = "Type of the contact medium, such as: email address, telephone number, postal address")


    public String getMediumType() {
        return mediumType;
    }

    public void setMediumType(String mediumType) {
        this.mediumType = mediumType;
    }

    public ContactMedium preferred(Boolean preferred) {
        this.preferred = preferred;
        return this;
    }

    /**
     * If true, indicates that is the preferred contact medium
     *
     * @return preferred
     */
    @ApiModelProperty(value = "If true, indicates that is the preferred contact medium")


    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    public ContactMedium characteristic(MediumCharacteristic characteristic) {
        this.characteristic = characteristic;
        return this;
    }

    /**
     * Get characteristic
     *
     * @return characteristic
     */
    @ApiModelProperty(value = "")

    @Valid

    public MediumCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(MediumCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public ContactMedium validFor(TimePeriod validFor) {
        this.validFor = validFor;
        return this;
    }

    /**
     * Get validFor
     *
     * @return validFor
     */
    @ApiModelProperty(value = "")

    @Valid

    public TimePeriod getValidFor() {
        return validFor;
    }

    public void setValidFor(TimePeriod validFor) {
        this.validFor = validFor;
    }

    public ContactMedium atBaseType(String atBaseType) {
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

    public ContactMedium atSchemaLocation(URI atSchemaLocation) {
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

    public ContactMedium atType(String atType) {
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
        ContactMedium contactMedium = (ContactMedium) o;
        return Objects.equals(this.mediumType, contactMedium.mediumType) &&
                Objects.equals(this.preferred, contactMedium.preferred) &&
                Objects.equals(this.characteristic, contactMedium.characteristic) &&
                Objects.equals(this.validFor, contactMedium.validFor) &&
                Objects.equals(this.atBaseType, contactMedium.atBaseType) &&
                Objects.equals(this.atSchemaLocation, contactMedium.atSchemaLocation) &&
                Objects.equals(this.atType, contactMedium.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediumType, preferred, characteristic, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ContactMedium {\n");

        sb.append("    mediumType: ").append(toIndentedString(mediumType)).append("\n");
        sb.append("    preferred: ").append(toIndentedString(preferred)).append("\n");
        sb.append("    characteristic: ").append(toIndentedString(characteristic)).append("\n");
        sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
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


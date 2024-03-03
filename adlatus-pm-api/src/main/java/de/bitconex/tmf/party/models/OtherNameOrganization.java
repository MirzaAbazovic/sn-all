package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Keeps track of other names, for example the old name of an organization.
 */
@ApiModel(description = "Keeps track of other names, for example the old name of an organization.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OtherNameOrganization {
    @JsonProperty("name")
    private String name;

    @JsonProperty("nameType")
    private String nameType;

    @JsonProperty("tradingName")
    private String tradingName;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public OtherNameOrganization name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Organization name (department name for example)
     *
     * @return name
     */
    @ApiModelProperty(value = "Organization name (department name for example)")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OtherNameOrganization nameType(String nameType) {
        this.nameType = nameType;
        return this;
    }

    /**
     * Co. , Inc. , Ltd. , Pty Ltd. , Plc; , Gmbh
     *
     * @return nameType
     */
    @ApiModelProperty(value = "Co. , Inc. , Ltd. , Pty Ltd. , Plc; , Gmbh")


    public String getNameType() {
        return nameType;
    }

    public void setNameType(String nameType) {
        this.nameType = nameType;
    }

    public OtherNameOrganization tradingName(String tradingName) {
        this.tradingName = tradingName;
        return this;
    }

    /**
     * The name that the organization trades under
     *
     * @return tradingName
     */
    @ApiModelProperty(value = "The name that the organization trades under")


    public String getTradingName() {
        return tradingName;
    }

    public void setTradingName(String tradingName) {
        this.tradingName = tradingName;
    }

    public OtherNameOrganization validFor(TimePeriod validFor) {
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

    public OtherNameOrganization atBaseType(String atBaseType) {
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

    public OtherNameOrganization atSchemaLocation(URI atSchemaLocation) {
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

    public OtherNameOrganization atType(String atType) {
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
        OtherNameOrganization otherNameOrganization = (OtherNameOrganization) o;
        return Objects.equals(this.name, otherNameOrganization.name) &&
                Objects.equals(this.nameType, otherNameOrganization.nameType) &&
                Objects.equals(this.tradingName, otherNameOrganization.tradingName) &&
                Objects.equals(this.validFor, otherNameOrganization.validFor) &&
                Objects.equals(this.atBaseType, otherNameOrganization.atBaseType) &&
                Objects.equals(this.atSchemaLocation, otherNameOrganization.atSchemaLocation) &&
                Objects.equals(this.atType, otherNameOrganization.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nameType, tradingName, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OtherNameOrganization {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    nameType: ").append(toIndentedString(nameType)).append("\n");
        sb.append("    tradingName: ").append(toIndentedString(tradingName)).append("\n");
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


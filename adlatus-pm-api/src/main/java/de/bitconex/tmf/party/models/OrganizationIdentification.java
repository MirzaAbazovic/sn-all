package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents our registration of information used as proof of identity by an organization
 */
@ApiModel(description = "Represents our registration of information used as proof of identity by an organization")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OrganizationIdentification {
    @JsonProperty("identificationId")
    private String identificationId;

    @JsonProperty("identificationType")
    private String identificationType;

    @JsonProperty("issuingAuthority")
    private String issuingAuthority;

    @JsonProperty("issuingDate")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime issuingDate;

    @JsonProperty("attachment")
    private AttachmentRefOrValue attachment;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public OrganizationIdentification identificationId(String identificationId) {
        this.identificationId = identificationId;
        return this;
    }

    /**
     * Identifier
     *
     * @return identificationId
     */
    @ApiModelProperty(value = "Identifier")


    public String getIdentificationId() {
        return identificationId;
    }

    public void setIdentificationId(String identificationId) {
        this.identificationId = identificationId;
    }

    public OrganizationIdentification identificationType(String identificationType) {
        this.identificationType = identificationType;
        return this;
    }

    /**
     * Type of identification information used to identify the company in a country or internationally
     *
     * @return identificationType
     */
    @ApiModelProperty(value = "Type of identification information used to identify the company in a country or internationally")


    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public OrganizationIdentification issuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
        return this;
    }

    /**
     * Authority which has issued the identifier (chamber of commerce...)
     *
     * @return issuingAuthority
     */
    @ApiModelProperty(value = "Authority which has issued the identifier (chamber of commerce...)")


    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public OrganizationIdentification issuingDate(OffsetDateTime issuingDate) {
        this.issuingDate = issuingDate;
        return this;
    }

    /**
     * Date at which the identifier was issued
     *
     * @return issuingDate
     */
    @ApiModelProperty(value = "Date at which the identifier was issued")

    @Valid

    public OffsetDateTime getIssuingDate() {
        return issuingDate;
    }

    public void setIssuingDate(OffsetDateTime issuingDate) {
        this.issuingDate = issuingDate;
    }

    public OrganizationIdentification attachment(AttachmentRefOrValue attachment) {
        this.attachment = attachment;
        return this;
    }

    /**
     * Get attachment
     *
     * @return attachment
     */
    @ApiModelProperty(value = "")

    @Valid

    public AttachmentRefOrValue getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentRefOrValue attachment) {
        this.attachment = attachment;
    }

    public OrganizationIdentification validFor(TimePeriod validFor) {
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

    public OrganizationIdentification atBaseType(String atBaseType) {
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

    public OrganizationIdentification atSchemaLocation(URI atSchemaLocation) {
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

    public OrganizationIdentification atType(String atType) {
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
        OrganizationIdentification organizationIdentification = (OrganizationIdentification) o;
        return Objects.equals(this.identificationId, organizationIdentification.identificationId) &&
                Objects.equals(this.identificationType, organizationIdentification.identificationType) &&
                Objects.equals(this.issuingAuthority, organizationIdentification.issuingAuthority) &&
                Objects.equals(this.issuingDate, organizationIdentification.issuingDate) &&
                Objects.equals(this.attachment, organizationIdentification.attachment) &&
                Objects.equals(this.validFor, organizationIdentification.validFor) &&
                Objects.equals(this.atBaseType, organizationIdentification.atBaseType) &&
                Objects.equals(this.atSchemaLocation, organizationIdentification.atSchemaLocation) &&
                Objects.equals(this.atType, organizationIdentification.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificationId, identificationType, issuingAuthority, issuingDate, attachment, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrganizationIdentification {\n");

        sb.append("    identificationId: ").append(toIndentedString(identificationId)).append("\n");
        sb.append("    identificationType: ").append(toIndentedString(identificationType)).append("\n");
        sb.append("    issuingAuthority: ").append(toIndentedString(issuingAuthority)).append("\n");
        sb.append("    issuingDate: ").append(toIndentedString(issuingDate)).append("\n");
        sb.append("    attachment: ").append(toIndentedString(attachment)).append("\n");
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


package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A tax exemption certificate represents a tax exemption granted to a party (individual or organization) by a tax jurisdiction which may be a city, state, country,... An exemption has a certificate identifier (received from the jurisdiction that levied the tax) and a validity period. An exemption is per tax types and determines for each type of tax what portion of the tax is exempted (partial by percentage or complete) via the tax definition.
 */
@ApiModel(description = "A tax exemption certificate represents a tax exemption granted to a party (individual or organization) by a tax jurisdiction which may be a city, state, country,... An exemption has a certificate identifier (received from the jurisdiction that levied the tax) and a validity period. An exemption is per tax types and determines for each type of tax what portion of the tax is exempted (partial by percentage or complete) via the tax definition.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class TaxExemptionCertificate {
    @JsonProperty("id")
    private String id;

    @JsonProperty("attachment")
    private AttachmentRefOrValue attachment;

    @JsonProperty("taxDefinition")
    @Valid
    private List<TaxDefinition> taxDefinition = null;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public TaxExemptionCertificate id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the certificate of the tax exemption
     *
     * @return id
     */
    @ApiModelProperty(value = "Unique identifier of the certificate of the tax exemption")


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaxExemptionCertificate attachment(AttachmentRefOrValue attachment) {
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

    public TaxExemptionCertificate taxDefinition(List<TaxDefinition> taxDefinition) {
        this.taxDefinition = taxDefinition;
        return this;
    }

    public TaxExemptionCertificate addTaxDefinitionItem(TaxDefinition taxDefinitionItem) {
        if (this.taxDefinition == null) {
            this.taxDefinition = new ArrayList<>();
        }
        this.taxDefinition.add(taxDefinitionItem);
        return this;
    }

    /**
     * Get taxDefinition
     *
     * @return taxDefinition
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<TaxDefinition> getTaxDefinition() {
        return taxDefinition;
    }

    public void setTaxDefinition(List<TaxDefinition> taxDefinition) {
        this.taxDefinition = taxDefinition;
    }

    public TaxExemptionCertificate validFor(TimePeriod validFor) {
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

    public TaxExemptionCertificate atBaseType(String atBaseType) {
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

    public TaxExemptionCertificate atSchemaLocation(URI atSchemaLocation) {
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

    public TaxExemptionCertificate atType(String atType) {
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
        TaxExemptionCertificate taxExemptionCertificate = (TaxExemptionCertificate) o;
        return Objects.equals(this.id, taxExemptionCertificate.id) &&
                Objects.equals(this.attachment, taxExemptionCertificate.attachment) &&
                Objects.equals(this.taxDefinition, taxExemptionCertificate.taxDefinition) &&
                Objects.equals(this.validFor, taxExemptionCertificate.validFor) &&
                Objects.equals(this.atBaseType, taxExemptionCertificate.atBaseType) &&
                Objects.equals(this.atSchemaLocation, taxExemptionCertificate.atSchemaLocation) &&
                Objects.equals(this.atType, taxExemptionCertificate.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, attachment, taxDefinition, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaxExemptionCertificate {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    attachment: ").append(toIndentedString(attachment)).append("\n");
        sb.append("    taxDefinition: ").append(toIndentedString(taxDefinition)).append("\n");
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


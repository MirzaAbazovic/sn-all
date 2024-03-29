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
 * Generic Party structure used to define commonalities between sub concepts of Individual and Organization.
 */
@ApiModel(description = "Generic Party structure used to define commonalities between sub concepts of Individual and Organization.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class Party {
    @JsonProperty("id")
    private String id;

    @JsonProperty("href")
    private String href;

    @JsonProperty("contactMedium")
    @Valid
    private List<ContactMedium> contactMedium = null;

    @JsonProperty("creditRating")
    @Valid
    private List<PartyCreditProfile> creditRating = null;

    @JsonProperty("externalReference")
    @Valid
    private List<ExternalReference> externalReference = null;

    @JsonProperty("partyCharacteristic")
    @Valid
    private List<Characteristic> partyCharacteristic = null;

    @JsonProperty("relatedParty")
    @Valid
    private List<RelatedParty> relatedParty = null;

    @JsonProperty("taxExemptionCertificate")
    @Valid
    private List<TaxExemptionCertificate> taxExemptionCertificate = null;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public Party id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the organization
     *
     * @return id
     */
    @ApiModelProperty(value = "Unique identifier of the organization")


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Party href(String href) {
        this.href = href;
        return this;
    }

    /**
     * Hyperlink to access the organization
     *
     * @return href
     */
    @ApiModelProperty(value = "Hyperlink to access the organization")


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Party contactMedium(List<ContactMedium> contactMedium) {
        this.contactMedium = contactMedium;
        return this;
    }

    public Party addContactMediumItem(ContactMedium contactMediumItem) {
        if (this.contactMedium == null) {
            this.contactMedium = new ArrayList<>();
        }
        this.contactMedium.add(contactMediumItem);
        return this;
    }

    /**
     * Get contactMedium
     *
     * @return contactMedium
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<ContactMedium> getContactMedium() {
        return contactMedium;
    }

    public void setContactMedium(List<ContactMedium> contactMedium) {
        this.contactMedium = contactMedium;
    }

    public Party creditRating(List<PartyCreditProfile> creditRating) {
        this.creditRating = creditRating;
        return this;
    }

    public Party addCreditRatingItem(PartyCreditProfile creditRatingItem) {
        if (this.creditRating == null) {
            this.creditRating = new ArrayList<>();
        }
        this.creditRating.add(creditRatingItem);
        return this;
    }

    /**
     * Get creditRating
     *
     * @return creditRating
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<PartyCreditProfile> getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(List<PartyCreditProfile> creditRating) {
        this.creditRating = creditRating;
    }

    public Party externalReference(List<ExternalReference> externalReference) {
        this.externalReference = externalReference;
        return this;
    }

    public Party addExternalReferenceItem(ExternalReference externalReferenceItem) {
        if (this.externalReference == null) {
            this.externalReference = new ArrayList<>();
        }
        this.externalReference.add(externalReferenceItem);
        return this;
    }

    /**
     * Get externalReference
     *
     * @return externalReference
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<ExternalReference> getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(List<ExternalReference> externalReference) {
        this.externalReference = externalReference;
    }

    public Party partyCharacteristic(List<Characteristic> partyCharacteristic) {
        this.partyCharacteristic = partyCharacteristic;
        return this;
    }

    public Party addPartyCharacteristicItem(Characteristic partyCharacteristicItem) {
        if (this.partyCharacteristic == null) {
            this.partyCharacteristic = new ArrayList<>();
        }
        this.partyCharacteristic.add(partyCharacteristicItem);
        return this;
    }

    /**
     * Get partyCharacteristic
     *
     * @return partyCharacteristic
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<Characteristic> getPartyCharacteristic() {
        return partyCharacteristic;
    }

    public void setPartyCharacteristic(List<Characteristic> partyCharacteristic) {
        this.partyCharacteristic = partyCharacteristic;
    }

    public Party relatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
        return this;
    }

    public Party addRelatedPartyItem(RelatedParty relatedPartyItem) {
        if (this.relatedParty == null) {
            this.relatedParty = new ArrayList<>();
        }
        this.relatedParty.add(relatedPartyItem);
        return this;
    }

    /**
     * Get relatedParty
     *
     * @return relatedParty
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<RelatedParty> getRelatedParty() {
        return relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    public Party taxExemptionCertificate(List<TaxExemptionCertificate> taxExemptionCertificate) {
        this.taxExemptionCertificate = taxExemptionCertificate;
        return this;
    }

    public Party addTaxExemptionCertificateItem(TaxExemptionCertificate taxExemptionCertificateItem) {
        if (this.taxExemptionCertificate == null) {
            this.taxExemptionCertificate = new ArrayList<>();
        }
        this.taxExemptionCertificate.add(taxExemptionCertificateItem);
        return this;
    }

    /**
     * Get taxExemptionCertificate
     *
     * @return taxExemptionCertificate
     */
    @ApiModelProperty(value = "")

    @Valid

    public List<TaxExemptionCertificate> getTaxExemptionCertificate() {
        return taxExemptionCertificate;
    }

    public void setTaxExemptionCertificate(List<TaxExemptionCertificate> taxExemptionCertificate) {
        this.taxExemptionCertificate = taxExemptionCertificate;
    }

    public Party atBaseType(String atBaseType) {
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

    public Party atSchemaLocation(URI atSchemaLocation) {
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

    public Party atType(String atType) {
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
        Party party = (Party) o;
        return Objects.equals(this.id, party.id) &&
                Objects.equals(this.href, party.href) &&
                Objects.equals(this.contactMedium, party.contactMedium) &&
                Objects.equals(this.creditRating, party.creditRating) &&
                Objects.equals(this.externalReference, party.externalReference) &&
                Objects.equals(this.partyCharacteristic, party.partyCharacteristic) &&
                Objects.equals(this.relatedParty, party.relatedParty) &&
                Objects.equals(this.taxExemptionCertificate, party.taxExemptionCertificate) &&
                Objects.equals(this.atBaseType, party.atBaseType) &&
                Objects.equals(this.atSchemaLocation, party.atSchemaLocation) &&
                Objects.equals(this.atType, party.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, contactMedium, creditRating, externalReference, partyCharacteristic, relatedParty, taxExemptionCertificate, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Party {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    contactMedium: ").append(toIndentedString(contactMedium)).append("\n");
        sb.append("    creditRating: ").append(toIndentedString(creditRating)).append("\n");
        sb.append("    externalReference: ").append(toIndentedString(externalReference)).append("\n");
        sb.append("    partyCharacteristic: ").append(toIndentedString(partyCharacteristic)).append("\n");
        sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
        sb.append("    taxExemptionCertificate: ").append(toIndentedString(taxExemptionCertificate)).append("\n");
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


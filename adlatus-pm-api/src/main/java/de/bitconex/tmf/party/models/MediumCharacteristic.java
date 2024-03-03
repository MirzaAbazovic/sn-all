package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Describes the contact medium characteristics that could be used to contact a party (an individual or an organization)
 */
@ApiModel(description = "Describes the contact medium characteristics that could be used to contact a party (an individual or an organization)")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class MediumCharacteristic {
    @JsonProperty("city")
    private String city;

    @JsonProperty("contactType")
    private String contactType;

    @JsonProperty("country")
    private String country;

    @JsonProperty("emailAddress")
    private String emailAddress;

    @JsonProperty("faxNumber")
    private String faxNumber;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("postCode")
    private String postCode;

    @JsonProperty("socialNetworkId")
    private String socialNetworkId;

    @JsonProperty("stateOrProvince")
    private String stateOrProvince;

    @JsonProperty("street1")
    private String street1;

    @JsonProperty("street2")
    private String street2;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public MediumCharacteristic city(String city) {
        this.city = city;
        return this;
    }

    /**
     * The city
     *
     * @return city
     */
    @ApiModelProperty(value = "The city")


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public MediumCharacteristic contactType(String contactType) {
        this.contactType = contactType;
        return this;
    }

    /**
     * The type of contact, for example: phone number such as mobile, fixed home, fixed office. postal address such as shipping instalation…
     *
     * @return contactType
     */
    @ApiModelProperty(value = "The type of contact, for example: phone number such as mobile, fixed home, fixed office. postal address such as shipping instalation…")


    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public MediumCharacteristic country(String country) {
        this.country = country;
        return this;
    }

    /**
     * The country
     *
     * @return country
     */
    @ApiModelProperty(value = "The country")


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public MediumCharacteristic emailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    /**
     * Full email address in standard format
     *
     * @return emailAddress
     */
    @ApiModelProperty(value = "Full email address in standard format")


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public MediumCharacteristic faxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
        return this;
    }

    /**
     * The fax number of the contact
     *
     * @return faxNumber
     */
    @ApiModelProperty(value = "The fax number of the contact")


    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public MediumCharacteristic phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    /**
     * The primary phone number of the contact
     *
     * @return phoneNumber
     */
    @ApiModelProperty(value = "The primary phone number of the contact")


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MediumCharacteristic postCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    /**
     * Postcode
     *
     * @return postCode
     */
    @ApiModelProperty(value = "Postcode")


    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public MediumCharacteristic socialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
        return this;
    }

    /**
     * Identifier as a member of a social network
     *
     * @return socialNetworkId
     */
    @ApiModelProperty(value = "Identifier as a member of a social network")


    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
    }

    public MediumCharacteristic stateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
        return this;
    }

    /**
     * State or province
     *
     * @return stateOrProvince
     */
    @ApiModelProperty(value = "State or province")


    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public MediumCharacteristic street1(String street1) {
        this.street1 = street1;
        return this;
    }

    /**
     * Describes the street
     *
     * @return street1
     */
    @ApiModelProperty(value = "Describes the street")


    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public MediumCharacteristic street2(String street2) {
        this.street2 = street2;
        return this;
    }

    /**
     * Complementary street description
     *
     * @return street2
     */
    @ApiModelProperty(value = "Complementary street description")


    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public MediumCharacteristic atBaseType(String atBaseType) {
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

    public MediumCharacteristic atSchemaLocation(URI atSchemaLocation) {
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

    public MediumCharacteristic atType(String atType) {
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
        MediumCharacteristic mediumCharacteristic = (MediumCharacteristic) o;
        return Objects.equals(this.city, mediumCharacteristic.city) &&
                Objects.equals(this.contactType, mediumCharacteristic.contactType) &&
                Objects.equals(this.country, mediumCharacteristic.country) &&
                Objects.equals(this.emailAddress, mediumCharacteristic.emailAddress) &&
                Objects.equals(this.faxNumber, mediumCharacteristic.faxNumber) &&
                Objects.equals(this.phoneNumber, mediumCharacteristic.phoneNumber) &&
                Objects.equals(this.postCode, mediumCharacteristic.postCode) &&
                Objects.equals(this.socialNetworkId, mediumCharacteristic.socialNetworkId) &&
                Objects.equals(this.stateOrProvince, mediumCharacteristic.stateOrProvince) &&
                Objects.equals(this.street1, mediumCharacteristic.street1) &&
                Objects.equals(this.street2, mediumCharacteristic.street2) &&
                Objects.equals(this.atBaseType, mediumCharacteristic.atBaseType) &&
                Objects.equals(this.atSchemaLocation, mediumCharacteristic.atSchemaLocation) &&
                Objects.equals(this.atType, mediumCharacteristic.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, contactType, country, emailAddress, faxNumber, phoneNumber, postCode, socialNetworkId, stateOrProvince, street1, street2, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class MediumCharacteristic {\n");

        sb.append("    city: ").append(toIndentedString(city)).append("\n");
        sb.append("    contactType: ").append(toIndentedString(contactType)).append("\n");
        sb.append("    country: ").append(toIndentedString(country)).append("\n");
        sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
        sb.append("    faxNumber: ").append(toIndentedString(faxNumber)).append("\n");
        sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
        sb.append("    postCode: ").append(toIndentedString(postCode)).append("\n");
        sb.append("    socialNetworkId: ").append(toIndentedString(socialNetworkId)).append("\n");
        sb.append("    stateOrProvince: ").append(toIndentedString(stateOrProvince)).append("\n");
        sb.append("    street1: ").append(toIndentedString(street1)).append("\n");
        sb.append("    street2: ").append(toIndentedString(street2)).append("\n");
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


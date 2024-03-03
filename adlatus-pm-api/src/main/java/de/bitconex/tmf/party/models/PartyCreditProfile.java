package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * An individual might be evaluated for its worthiness and this evaluation might be based on a credit rating given by a credit agency.
 */
@ApiModel(description = "An individual might be evaluated for its worthiness and this evaluation might be based on a credit rating given by a credit agency.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class PartyCreditProfile {
    @JsonProperty("creditAgencyName")
    private String creditAgencyName;

    @JsonProperty("creditAgencyType")
    private String creditAgencyType;

    @JsonProperty("ratingReference")
    private String ratingReference;

    @JsonProperty("ratingScore")
    private Integer ratingScore;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public PartyCreditProfile creditAgencyName(String creditAgencyName) {
        this.creditAgencyName = creditAgencyName;
        return this;
    }

    /**
     * Name of the credit agency giving the score
     *
     * @return creditAgencyName
     */
    @ApiModelProperty(value = "Name of the credit agency giving the score")


    public String getCreditAgencyName() {
        return creditAgencyName;
    }

    public void setCreditAgencyName(String creditAgencyName) {
        this.creditAgencyName = creditAgencyName;
    }

    public PartyCreditProfile creditAgencyType(String creditAgencyType) {
        this.creditAgencyType = creditAgencyType;
        return this;
    }

    /**
     * Type of the credit agency giving the score
     *
     * @return creditAgencyType
     */
    @ApiModelProperty(value = "Type of the credit agency giving the score")


    public String getCreditAgencyType() {
        return creditAgencyType;
    }

    public void setCreditAgencyType(String creditAgencyType) {
        this.creditAgencyType = creditAgencyType;
    }

    public PartyCreditProfile ratingReference(String ratingReference) {
        this.ratingReference = ratingReference;
        return this;
    }

    /**
     * Reference corresponding to the credit rating
     *
     * @return ratingReference
     */
    @ApiModelProperty(value = "Reference corresponding to the credit rating")


    public String getRatingReference() {
        return ratingReference;
    }

    public void setRatingReference(String ratingReference) {
        this.ratingReference = ratingReference;
    }

    public PartyCreditProfile ratingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
        return this;
    }

    /**
     * A measure of a party’s creditworthiness calculated on the basis of a combination of factors such as their income and credit history
     *
     * @return ratingScore
     */
    @ApiModelProperty(value = "A measure of a party’s creditworthiness calculated on the basis of a combination of factors such as their income and credit history")


    public Integer getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }

    public PartyCreditProfile validFor(TimePeriod validFor) {
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

    public PartyCreditProfile atBaseType(String atBaseType) {
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

    public PartyCreditProfile atSchemaLocation(URI atSchemaLocation) {
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

    public PartyCreditProfile atType(String atType) {
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
        PartyCreditProfile partyCreditProfile = (PartyCreditProfile) o;
        return Objects.equals(this.creditAgencyName, partyCreditProfile.creditAgencyName) &&
                Objects.equals(this.creditAgencyType, partyCreditProfile.creditAgencyType) &&
                Objects.equals(this.ratingReference, partyCreditProfile.ratingReference) &&
                Objects.equals(this.ratingScore, partyCreditProfile.ratingScore) &&
                Objects.equals(this.validFor, partyCreditProfile.validFor) &&
                Objects.equals(this.atBaseType, partyCreditProfile.atBaseType) &&
                Objects.equals(this.atSchemaLocation, partyCreditProfile.atSchemaLocation) &&
                Objects.equals(this.atType, partyCreditProfile.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditAgencyName, creditAgencyType, ratingReference, ratingScore, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PartyCreditProfile {\n");

        sb.append("    creditAgencyName: ").append(toIndentedString(creditAgencyName)).append("\n");
        sb.append("    creditAgencyType: ").append(toIndentedString(creditAgencyType)).append("\n");
        sb.append("    ratingReference: ").append(toIndentedString(ratingReference)).append("\n");
        sb.append("    ratingScore: ").append(toIndentedString(ratingScore)).append("\n");
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


package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Keeps track of other names, for example the old name of a woman before marriage or an artist name.
 */
@ApiModel(description = "Keeps track of other names, for example the old name of a woman before marriage or an artist name.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class OtherNameIndividual {
    @JsonProperty("aristocraticTitle")
    private String aristocraticTitle;

    @JsonProperty("familyName")
    private String familyName;

    @JsonProperty("familyNamePrefix")
    private String familyNamePrefix;

    @JsonProperty("formattedName")
    private String formattedName;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("generation")
    private String generation;

    @JsonProperty("givenName")
    private String givenName;

    @JsonProperty("legalName")
    private String legalName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("preferredGivenName")
    private String preferredGivenName;

    @JsonProperty("title")
    private String title;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public OtherNameIndividual aristocraticTitle(String aristocraticTitle) {
        this.aristocraticTitle = aristocraticTitle;
        return this;
    }

    /**
     * e.g. Baron, Graf, Earl,…
     *
     * @return aristocraticTitle
     */
    @ApiModelProperty(value = "e.g. Baron, Graf, Earl,…")


    public String getAristocraticTitle() {
        return aristocraticTitle;
    }

    public void setAristocraticTitle(String aristocraticTitle) {
        this.aristocraticTitle = aristocraticTitle;
    }

    public OtherNameIndividual familyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    /**
     * Contains the non-chosen or inherited name. Also known as last name in the Western context
     *
     * @return familyName
     */
    @ApiModelProperty(value = "Contains the non-chosen or inherited name. Also known as last name in the Western context")


    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public OtherNameIndividual familyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
        return this;
    }

    /**
     * Family name prefix
     *
     * @return familyNamePrefix
     */
    @ApiModelProperty(value = "Family name prefix")


    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    public OtherNameIndividual formattedName(String formattedName) {
        this.formattedName = formattedName;
        return this;
    }

    /**
     * . A fully formatted name in one string with all of its pieces in their proper place and all of the necessary punctuation. Useful for specific contexts (Chinese, Japanese, Korean,…)
     *
     * @return formattedName
     */
    @ApiModelProperty(value = ". A fully formatted name in one string with all of its pieces in their proper place and all of the necessary punctuation. Useful for specific contexts (Chinese, Japanese, Korean,…)")


    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    public OtherNameIndividual fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    /**
     * Full name flatten (first, middle, and last names)
     *
     * @return fullName
     */
    @ApiModelProperty(value = "Full name flatten (first, middle, and last names)")


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public OtherNameIndividual generation(String generation) {
        this.generation = generation;
        return this;
    }

    /**
     * e.g. Sr, Jr…
     *
     * @return generation
     */
    @ApiModelProperty(value = "e.g. Sr, Jr…")


    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public OtherNameIndividual givenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    /**
     * First name
     *
     * @return givenName
     */
    @ApiModelProperty(value = "First name")


    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public OtherNameIndividual legalName(String legalName) {
        this.legalName = legalName;
        return this;
    }

    /**
     * Legal name or birth name (name one has for official purposes)
     *
     * @return legalName
     */
    @ApiModelProperty(value = "Legal name or birth name (name one has for official purposes)")


    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public OtherNameIndividual middleName(String middleName) {
        this.middleName = middleName;
        return this;
    }

    /**
     * Middle name or initial
     *
     * @return middleName
     */
    @ApiModelProperty(value = "Middle name or initial")


    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public OtherNameIndividual preferredGivenName(String preferredGivenName) {
        this.preferredGivenName = preferredGivenName;
        return this;
    }

    /**
     * Contains the chosen name by which the person prefers to be addressed. Note: This name may be a name other than a given name, such as a nickname
     *
     * @return preferredGivenName
     */
    @ApiModelProperty(value = "Contains the chosen name by which the person prefers to be addressed. Note: This name may be a name other than a given name, such as a nickname")


    public String getPreferredGivenName() {
        return preferredGivenName;
    }

    public void setPreferredGivenName(String preferredGivenName) {
        this.preferredGivenName = preferredGivenName;
    }

    public OtherNameIndividual title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Use for titles (aristrocatic, social, ...): Pr, Dr, Sir,....
     *
     * @return title
     */
    @ApiModelProperty(value = "Use for titles (aristrocatic, social, ...): Pr, Dr, Sir,....")


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OtherNameIndividual validFor(TimePeriod validFor) {
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

    public OtherNameIndividual atBaseType(String atBaseType) {
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

    public OtherNameIndividual atSchemaLocation(URI atSchemaLocation) {
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

    public OtherNameIndividual atType(String atType) {
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
        OtherNameIndividual otherNameIndividual = (OtherNameIndividual) o;
        return Objects.equals(this.aristocraticTitle, otherNameIndividual.aristocraticTitle) &&
                Objects.equals(this.familyName, otherNameIndividual.familyName) &&
                Objects.equals(this.familyNamePrefix, otherNameIndividual.familyNamePrefix) &&
                Objects.equals(this.formattedName, otherNameIndividual.formattedName) &&
                Objects.equals(this.fullName, otherNameIndividual.fullName) &&
                Objects.equals(this.generation, otherNameIndividual.generation) &&
                Objects.equals(this.givenName, otherNameIndividual.givenName) &&
                Objects.equals(this.legalName, otherNameIndividual.legalName) &&
                Objects.equals(this.middleName, otherNameIndividual.middleName) &&
                Objects.equals(this.preferredGivenName, otherNameIndividual.preferredGivenName) &&
                Objects.equals(this.title, otherNameIndividual.title) &&
                Objects.equals(this.validFor, otherNameIndividual.validFor) &&
                Objects.equals(this.atBaseType, otherNameIndividual.atBaseType) &&
                Objects.equals(this.atSchemaLocation, otherNameIndividual.atSchemaLocation) &&
                Objects.equals(this.atType, otherNameIndividual.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aristocraticTitle, familyName, familyNamePrefix, formattedName, fullName, generation, givenName, legalName, middleName, preferredGivenName, title, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OtherNameIndividual {\n");

        sb.append("    aristocraticTitle: ").append(toIndentedString(aristocraticTitle)).append("\n");
        sb.append("    familyName: ").append(toIndentedString(familyName)).append("\n");
        sb.append("    familyNamePrefix: ").append(toIndentedString(familyNamePrefix)).append("\n");
        sb.append("    formattedName: ").append(toIndentedString(formattedName)).append("\n");
        sb.append("    fullName: ").append(toIndentedString(fullName)).append("\n");
        sb.append("    generation: ").append(toIndentedString(generation)).append("\n");
        sb.append("    givenName: ").append(toIndentedString(givenName)).append("\n");
        sb.append("    legalName: ").append(toIndentedString(legalName)).append("\n");
        sb.append("    middleName: ").append(toIndentedString(middleName)).append("\n");
        sb.append("    preferredGivenName: ").append(toIndentedString(preferredGivenName)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
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


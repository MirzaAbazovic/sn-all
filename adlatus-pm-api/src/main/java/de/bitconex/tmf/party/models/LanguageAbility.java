package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Ability of an individual to understand or converse in a language.
 */
@ApiModel(description = "Ability of an individual to understand or converse in a language.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class LanguageAbility {
    @JsonProperty("isFavouriteLanguage")
    private Boolean isFavouriteLanguage;

    @JsonProperty("languageCode")
    private String languageCode;

    @JsonProperty("languageName")
    private String languageName;

    @JsonProperty("listeningProficiency")
    private String listeningProficiency;

    @JsonProperty("readingProficiency")
    private String readingProficiency;

    @JsonProperty("speakingProficiency")
    private String speakingProficiency;

    @JsonProperty("writingProficiency")
    private String writingProficiency;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public LanguageAbility isFavouriteLanguage(Boolean isFavouriteLanguage) {
        this.isFavouriteLanguage = isFavouriteLanguage;
        return this;
    }

    /**
     * A “true” value specifies whether the language is considered by the individual as his favourite one
     *
     * @return isFavouriteLanguage
     */
    @ApiModelProperty(value = "A “true” value specifies whether the language is considered by the individual as his favourite one")


    public Boolean getIsFavouriteLanguage() {
        return isFavouriteLanguage;
    }

    public void setIsFavouriteLanguage(Boolean isFavouriteLanguage) {
        this.isFavouriteLanguage = isFavouriteLanguage;
    }

    public LanguageAbility languageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }

    /**
     * Language code (RFC 5646)
     *
     * @return languageCode
     */
    @ApiModelProperty(value = "Language code (RFC 5646)")


    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public LanguageAbility languageName(String languageName) {
        this.languageName = languageName;
        return this;
    }

    /**
     * Language name
     *
     * @return languageName
     */
    @ApiModelProperty(value = "Language name")


    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public LanguageAbility listeningProficiency(String listeningProficiency) {
        this.listeningProficiency = listeningProficiency;
        return this;
    }

    /**
     * Listening proficiency evaluated for this language
     *
     * @return listeningProficiency
     */
    @ApiModelProperty(value = "Listening proficiency evaluated for this language")


    public String getListeningProficiency() {
        return listeningProficiency;
    }

    public void setListeningProficiency(String listeningProficiency) {
        this.listeningProficiency = listeningProficiency;
    }

    public LanguageAbility readingProficiency(String readingProficiency) {
        this.readingProficiency = readingProficiency;
        return this;
    }

    /**
     * Reading proficiency evaluated for this language
     *
     * @return readingProficiency
     */
    @ApiModelProperty(value = "Reading proficiency evaluated for this language")


    public String getReadingProficiency() {
        return readingProficiency;
    }

    public void setReadingProficiency(String readingProficiency) {
        this.readingProficiency = readingProficiency;
    }

    public LanguageAbility speakingProficiency(String speakingProficiency) {
        this.speakingProficiency = speakingProficiency;
        return this;
    }

    /**
     * Speaking proficiency evaluated for this language
     *
     * @return speakingProficiency
     */
    @ApiModelProperty(value = "Speaking proficiency evaluated for this language")


    public String getSpeakingProficiency() {
        return speakingProficiency;
    }

    public void setSpeakingProficiency(String speakingProficiency) {
        this.speakingProficiency = speakingProficiency;
    }

    public LanguageAbility writingProficiency(String writingProficiency) {
        this.writingProficiency = writingProficiency;
        return this;
    }

    /**
     * Writing proficiency evaluated for this language
     *
     * @return writingProficiency
     */
    @ApiModelProperty(value = "Writing proficiency evaluated for this language")


    public String getWritingProficiency() {
        return writingProficiency;
    }

    public void setWritingProficiency(String writingProficiency) {
        this.writingProficiency = writingProficiency;
    }

    public LanguageAbility validFor(TimePeriod validFor) {
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

    public LanguageAbility atBaseType(String atBaseType) {
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

    public LanguageAbility atSchemaLocation(URI atSchemaLocation) {
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

    public LanguageAbility atType(String atType) {
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
        LanguageAbility languageAbility = (LanguageAbility) o;
        return Objects.equals(this.isFavouriteLanguage, languageAbility.isFavouriteLanguage) &&
                Objects.equals(this.languageCode, languageAbility.languageCode) &&
                Objects.equals(this.languageName, languageAbility.languageName) &&
                Objects.equals(this.listeningProficiency, languageAbility.listeningProficiency) &&
                Objects.equals(this.readingProficiency, languageAbility.readingProficiency) &&
                Objects.equals(this.speakingProficiency, languageAbility.speakingProficiency) &&
                Objects.equals(this.writingProficiency, languageAbility.writingProficiency) &&
                Objects.equals(this.validFor, languageAbility.validFor) &&
                Objects.equals(this.atBaseType, languageAbility.atBaseType) &&
                Objects.equals(this.atSchemaLocation, languageAbility.atSchemaLocation) &&
                Objects.equals(this.atType, languageAbility.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFavouriteLanguage, languageCode, languageName, listeningProficiency, readingProficiency, speakingProficiency, writingProficiency, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LanguageAbility {\n");

        sb.append("    isFavouriteLanguage: ").append(toIndentedString(isFavouriteLanguage)).append("\n");
        sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
        sb.append("    languageName: ").append(toIndentedString(languageName)).append("\n");
        sb.append("    listeningProficiency: ").append(toIndentedString(listeningProficiency)).append("\n");
        sb.append("    readingProficiency: ").append(toIndentedString(readingProficiency)).append("\n");
        sb.append("    speakingProficiency: ").append(toIndentedString(speakingProficiency)).append("\n");
        sb.append("    writingProficiency: ").append(toIndentedString(writingProficiency)).append("\n");
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


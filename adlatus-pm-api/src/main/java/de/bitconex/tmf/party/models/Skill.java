package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Skills evaluated for an individual with a level and possibly with a limited validity when an obsolescence is defined (Ex: the first-aid certificate first level is limited to one year and an update training is required each year to keep the level).
 */
@ApiModel(description = "Skills evaluated for an individual with a level and possibly with a limited validity when an obsolescence is defined (Ex: the first-aid certificate first level is limited to one year and an update training is required each year to keep the level).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class Skill {
    @JsonProperty("comment")
    private String comment;

    @JsonProperty("evaluatedLevel")
    private String evaluatedLevel;

    @JsonProperty("skillCode")
    private String skillCode;

    @JsonProperty("skillName")
    private String skillName;

    @JsonProperty("validFor")
    private TimePeriod validFor;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public Skill comment(String comment) {
        this.comment = comment;
        return this;
    }

    /**
     * A free text comment linked to the evaluation done
     *
     * @return comment
     */
    @ApiModelProperty(value = "A free text comment linked to the evaluation done")


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Skill evaluatedLevel(String evaluatedLevel) {
        this.evaluatedLevel = evaluatedLevel;
        return this;
    }

    /**
     * Level of expertise in a skill evaluated for an individual
     *
     * @return evaluatedLevel
     */
    @ApiModelProperty(value = "Level of expertise in a skill evaluated for an individual")


    public String getEvaluatedLevel() {
        return evaluatedLevel;
    }

    public void setEvaluatedLevel(String evaluatedLevel) {
        this.evaluatedLevel = evaluatedLevel;
    }

    public Skill skillCode(String skillCode) {
        this.skillCode = skillCode;
        return this;
    }

    /**
     * Code of the skill
     *
     * @return skillCode
     */
    @ApiModelProperty(value = "Code of the skill")


    public String getSkillCode() {
        return skillCode;
    }

    public void setSkillCode(String skillCode) {
        this.skillCode = skillCode;
    }

    public Skill skillName(String skillName) {
        this.skillName = skillName;
        return this;
    }

    /**
     * Name of the skill such as Java language,…
     *
     * @return skillName
     */
    @ApiModelProperty(value = "Name of the skill such as Java language,…")


    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Skill validFor(TimePeriod validFor) {
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

    public Skill atBaseType(String atBaseType) {
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

    public Skill atSchemaLocation(URI atSchemaLocation) {
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

    public Skill atType(String atType) {
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
        Skill skill = (Skill) o;
        return Objects.equals(this.comment, skill.comment) &&
                Objects.equals(this.evaluatedLevel, skill.evaluatedLevel) &&
                Objects.equals(this.skillCode, skill.skillCode) &&
                Objects.equals(this.skillName, skill.skillName) &&
                Objects.equals(this.validFor, skill.validFor) &&
                Objects.equals(this.atBaseType, skill.atBaseType) &&
                Objects.equals(this.atSchemaLocation, skill.atSchemaLocation) &&
                Objects.equals(this.atType, skill.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, evaluatedLevel, skillCode, skillName, validFor, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Skill {\n");

        sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
        sb.append("    evaluatedLevel: ").append(toIndentedString(evaluatedLevel)).append("\n");
        sb.append("    skillCode: ").append(toIndentedString(skillCode)).append("\n");
        sb.append("    skillName: ").append(toIndentedString(skillName)).append("\n");
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


package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Specification for resource, service or product features
 */

@Schema(name = "FeatureSpecification", description = "Specification for resource, service or product features")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class FeatureSpecification {

  private String id;

  private URI href;

  private Boolean isBundle;

  private Boolean isEnabled;

  private String name;

  private String version;

  @Valid
  private List<@Valid ConstraintRef> constraint;

  @Valid
  private List<@Valid FeatureSpecificationCharacteristic> featureSpecCharacteristic;

  @Valid
  private List<@Valid FeatureSpecificationRelationship> featureSpecRelationship;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  public FeatureSpecification id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of the feature specification. Must be locally unique within the containing specification, thus allowing direct access to the feature spec.
   * @return id
  */
  
  @Schema(name = "id", description = "Identifier of the feature specification. Must be locally unique within the containing specification, thus allowing direct access to the feature spec.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FeatureSpecification href(URI href) {
    this.href = href;
    return this;
  }

  /**
   * Hyperlink reference
   * @return href
  */
  @Valid 
  @Schema(name = "href", description = "Hyperlink reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("href")
  public URI getHref() {
    return href;
  }

  public void setHref(URI href) {
    this.href = href;
  }

  public FeatureSpecification isBundle(Boolean isBundle) {
    this.isBundle = isBundle;
    return this;
  }

  /**
   * A flag indicating if this is a feature group (true) or not (false)
   * @return isBundle
  */
  
  @Schema(name = "isBundle", description = "A flag indicating if this is a feature group (true) or not (false)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isBundle")
  public Boolean getIsBundle() {
    return isBundle;
  }

  public void setIsBundle(Boolean isBundle) {
    this.isBundle = isBundle;
  }

  public FeatureSpecification isEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
    return this;
  }

  /**
   * A flag indicating if the feature is enabled (true) or not (false)
   * @return isEnabled
  */
  
  @Schema(name = "isEnabled", description = "A flag indicating if the feature is enabled (true) or not (false)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isEnabled")
  public Boolean getIsEnabled() {
    return isEnabled;
  }

  public void setIsEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public FeatureSpecification name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Unique name given to the feature specification
   * @return name
  */
  
  @Schema(name = "name", description = "Unique name given to the feature specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FeatureSpecification version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Version of the feature specification
   * @return version
  */
  
  @Schema(name = "version", description = "Version of the feature specification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public FeatureSpecification constraint(List<@Valid ConstraintRef> constraint) {
    this.constraint = constraint;
    return this;
  }

  public FeatureSpecification addConstraintItem(ConstraintRef constraintItem) {
    if (this.constraint == null) {
      this.constraint = new ArrayList<>();
    }
    this.constraint.add(constraintItem);
    return this;
  }

  /**
   * This is a list of feature constraints
   * @return constraint
  */
  @Valid 
  @Schema(name = "constraint", description = "This is a list of feature constraints", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("constraint")
  public List<@Valid ConstraintRef> getConstraint() {
    return constraint;
  }

  public void setConstraint(List<@Valid ConstraintRef> constraint) {
    this.constraint = constraint;
  }

  public FeatureSpecification featureSpecCharacteristic(List<@Valid FeatureSpecificationCharacteristic> featureSpecCharacteristic) {
    this.featureSpecCharacteristic = featureSpecCharacteristic;
    return this;
  }

  public FeatureSpecification addFeatureSpecCharacteristicItem(FeatureSpecificationCharacteristic featureSpecCharacteristicItem) {
    if (this.featureSpecCharacteristic == null) {
      this.featureSpecCharacteristic = new ArrayList<>();
    }
    this.featureSpecCharacteristic.add(featureSpecCharacteristicItem);
    return this;
  }

  /**
   * This is a list of characteristics for a particular feature
   * @return featureSpecCharacteristic
  */
  @Valid 
  @Schema(name = "featureSpecCharacteristic", description = "This is a list of characteristics for a particular feature", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureSpecCharacteristic")
  public List<@Valid FeatureSpecificationCharacteristic> getFeatureSpecCharacteristic() {
    return featureSpecCharacteristic;
  }

  public void setFeatureSpecCharacteristic(List<@Valid FeatureSpecificationCharacteristic> featureSpecCharacteristic) {
    this.featureSpecCharacteristic = featureSpecCharacteristic;
  }

  public FeatureSpecification featureSpecRelationship(List<@Valid FeatureSpecificationRelationship> featureSpecRelationship) {
    this.featureSpecRelationship = featureSpecRelationship;
    return this;
  }

  public FeatureSpecification addFeatureSpecRelationshipItem(FeatureSpecificationRelationship featureSpecRelationshipItem) {
    if (this.featureSpecRelationship == null) {
      this.featureSpecRelationship = new ArrayList<>();
    }
    this.featureSpecRelationship.add(featureSpecRelationshipItem);
    return this;
  }

  /**
   * A dependency, exclusivity or aggratation relationship between/among feature specifications.
   * @return featureSpecRelationship
  */
  @Valid 
  @Schema(name = "featureSpecRelationship", description = "A dependency, exclusivity or aggratation relationship between/among feature specifications.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureSpecRelationship")
  public List<@Valid FeatureSpecificationRelationship> getFeatureSpecRelationship() {
    return featureSpecRelationship;
  }

  public void setFeatureSpecRelationship(List<@Valid FeatureSpecificationRelationship> featureSpecRelationship) {
    this.featureSpecRelationship = featureSpecRelationship;
  }

  public FeatureSpecification validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * Get validFor
   * @return validFor
  */
  @Valid 
  @Schema(name = "validFor", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("validFor")
  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public FeatureSpecification atBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
   * @return atBaseType
  */
  
  @Schema(name = "@baseType", description = "When sub-classing, this defines the super-class", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@baseType")
  public String getAtBaseType() {
    return atBaseType;
  }

  public void setAtBaseType(String atBaseType) {
    this.atBaseType = atBaseType;
  }

  public FeatureSpecification atSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
   * @return atSchemaLocation
  */
  @Valid 
  @Schema(name = "@schemaLocation", description = "A URI to a JSON-Schema file that defines additional attributes and relationships", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@schemaLocation")
  public URI getAtSchemaLocation() {
    return atSchemaLocation;
  }

  public void setAtSchemaLocation(URI atSchemaLocation) {
    this.atSchemaLocation = atSchemaLocation;
  }

  public FeatureSpecification atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class Extensible name
   * @return atType
  */
  
  @Schema(name = "@type", description = "When sub-classing, this defines the sub-class Extensible name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("@type")
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
    FeatureSpecification featureSpecification = (FeatureSpecification) o;
    return Objects.equals(this.id, featureSpecification.id) &&
        Objects.equals(this.href, featureSpecification.href) &&
        Objects.equals(this.isBundle, featureSpecification.isBundle) &&
        Objects.equals(this.isEnabled, featureSpecification.isEnabled) &&
        Objects.equals(this.name, featureSpecification.name) &&
        Objects.equals(this.version, featureSpecification.version) &&
        Objects.equals(this.constraint, featureSpecification.constraint) &&
        Objects.equals(this.featureSpecCharacteristic, featureSpecification.featureSpecCharacteristic) &&
        Objects.equals(this.featureSpecRelationship, featureSpecification.featureSpecRelationship) &&
        Objects.equals(this.validFor, featureSpecification.validFor) &&
        Objects.equals(this.atBaseType, featureSpecification.atBaseType) &&
        Objects.equals(this.atSchemaLocation, featureSpecification.atSchemaLocation) &&
        Objects.equals(this.atType, featureSpecification.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, isBundle, isEnabled, name, version, constraint, featureSpecCharacteristic, featureSpecRelationship, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureSpecification {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    isBundle: ").append(toIndentedString(isBundle)).append("\n");
    sb.append("    isEnabled: ").append(toIndentedString(isEnabled)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    constraint: ").append(toIndentedString(constraint)).append("\n");
    sb.append("    featureSpecCharacteristic: ").append(toIndentedString(featureSpecCharacteristic)).append("\n");
    sb.append("    featureSpecRelationship: ").append(toIndentedString(featureSpecRelationship)).append("\n");
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


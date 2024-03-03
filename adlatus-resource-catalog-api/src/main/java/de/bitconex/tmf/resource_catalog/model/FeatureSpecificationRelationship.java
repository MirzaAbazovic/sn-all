package de.bitconex.tmf.resource_catalog.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.annotation.Generated;

/**
 * Relationship between feature specifications
 */

@Schema(name = "FeatureSpecificationRelationship", description = "Relationship between feature specifications")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public class FeatureSpecificationRelationship {

  private String id;

  private URI href;

  private String featureId;

  private String name;

  private URI parentSpecificationHref;

  private String parentSpecificationId;

  private String relationshipType;

  private TimePeriod validFor;

  private String atBaseType;

  private URI atSchemaLocation;

  private String atType;

  /**
   * Default constructor
   * @deprecated Use {@link FeatureSpecificationRelationship#FeatureSpecificationRelationship(String, String)}
   */
  @Deprecated
  public FeatureSpecificationRelationship() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public FeatureSpecificationRelationship(String name, String relationshipType) {
    this.name = name;
    this.relationshipType = relationshipType;
  }

  public FeatureSpecificationRelationship id(String id) {
    this.id = id;
    return this;
  }

  /**
   * unique identifier
   * @return id
  */
  
  @Schema(name = "id", description = "unique identifier", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FeatureSpecificationRelationship href(URI href) {
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

  public FeatureSpecificationRelationship featureId(String featureId) {
    this.featureId = featureId;
    return this;
  }

  /**
   * Unique identifier of the target feature specification.
   * @return featureId
  */
  
  @Schema(name = "featureId", description = "Unique identifier of the target feature specification.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("featureId")
  public String getFeatureId() {
    return featureId;
  }

  public void setFeatureId(String featureId) {
    this.featureId = featureId;
  }

  public FeatureSpecificationRelationship name(String name) {
    this.name = name;
    return this;
  }

  /**
   * This is the name of the target feature specification.
   * @return name
  */
  @NotNull 
  @Schema(name = "name", description = "This is the name of the target feature specification.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public FeatureSpecificationRelationship parentSpecificationHref(URI parentSpecificationHref) {
    this.parentSpecificationHref = parentSpecificationHref;
    return this;
  }

  /**
   * Hyperlink reference to the parent specification containing the target feature
   * @return parentSpecificationHref
  */
  @Valid 
  @Schema(name = "parentSpecificationHref", description = "Hyperlink reference to the parent specification containing the target feature", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("parentSpecificationHref")
  public URI getParentSpecificationHref() {
    return parentSpecificationHref;
  }

  public void setParentSpecificationHref(URI parentSpecificationHref) {
    this.parentSpecificationHref = parentSpecificationHref;
  }

  public FeatureSpecificationRelationship parentSpecificationId(String parentSpecificationId) {
    this.parentSpecificationId = parentSpecificationId;
    return this;
  }

  /**
   * Unique identifier of the parent specification containing the target feature
   * @return parentSpecificationId
  */
  
  @Schema(name = "parentSpecificationId", description = "Unique identifier of the parent specification containing the target feature", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("parentSpecificationId")
  public String getParentSpecificationId() {
    return parentSpecificationId;
  }

  public void setParentSpecificationId(String parentSpecificationId) {
    this.parentSpecificationId = parentSpecificationId;
  }

  public FeatureSpecificationRelationship relationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
    return this;
  }

  /**
   * This is the type of the feature specification relationship.
   * @return relationshipType
  */
  @NotNull 
  @Schema(name = "relationshipType", description = "This is the type of the feature specification relationship.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("relationshipType")
  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType;
  }

  public FeatureSpecificationRelationship validFor(TimePeriod validFor) {
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

  public FeatureSpecificationRelationship atBaseType(String atBaseType) {
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

  public FeatureSpecificationRelationship atSchemaLocation(URI atSchemaLocation) {
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

  public FeatureSpecificationRelationship atType(String atType) {
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
    FeatureSpecificationRelationship featureSpecificationRelationship = (FeatureSpecificationRelationship) o;
    return Objects.equals(this.id, featureSpecificationRelationship.id) &&
        Objects.equals(this.href, featureSpecificationRelationship.href) &&
        Objects.equals(this.featureId, featureSpecificationRelationship.featureId) &&
        Objects.equals(this.name, featureSpecificationRelationship.name) &&
        Objects.equals(this.parentSpecificationHref, featureSpecificationRelationship.parentSpecificationHref) &&
        Objects.equals(this.parentSpecificationId, featureSpecificationRelationship.parentSpecificationId) &&
        Objects.equals(this.relationshipType, featureSpecificationRelationship.relationshipType) &&
        Objects.equals(this.validFor, featureSpecificationRelationship.validFor) &&
        Objects.equals(this.atBaseType, featureSpecificationRelationship.atBaseType) &&
        Objects.equals(this.atSchemaLocation, featureSpecificationRelationship.atSchemaLocation) &&
        Objects.equals(this.atType, featureSpecificationRelationship.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, href, featureId, name, parentSpecificationHref, parentSpecificationId, relationshipType, validFor, atBaseType, atSchemaLocation, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FeatureSpecificationRelationship {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    featureId: ").append(toIndentedString(featureId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    parentSpecificationHref: ").append(toIndentedString(parentSpecificationHref)).append("\n");
    sb.append("    parentSpecificationId: ").append(toIndentedString(parentSpecificationId)).append("\n");
    sb.append("    relationshipType: ").append(toIndentedString(relationshipType)).append("\n");
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


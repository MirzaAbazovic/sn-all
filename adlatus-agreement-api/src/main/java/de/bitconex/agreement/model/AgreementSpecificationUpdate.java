package de.bitconex.agreement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A template of an agreement that can be used when establishing partnerships Skipped properties: id,href
 */
@ApiModel(description = "A template of an agreement that can be used when establishing partnerships Skipped properties: id,href")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")


public class AgreementSpecificationUpdate   {
  @JsonProperty("description")
  private String description = null;

  @JsonProperty("isBundle")
  private Boolean isBundle = null;

  @JsonProperty("lastUpdate")
  private OffsetDateTime lastUpdate = null;

  @JsonProperty("lifecycleStatus")
  private String lifecycleStatus = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("version")
  private String version = null;

  @JsonProperty("attachment")
  @Valid
  private List<AttachmentRefOrValue> attachment = new ArrayList<AttachmentRefOrValue>();

  @JsonProperty("relatedParty")
  @Valid
  private List<RelatedParty> relatedParty = null;

  @JsonProperty("serviceCategory")
  private CategoryRef serviceCategory = null;

  @JsonProperty("specificationCharacteristic")
  @Valid
  private List<AgreementSpecCharacteristic> specificationCharacteristic = null;

  @JsonProperty("specificationRelationship")
  @Valid
  private List<AgreementSpecificationRelationship> specificationRelationship = null;

  @JsonProperty("validFor")
  private TimePeriod validFor = null;

  @JsonProperty("@baseType")
  private String baseType = null;

  @JsonProperty("@schemaLocation")
  private String schemaLocation = null;

  @JsonProperty("@type")
  private String type = null;

  public AgreementSpecificationUpdate description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A narrative that explains in detail what the agreement specification is about
   * @return description
  **/
  @ApiModelProperty(value = "A narrative that explains in detail what the agreement specification is about")


  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public AgreementSpecificationUpdate isBundle(Boolean isBundle) {
    this.isBundle = isBundle;
    return this;
  }

  /**
   * If true, this agreement specification is a grouping of other agreement specifications. The list of bundled agreement specifications is provided by the specificationRelationship property
   * @return isBundle
  **/
  @ApiModelProperty(value = "If true, this agreement specification is a grouping of other agreement specifications. The list of bundled agreement specifications is provided by the specificationRelationship property")


  public Boolean isIsBundle() {
    return isBundle;
  }

  public void setIsBundle(Boolean isBundle) {
    this.isBundle = isBundle;
  }

  public AgreementSpecificationUpdate lastUpdate(OffsetDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
    return this;
  }

  /**
   * Date and time of the last update
   * @return lastUpdate
  **/
  @ApiModelProperty(value = "Date and time of the last update")

  @Valid

  public OffsetDateTime getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(OffsetDateTime lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public AgreementSpecificationUpdate lifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
    return this;
  }

  /**
   * Indicates the current lifecycle status
   * @return lifecycleStatus
  **/
  @ApiModelProperty(value = "Indicates the current lifecycle status")


  public String getLifecycleStatus() {
    return lifecycleStatus;
  }

  public void setLifecycleStatus(String lifecycleStatus) {
    this.lifecycleStatus = lifecycleStatus;
  }

  public AgreementSpecificationUpdate name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the agreement specification
   * @return name
  **/
  @ApiModelProperty(required = true, value = "Name of the agreement specification")
  @NotNull


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AgreementSpecificationUpdate version(String version) {
    this.version = version;
    return this;
  }

  /**
   * Agreement specification version
   * @return version
  **/
  @ApiModelProperty(value = "Agreement specification version")


  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public AgreementSpecificationUpdate attachment(List<AttachmentRefOrValue> attachment) {
    this.attachment = attachment;
    return this;
  }

  public AgreementSpecificationUpdate addAttachmentItem(AttachmentRefOrValue attachmentItem) {
    this.attachment.add(attachmentItem);
    return this;
  }

  /**
   * Get attachment
   * @return attachment
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid
@Size(min=1) 
  public List<AttachmentRefOrValue> getAttachment() {
    return attachment;
  }

  public void setAttachment(List<AttachmentRefOrValue> attachment) {
    this.attachment = attachment;
  }

  public AgreementSpecificationUpdate relatedParty(List<RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
    return this;
  }

  public AgreementSpecificationUpdate addRelatedPartyItem(RelatedParty relatedPartyItem) {
    if (this.relatedParty == null) {
      this.relatedParty = new ArrayList<RelatedParty>();
    }
    this.relatedParty.add(relatedPartyItem);
    return this;
  }

  /**
   * Get relatedParty
   * @return relatedParty
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<RelatedParty> getRelatedParty() {
    return relatedParty;
  }

  public void setRelatedParty(List<RelatedParty> relatedParty) {
    this.relatedParty = relatedParty;
  }

  public AgreementSpecificationUpdate serviceCategory(CategoryRef serviceCategory) {
    this.serviceCategory = serviceCategory;
    return this;
  }

  /**
   * Get serviceCategory
   * @return serviceCategory
  **/
  @ApiModelProperty(value = "")

  @Valid

  public CategoryRef getServiceCategory() {
    return serviceCategory;
  }

  public void setServiceCategory(CategoryRef serviceCategory) {
    this.serviceCategory = serviceCategory;
  }

  public AgreementSpecificationUpdate specificationCharacteristic(List<AgreementSpecCharacteristic> specificationCharacteristic) {
    this.specificationCharacteristic = specificationCharacteristic;
    return this;
  }

  public AgreementSpecificationUpdate addSpecificationCharacteristicItem(AgreementSpecCharacteristic specificationCharacteristicItem) {
    if (this.specificationCharacteristic == null) {
      this.specificationCharacteristic = new ArrayList<AgreementSpecCharacteristic>();
    }
    this.specificationCharacteristic.add(specificationCharacteristicItem);
    return this;
  }

  /**
   * Get specificationCharacteristic
   * @return specificationCharacteristic
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<AgreementSpecCharacteristic> getSpecificationCharacteristic() {
    return specificationCharacteristic;
  }

  public void setSpecificationCharacteristic(List<AgreementSpecCharacteristic> specificationCharacteristic) {
    this.specificationCharacteristic = specificationCharacteristic;
  }

  public AgreementSpecificationUpdate specificationRelationship(List<AgreementSpecificationRelationship> specificationRelationship) {
    this.specificationRelationship = specificationRelationship;
    return this;
  }

  public AgreementSpecificationUpdate addSpecificationRelationshipItem(AgreementSpecificationRelationship specificationRelationshipItem) {
    if (this.specificationRelationship == null) {
      this.specificationRelationship = new ArrayList<AgreementSpecificationRelationship>();
    }
    this.specificationRelationship.add(specificationRelationshipItem);
    return this;
  }

  /**
   * Get specificationRelationship
   * @return specificationRelationship
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<AgreementSpecificationRelationship> getSpecificationRelationship() {
    return specificationRelationship;
  }

  public void setSpecificationRelationship(List<AgreementSpecificationRelationship> specificationRelationship) {
    this.specificationRelationship = specificationRelationship;
  }

  public AgreementSpecificationUpdate validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * The period for which the agreement specification is valid
   * @return validFor
  **/
  @ApiModelProperty(value = "The period for which the agreement specification is valid")

  @Valid

  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public AgreementSpecificationUpdate baseType(String baseType) {
    this.baseType = baseType;
    return this;
  }

  /**
   * When sub-classing, this defines the super-class
   * @return baseType
  **/
  @ApiModelProperty(value = "When sub-classing, this defines the super-class")


  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(String baseType) {
    this.baseType = baseType;
  }

  public AgreementSpecificationUpdate schemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
    return this;
  }

  /**
   * A URI to a JSON-Schema file that defines additional attributes and relationships
   * @return schemaLocation
  **/
  @ApiModelProperty(value = "A URI to a JSON-Schema file that defines additional attributes and relationships")


  public String getSchemaLocation() {
    return schemaLocation;
  }

  public void setSchemaLocation(String schemaLocation) {
    this.schemaLocation = schemaLocation;
  }

  public AgreementSpecificationUpdate type(String type) {
    this.type = type;
    return this;
  }

  /**
   * When sub-classing, this defines the sub-class entity name
   * @return type
  **/
  @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AgreementSpecificationUpdate agreementSpecificationUpdate = (AgreementSpecificationUpdate) o;
    return Objects.equals(this.description, agreementSpecificationUpdate.description) &&
        Objects.equals(this.isBundle, agreementSpecificationUpdate.isBundle) &&
        Objects.equals(this.lastUpdate, agreementSpecificationUpdate.lastUpdate) &&
        Objects.equals(this.lifecycleStatus, agreementSpecificationUpdate.lifecycleStatus) &&
        Objects.equals(this.name, agreementSpecificationUpdate.name) &&
        Objects.equals(this.version, agreementSpecificationUpdate.version) &&
        Objects.equals(this.attachment, agreementSpecificationUpdate.attachment) &&
        Objects.equals(this.relatedParty, agreementSpecificationUpdate.relatedParty) &&
        Objects.equals(this.serviceCategory, agreementSpecificationUpdate.serviceCategory) &&
        Objects.equals(this.specificationCharacteristic, agreementSpecificationUpdate.specificationCharacteristic) &&
        Objects.equals(this.specificationRelationship, agreementSpecificationUpdate.specificationRelationship) &&
        Objects.equals(this.validFor, agreementSpecificationUpdate.validFor) &&
        Objects.equals(this.baseType, agreementSpecificationUpdate.baseType) &&
        Objects.equals(this.schemaLocation, agreementSpecificationUpdate.schemaLocation) &&
        Objects.equals(this.type, agreementSpecificationUpdate.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, isBundle, lastUpdate, lifecycleStatus, name, version, attachment, relatedParty, serviceCategory, specificationCharacteristic, specificationRelationship, validFor, baseType, schemaLocation, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AgreementSpecificationUpdate {\n");
    
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    isBundle: ").append(toIndentedString(isBundle)).append("\n");
    sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
    sb.append("    lifecycleStatus: ").append(toIndentedString(lifecycleStatus)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    attachment: ").append(toIndentedString(attachment)).append("\n");
    sb.append("    relatedParty: ").append(toIndentedString(relatedParty)).append("\n");
    sb.append("    serviceCategory: ").append(toIndentedString(serviceCategory)).append("\n");
    sb.append("    specificationCharacteristic: ").append(toIndentedString(specificationCharacteristic)).append("\n");
    sb.append("    specificationRelationship: ").append(toIndentedString(specificationRelationship)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
    sb.append("    baseType: ").append(toIndentedString(baseType)).append("\n");
    sb.append("    schemaLocation: ").append(toIndentedString(schemaLocation)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}


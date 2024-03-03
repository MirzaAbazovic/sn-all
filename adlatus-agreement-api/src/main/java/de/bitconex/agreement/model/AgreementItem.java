package de.bitconex.agreement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A part of the agreement expressed in terms of a product offering and possibly including specific terms and conditions.
 */
@ApiModel(description = "A part of the agreement expressed in terms of a product offering and possibly including specific terms and conditions.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")


public class AgreementItem   {
  @JsonProperty("product")
  @Valid
  private List<ProductRef> product = null;

  @JsonProperty("productOffering")
  @Valid
  private List<ProductOfferingRef> productOffering = null;

  @JsonProperty("termOrCondition")
  @Valid
  private List<AgreementTermOrCondition> termOrCondition = null;

  @JsonProperty("@baseType")
  private String baseType = null;

  @JsonProperty("@schemaLocation")
  private String schemaLocation = null;

  @JsonProperty("@type")
  private String type = null;

  public AgreementItem product(List<ProductRef> product) {
    this.product = product;
    return this;
  }

  public AgreementItem addProductItem(ProductRef productItem) {
    if (this.product == null) {
      this.product = new ArrayList<ProductRef>();
    }
    this.product.add(productItem);
    return this;
  }

  /**
   * The list of products indirectly referred by this agreement item (since an agreement item refers primarily to product offerings)
   * @return product
  **/
  @ApiModelProperty(value = "The list of products indirectly referred by this agreement item (since an agreement item refers primarily to product offerings)")

  @Valid

  public List<ProductRef> getProduct() {
    return product;
  }

  public void setProduct(List<ProductRef> product) {
    this.product = product;
  }

  public AgreementItem productOffering(List<ProductOfferingRef> productOffering) {
    this.productOffering = productOffering;
    return this;
  }

  public AgreementItem addProductOfferingItem(ProductOfferingRef productOfferingItem) {
    if (this.productOffering == null) {
      this.productOffering = new ArrayList<ProductOfferingRef>();
    }
    this.productOffering.add(productOfferingItem);
    return this;
  }

  /**
   * The list of product offerings referred by this agreement item
   * @return productOffering
  **/
  @ApiModelProperty(value = "The list of product offerings referred by this agreement item")

  @Valid

  public List<ProductOfferingRef> getProductOffering() {
    return productOffering;
  }

  public void setProductOffering(List<ProductOfferingRef> productOffering) {
    this.productOffering = productOffering;
  }

  public AgreementItem termOrCondition(List<AgreementTermOrCondition> termOrCondition) {
    this.termOrCondition = termOrCondition;
    return this;
  }

  public AgreementItem addTermOrConditionItem(AgreementTermOrCondition termOrConditionItem) {
    if (this.termOrCondition == null) {
      this.termOrCondition = new ArrayList<AgreementTermOrCondition>();
    }
    this.termOrCondition.add(termOrConditionItem);
    return this;
  }

  /**
   * Get termOrCondition
   * @return termOrCondition
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<AgreementTermOrCondition> getTermOrCondition() {
    return termOrCondition;
  }

  public void setTermOrCondition(List<AgreementTermOrCondition> termOrCondition) {
    this.termOrCondition = termOrCondition;
  }

  public AgreementItem baseType(String baseType) {
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

  public AgreementItem schemaLocation(String schemaLocation) {
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

  public AgreementItem type(String type) {
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
    AgreementItem agreementItem = (AgreementItem) o;
    return Objects.equals(this.product, agreementItem.product) &&
        Objects.equals(this.productOffering, agreementItem.productOffering) &&
        Objects.equals(this.termOrCondition, agreementItem.termOrCondition) &&
        Objects.equals(this.baseType, agreementItem.baseType) &&
        Objects.equals(this.schemaLocation, agreementItem.schemaLocation) &&
        Objects.equals(this.type, agreementItem.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(product, productOffering, termOrCondition, baseType, schemaLocation, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AgreementItem {\n");
    
    sb.append("    product: ").append(toIndentedString(product)).append("\n");
    sb.append("    productOffering: ").append(toIndentedString(productOffering)).append("\n");
    sb.append("    termOrCondition: ").append(toIndentedString(termOrCondition)).append("\n");
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


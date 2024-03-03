package de.bitconex.agreement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

/**
 * A number or text that can be assigned to an agreement specification characteristic.
 */
@ApiModel(description = "A number or text that can be assigned to an agreement specification characteristic.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")


public class AgreementSpecCharacteristicValue   {
  @JsonProperty("default")
  private Boolean _default = null;

  @JsonProperty("unitOfMeasure")
  private String unitOfMeasure = null;

  @JsonProperty("valueFrom")
  private String valueFrom = null;

  @JsonProperty("valueTo")
  private String valueTo = null;

  @JsonProperty("valueType")
  private String valueType = null;

  @JsonProperty("validFor")
  private TimePeriod validFor = null;

  @JsonProperty("value")
  private Object value = null;

  @JsonProperty("@baseType")
  private String baseType = null;

  @JsonProperty("@schemaLocation")
  private String schemaLocation = null;

  @JsonProperty("@type")
  private String type = null;

  public AgreementSpecCharacteristicValue _default(Boolean _default) {
    this._default = _default;
    return this;
  }

  /**
   * Indicates if the value is the default value for a characteristic
   * @return _default
  **/
  @ApiModelProperty(value = "Indicates if the value is the default value for a characteristic")


  public Boolean isDefault() {
    return _default;
  }

  public void setDefault(Boolean _default) {
    this._default = _default;
  }

  public AgreementSpecCharacteristicValue unitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
    return this;
  }

  /**
   * Unit of measure for the characteristic, such as minutes, gigabytes (GB) and so on.
   * @return unitOfMeasure
  **/
  @ApiModelProperty(value = "Unit of measure for the characteristic, such as minutes, gigabytes (GB) and so on.")


  public String getUnitOfMeasure() {
    return unitOfMeasure;
  }

  public void setUnitOfMeasure(String unitOfMeasure) {
    this.unitOfMeasure = unitOfMeasure;
  }

  public AgreementSpecCharacteristicValue valueFrom(String valueFrom) {
    this.valueFrom = valueFrom;
    return this;
  }

  /**
   * The low range value that a characteristic can take on
   * @return valueFrom
  **/
  @ApiModelProperty(value = "The low range value that a characteristic can take on")


  public String getValueFrom() {
    return valueFrom;
  }

  public void setValueFrom(String valueFrom) {
    this.valueFrom = valueFrom;
  }

  public AgreementSpecCharacteristicValue valueTo(String valueTo) {
    this.valueTo = valueTo;
    return this;
  }

  /**
   * The upper range value that a characteristic can take on
   * @return valueTo
  **/
  @ApiModelProperty(value = "The upper range value that a characteristic can take on")


  public String getValueTo() {
    return valueTo;
  }

  public void setValueTo(String valueTo) {
    this.valueTo = valueTo;
  }

  public AgreementSpecCharacteristicValue valueType(String valueType) {
    this.valueType = valueType;
    return this;
  }

  /**
   * A kind of value that the characteristic can take on, such as numeric, text, and so forth
   * @return valueType
  **/
  @ApiModelProperty(value = "A kind of value that the characteristic can take on, such as numeric, text, and so forth")


  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType;
  }

  public AgreementSpecCharacteristicValue validFor(TimePeriod validFor) {
    this.validFor = validFor;
    return this;
  }

  /**
   * The period of time for which a value is applicable
   * @return validFor
  **/
  @ApiModelProperty(value = "The period of time for which a value is applicable")

  @Valid

  public TimePeriod getValidFor() {
    return validFor;
  }

  public void setValidFor(TimePeriod validFor) {
    this.validFor = validFor;
  }

  public AgreementSpecCharacteristicValue value(Object value) {
    this.value = value;
    return this;
  }

  /**
   * A discrete value that the characteristic can take on
   * @return value
  **/
  @ApiModelProperty(value = "A discrete value that the characteristic can take on")

  @Valid

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public AgreementSpecCharacteristicValue baseType(String baseType) {
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

  public AgreementSpecCharacteristicValue schemaLocation(String schemaLocation) {
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

  public AgreementSpecCharacteristicValue type(String type) {
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
    AgreementSpecCharacteristicValue agreementSpecCharacteristicValue = (AgreementSpecCharacteristicValue) o;
    return Objects.equals(this._default, agreementSpecCharacteristicValue._default) &&
        Objects.equals(this.unitOfMeasure, agreementSpecCharacteristicValue.unitOfMeasure) &&
        Objects.equals(this.valueFrom, agreementSpecCharacteristicValue.valueFrom) &&
        Objects.equals(this.valueTo, agreementSpecCharacteristicValue.valueTo) &&
        Objects.equals(this.valueType, agreementSpecCharacteristicValue.valueType) &&
        Objects.equals(this.validFor, agreementSpecCharacteristicValue.validFor) &&
        Objects.equals(this.value, agreementSpecCharacteristicValue.value) &&
        Objects.equals(this.baseType, agreementSpecCharacteristicValue.baseType) &&
        Objects.equals(this.schemaLocation, agreementSpecCharacteristicValue.schemaLocation) &&
        Objects.equals(this.type, agreementSpecCharacteristicValue.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_default, unitOfMeasure, valueFrom, valueTo, valueType, validFor, value, baseType, schemaLocation, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AgreementSpecCharacteristicValue {\n");
    
    sb.append("    _default: ").append(toIndentedString(_default)).append("\n");
    sb.append("    unitOfMeasure: ").append(toIndentedString(unitOfMeasure)).append("\n");
    sb.append("    valueFrom: ").append(toIndentedString(valueFrom)).append("\n");
    sb.append("    valueTo: ").append(toIndentedString(valueTo)).append("\n");
    sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
    sb.append("    validFor: ").append(toIndentedString(validFor)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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


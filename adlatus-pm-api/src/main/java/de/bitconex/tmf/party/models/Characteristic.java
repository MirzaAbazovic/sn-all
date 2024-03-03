package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

/**
 * Describes a given characteristic of an object or entity through a name/value pair.
 */
@ApiModel(description = "Describes a given characteristic of an object or entity through a name/value pair.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class Characteristic {
    @JsonProperty("name")
    private String name;

    @JsonProperty("valueType")
    private String valueType;

    @JsonProperty("value")
    private Object value;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public Characteristic name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the characteristic
     *
     * @return name
     */
    @ApiModelProperty(required = true, value = "Name of the characteristic")
    @NotNull


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Characteristic valueType(String valueType) {
        this.valueType = valueType;
        return this;
    }

    /**
     * Data type of the value of the characteristic
     *
     * @return valueType
     */
    @ApiModelProperty(value = "Data type of the value of the characteristic")


    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public Characteristic value(Object value) {
        this.value = value;
        return this;
    }

    /**
     * Get value
     *
     * @return value
     */
    @ApiModelProperty(required = true, value = "")
    @NotNull


    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Characteristic atBaseType(String atBaseType) {
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

    public Characteristic atSchemaLocation(URI atSchemaLocation) {
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

    public Characteristic atType(String atType) {
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
        Characteristic characteristic = (Characteristic) o;
        return Objects.equals(this.name, characteristic.name) &&
                Objects.equals(this.valueType, characteristic.valueType) &&
                Objects.equals(this.value, characteristic.value) &&
                Objects.equals(this.atBaseType, characteristic.atBaseType) &&
                Objects.equals(this.atSchemaLocation, characteristic.atSchemaLocation) &&
                Objects.equals(this.atType, characteristic.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, valueType, value, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Characteristic {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
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


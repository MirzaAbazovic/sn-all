package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * Reference of a tax definition. A tax is levied by an authorized tax jurisdiction. There are many different types of tax (Federal Tax levied by the US Government, State Tax levied by the State of California,…).
 */
@ApiModel(description = "Reference of a tax definition. A tax is levied by an authorized tax jurisdiction. There are many different types of tax (Federal Tax levied by the US Government, State Tax levied by the State of California,…).")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class TaxDefinition {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("taxType")
    private String taxType;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    @JsonProperty("@referredType")
    private String atReferredType;

    public TaxDefinition id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique identifier of the tax.
     *
     * @return id
     */
    @ApiModelProperty(value = "Unique identifier of the tax.")


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TaxDefinition name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Tax name.
     *
     * @return name
     */
    @ApiModelProperty(value = "Tax name.")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaxDefinition taxType(String taxType) {
        this.taxType = taxType;
        return this;
    }

    /**
     * Type of  the tax.
     *
     * @return taxType
     */
    @ApiModelProperty(value = "Type of  the tax.")


    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public TaxDefinition atBaseType(String atBaseType) {
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

    public TaxDefinition atSchemaLocation(URI atSchemaLocation) {
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

    public TaxDefinition atType(String atType) {
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

    public TaxDefinition atReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
        return this;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     *
     * @return atReferredType
     */
    @ApiModelProperty(value = "The actual type of the target instance when needed for disambiguation.")


    public String getAtReferredType() {
        return atReferredType;
    }

    public void setAtReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaxDefinition taxDefinition = (TaxDefinition) o;
        return Objects.equals(this.id, taxDefinition.id) &&
                Objects.equals(this.name, taxDefinition.name) &&
                Objects.equals(this.taxType, taxDefinition.taxType) &&
                Objects.equals(this.atBaseType, taxDefinition.atBaseType) &&
                Objects.equals(this.atSchemaLocation, taxDefinition.atSchemaLocation) &&
                Objects.equals(this.atType, taxDefinition.atType) &&
                Objects.equals(this.atReferredType, taxDefinition.atReferredType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taxType, atBaseType, atSchemaLocation, atType, atReferredType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TaxDefinition {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    taxType: ").append(toIndentedString(taxType)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
        sb.append("    atReferredType: ").append(toIndentedString(atReferredType)).append("\n");
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


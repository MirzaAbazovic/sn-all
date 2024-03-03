package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import java.util.Objects;

/**
 * An amount in a given unit
 */

@Schema(name = "Quantity", description = "An amount in a given unit")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class Quantity {

  private Float amount = 1.0f;

  private String units;

  public Quantity amount(Float amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Numeric value in a given unit
   * @return amount
  */
  
  @Schema(name = "amount", description = "Numeric value in a given unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("amount")
  public Float getAmount() {
    return amount;
  }

  public void setAmount(Float amount) {
    this.amount = amount;
  }

  public Quantity units(String units) {
    this.units = units;
    return this;
  }

  /**
   * Unit
   * @return units
  */
  
  @Schema(name = "units", description = "Unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("units")
  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Quantity quantity = (Quantity) o;
    return Objects.equals(this.amount, quantity.amount) &&
        Objects.equals(this.units, quantity.units);
  }

  @Override
  public int hashCode() {
    return Objects.hash(amount, units);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Quantity {\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    units: ").append(toIndentedString(units)).append("\n");
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


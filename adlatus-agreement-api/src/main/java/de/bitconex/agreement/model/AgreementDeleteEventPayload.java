package de.bitconex.agreement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Objects;

/**
 * The event data structure
 */
@ApiModel(description = "The event data structure")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")


public class AgreementDeleteEventPayload   {
  @JsonProperty("agreement")
  private Agreement agreement = null;

  public AgreementDeleteEventPayload agreement(Agreement agreement) {
    this.agreement = agreement;
    return this;
  }

  /**
   * The involved resource data for the event
   * @return agreement
  **/
  @ApiModelProperty(value = "The involved resource data for the event")

  @Valid

  public Agreement getAgreement() {
    return agreement;
  }

  public void setAgreement(Agreement agreement) {
    this.agreement = agreement;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AgreementDeleteEventPayload agreementDeleteEventPayload = (AgreementDeleteEventPayload) o;
    return Objects.equals(this.agreement, agreementDeleteEventPayload.agreement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agreement);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AgreementDeleteEventPayload {\n");
    
    sb.append("    agreement: ").append(toIndentedString(agreement)).append("\n");
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


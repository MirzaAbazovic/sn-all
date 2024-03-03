package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.Objects;

/**
 * The event data structure
 */

@Schema(name = "SearchTimeSlotCreateEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class SearchTimeSlotCreateEventPayload {

  private SearchTimeSlot searchTimeSlot;

  public SearchTimeSlotCreateEventPayload searchTimeSlot(SearchTimeSlot searchTimeSlot) {
    this.searchTimeSlot = searchTimeSlot;
    return this;
  }

  /**
   * Get searchTimeSlot
   * @return searchTimeSlot
  */
  @Valid 
  @Schema(name = "searchTimeSlot", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("searchTimeSlot")
  public SearchTimeSlot getSearchTimeSlot() {
    return searchTimeSlot;
  }

  public void setSearchTimeSlot(SearchTimeSlot searchTimeSlot) {
    this.searchTimeSlot = searchTimeSlot;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchTimeSlotCreateEventPayload searchTimeSlotCreateEventPayload = (SearchTimeSlotCreateEventPayload) o;
    return Objects.equals(this.searchTimeSlot, searchTimeSlotCreateEventPayload.searchTimeSlot);
  }

  @Override
  public int hashCode() {
    return Objects.hash(searchTimeSlot);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchTimeSlotCreateEventPayload {\n");
    sb.append("    searchTimeSlot: ").append(toIndentedString(searchTimeSlot)).append("\n");
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


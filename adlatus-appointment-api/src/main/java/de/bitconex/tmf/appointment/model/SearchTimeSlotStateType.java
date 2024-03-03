package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.Generated;

/**
 * Valid values for the lifecycle state of the searchTimeSlot
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public enum SearchTimeSlotStateType {
  
  INPROGRESS("inProgress"),
  
  DONE("done"),
  
  REJECTED("rejected"),
  
  TERMINATEDWITHERROR("terminatedWithError");

  private String value;

  SearchTimeSlotStateType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SearchTimeSlotStateType fromValue(String value) {
    for (SearchTimeSlotStateType b : SearchTimeSlotStateType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


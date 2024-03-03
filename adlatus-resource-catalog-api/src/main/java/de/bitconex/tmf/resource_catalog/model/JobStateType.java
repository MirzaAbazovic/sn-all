package de.bitconex.tmf.resource_catalog.model;

import com.fasterxml.jackson.annotation.JsonValue;


import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Valid values for the state of a batch job (e.g. catalog import)
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
public enum JobStateType {
  
  NOT_STARTED("Not Started"),
  
  RUNNING("Running"),
  
  SUCCEEDED("Succeeded"),
  
  FAILED("Failed");

  private String value;

  JobStateType(String value) {
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
  public static JobStateType fromValue(String value) {
    for (JobStateType b : JobStateType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


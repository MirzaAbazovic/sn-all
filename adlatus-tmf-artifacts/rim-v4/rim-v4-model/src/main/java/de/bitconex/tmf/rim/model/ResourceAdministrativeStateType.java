/*
 * API Resource Inventory Management
 * ## TMF API Reference: TMF639 - Resource Inventory   ### Release : 19.5 - December 2019  Resource Inventory  API goal is to provide the ability to manage Resources.  ### Operations Resource Inventory API performs the following operations on the resources : - Retrieve an entity or a collection of entities depending on filter criteria - Partial update of an entity (including updating rules) - Create an entity (including default values and creation rules) - Delete an entity (for administration purposes) - Manage notification of events
 *
 * The version of the OpenAPI document: 4.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package de.bitconex.tmf.rim.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * ResourceAdministrativeStateType enumerations
 */
public enum ResourceAdministrativeStateType {
  
  LOCKED("locked"),
  
  UNLOCKED("unlocked"),
  
  SHUTDOWN("shutdown");

  private String value;

  ResourceAdministrativeStateType(String value) {
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
  public static ResourceAdministrativeStateType fromValue(String value) {
    for (ResourceAdministrativeStateType b : ResourceAdministrativeStateType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


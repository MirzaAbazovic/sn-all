package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Sets the communication endpoint address the service instance must use to deliver notification information
 */

@Schema(name = "EventSubscriptionInput", description = "Sets the communication endpoint address the service instance must use to deliver notification information")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class EventSubscriptionInput {

  private String callback;

  private String query;

  /**
   * Default constructor
   * @deprecated Use {@link EventSubscriptionInput#EventSubscriptionInput(String)}
   */
  @Deprecated
  public EventSubscriptionInput() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EventSubscriptionInput(String callback) {
    this.callback = callback;
  }

  public EventSubscriptionInput callback(String callback) {
    this.callback = callback;
    return this;
  }

  /**
   * The callback being registered.
   * @return callback
  */
  @NotNull 
  @Schema(name = "callback", description = "The callback being registered.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("callback")
  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }

  public EventSubscriptionInput query(String query) {
    this.query = query;
    return this;
  }

  /**
   * additional data to be passed
   * @return query
  */
  
  @Schema(name = "query", description = "additional data to be passed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("query")
  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventSubscriptionInput eventSubscriptionInput = (EventSubscriptionInput) o;
    return Objects.equals(this.callback, eventSubscriptionInput.callback) &&
        Objects.equals(this.query, eventSubscriptionInput.query);
  }

  @Override
  public int hashCode() {
    return Objects.hash(callback, query);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EventSubscriptionInput {\n");
    sb.append("    callback: ").append(toIndentedString(callback)).append("\n");
    sb.append("    query: ").append(toIndentedString(query)).append("\n");
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


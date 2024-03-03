package de.bitconex.tmf.appointment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.util.Objects;

/**
 * The event data structure
 */

@Schema(name = "AppointmentStateChangeEventPayload", description = "The event data structure")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-28T17:14:44.368107400+02:00[Europe/Budapest]")
public class AppointmentStateChangeEventPayload {

  private Appointment appointment;

  public AppointmentStateChangeEventPayload appointment(Appointment appointment) {
    this.appointment = appointment;
    return this;
  }

  /**
   * Get appointment
   * @return appointment
  */
  @Valid 
  @Schema(name = "appointment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("appointment")
  public Appointment getAppointment() {
    return appointment;
  }

  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppointmentStateChangeEventPayload appointmentStateChangeEventPayload = (AppointmentStateChangeEventPayload) o;
    return Objects.equals(this.appointment, appointmentStateChangeEventPayload.appointment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(appointment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppointmentStateChangeEventPayload {\n");
    sb.append("    appointment: ").append(toIndentedString(appointment)).append("\n");
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


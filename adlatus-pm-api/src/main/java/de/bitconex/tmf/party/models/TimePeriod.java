package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A period of time, either as a deadline (endDateTime only) a startDateTime only, or both
 */
@ApiModel(description = "A period of time, either as a deadline (endDateTime only) a startDateTime only, or both")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class TimePeriod {
    @JsonProperty("endDateTime")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endDateTime;

    @JsonProperty("startDateTime")
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startDateTime;

    public TimePeriod endDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
        return this;
    }

    /**
     * End of the time period, using IETC-RFC-3339 format
     *
     * @return endDateTime
     */
    @ApiModelProperty(value = "End of the time period, using IETC-RFC-3339 format")

    @Valid

    public OffsetDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(OffsetDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public TimePeriod startDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
        return this;
    }

    /**
     * Start of the time period, using IETC-RFC-3339 format. If you define a start, you must also define an end
     *
     * @return startDateTime
     */
    @ApiModelProperty(value = "Start of the time period, using IETC-RFC-3339 format. If you define a start, you must also define an end")

    @Valid

    public OffsetDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(OffsetDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TimePeriod timePeriod = (TimePeriod) o;
        return Objects.equals(this.endDateTime, timePeriod.endDateTime) &&
                Objects.equals(this.startDateTime, timePeriod.startDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endDateTime, startDateTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TimePeriod {\n");

        sb.append("    endDateTime: ").append(toIndentedString(endDateTime)).append("\n");
        sb.append("    startDateTime: ").append(toIndentedString(startDateTime)).append("\n");
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


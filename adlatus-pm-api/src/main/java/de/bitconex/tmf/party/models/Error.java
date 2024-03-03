package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

/**
 * Used when an API throws an Error, typically with a HTTP error response-code (3xx, 4xx, 5xx)
 */
@ApiModel(description = "Used when an API throws an Error, typically with a HTTP error response-code (3xx, 4xx, 5xx)")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class Error {
    @JsonProperty("code")
    private String code;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("referenceError")
    private URI referenceError;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    public Error code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Application relevant detail, defined in the API or a common list.
     *
     * @return code
     */
    @ApiModelProperty(required = true, value = "Application relevant detail, defined in the API or a common list.")
    @NotNull


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Error reason(String reason) {
        this.reason = reason;
        return this;
    }

    /**
     * Explanation of the reason for the error which can be shown to a client user.
     *
     * @return reason
     */
    @ApiModelProperty(required = true, value = "Explanation of the reason for the error which can be shown to a client user.")
    @NotNull


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Error message(String message) {
        this.message = message;
        return this;
    }

    /**
     * More details and corrective actions related to the error which can be shown to a client user.
     *
     * @return message
     */
    @ApiModelProperty(value = "More details and corrective actions related to the error which can be shown to a client user.")


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Error status(String status) {
        this.status = status;
        return this;
    }

    /**
     * HTTP Error code extension
     *
     * @return status
     */
    @ApiModelProperty(value = "HTTP Error code extension")


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Error referenceError(URI referenceError) {
        this.referenceError = referenceError;
        return this;
    }

    /**
     * URI of documentation describing the error.
     *
     * @return referenceError
     */
    @ApiModelProperty(value = "URI of documentation describing the error.")

    @Valid

    public URI getReferenceError() {
        return referenceError;
    }

    public void setReferenceError(URI referenceError) {
        this.referenceError = referenceError;
    }

    public Error atBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class.
     *
     * @return atBaseType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the super-class.")


    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public Error atSchemaLocation(URI atSchemaLocation) {
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

    public Error atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name.
     *
     * @return atType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name.")


    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Error error = (Error) o;
        return Objects.equals(this.code, error.code) &&
                Objects.equals(this.reason, error.reason) &&
                Objects.equals(this.message, error.message) &&
                Objects.equals(this.status, error.status) &&
                Objects.equals(this.referenceError, error.referenceError) &&
                Objects.equals(this.atBaseType, error.atBaseType) &&
                Objects.equals(this.atSchemaLocation, error.atSchemaLocation) &&
                Objects.equals(this.atType, error.atType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, reason, message, status, referenceError, atBaseType, atSchemaLocation, atType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Error {\n");

        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    referenceError: ").append(toIndentedString(referenceError)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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


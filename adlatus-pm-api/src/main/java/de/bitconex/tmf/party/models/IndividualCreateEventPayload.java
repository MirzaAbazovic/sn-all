package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import java.util.Objects;

/**
 * The event data structure
 */
@ApiModel(description = "The event data structure")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class IndividualCreateEventPayload {
    @JsonProperty("individual")
    private Individual individual;

    public IndividualCreateEventPayload individual(Individual individual) {
        this.individual = individual;
        return this;
    }

    /**
     * Get individual
     *
     * @return individual
     */
    @ApiModelProperty(value = "")

    @Valid

    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IndividualCreateEventPayload individualCreateEventPayload = (IndividualCreateEventPayload) o;
        return Objects.equals(this.individual, individualCreateEventPayload.individual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(individual);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class IndividualCreateEventPayload {\n");

        sb.append("    individual: ").append(toIndentedString(individual)).append("\n");
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


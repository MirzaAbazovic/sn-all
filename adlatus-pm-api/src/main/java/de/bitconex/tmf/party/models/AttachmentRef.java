package de.bitconex.tmf.party.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

/**
 * Attachment reference. An attachment complements the description of an element (for instance a product) through video, pictures
 */
@ApiModel(description = "Attachment reference. An attachment complements the description of an element (for instance a product) through video, pictures")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-31T12:47:08.301265800+02:00[Europe/Belgrade]")
public class AttachmentRef {
    @JsonProperty("id")
    private String id;

    @JsonProperty("href")
    private String href;

    @JsonProperty("description")
    private String description;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("@baseType")
    private String atBaseType;

    @JsonProperty("@schemaLocation")
    private URI atSchemaLocation;

    @JsonProperty("@type")
    private String atType;

    @JsonProperty("@referredType")
    private String atReferredType;

    public AttachmentRef id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Unique-Identifier for this attachment
     *
     * @return id
     */
    @ApiModelProperty(required = true, value = "Unique-Identifier for this attachment")
    @NotNull


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AttachmentRef href(String href) {
        this.href = href;
        return this;
    }

    /**
     * URL serving as reference for the attachment resource
     *
     * @return href
     */
    @ApiModelProperty(value = "URL serving as reference for the attachment resource")


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public AttachmentRef description(String description) {
        this.description = description;
        return this;
    }

    /**
     * A narrative text describing the content of the attachment
     *
     * @return description
     */
    @ApiModelProperty(value = "A narrative text describing the content of the attachment")


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AttachmentRef name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Name of the related entity.
     *
     * @return name
     */
    @ApiModelProperty(value = "Name of the related entity.")


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttachmentRef url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Link to the attachment media/content
     *
     * @return url
     */
    @ApiModelProperty(value = "Link to the attachment media/content")


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AttachmentRef atBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
        return this;
    }

    /**
     * When sub-classing, this defines the super-class
     *
     * @return atBaseType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the super-class")


    public String getAtBaseType() {
        return atBaseType;
    }

    public void setAtBaseType(String atBaseType) {
        this.atBaseType = atBaseType;
    }

    public AttachmentRef atSchemaLocation(URI atSchemaLocation) {
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

    public AttachmentRef atType(String atType) {
        this.atType = atType;
        return this;
    }

    /**
     * When sub-classing, this defines the sub-class entity name
     *
     * @return atType
     */
    @ApiModelProperty(value = "When sub-classing, this defines the sub-class entity name")


    public String getAtType() {
        return atType;
    }

    public void setAtType(String atType) {
        this.atType = atType;
    }

    public AttachmentRef atReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
        return this;
    }

    /**
     * The actual type of the target instance when needed for disambiguation.
     *
     * @return atReferredType
     */
    @ApiModelProperty(value = "The actual type of the target instance when needed for disambiguation.")


    public String getAtReferredType() {
        return atReferredType;
    }

    public void setAtReferredType(String atReferredType) {
        this.atReferredType = atReferredType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttachmentRef attachmentRef = (AttachmentRef) o;
        return Objects.equals(this.id, attachmentRef.id) &&
                Objects.equals(this.href, attachmentRef.href) &&
                Objects.equals(this.description, attachmentRef.description) &&
                Objects.equals(this.name, attachmentRef.name) &&
                Objects.equals(this.url, attachmentRef.url) &&
                Objects.equals(this.atBaseType, attachmentRef.atBaseType) &&
                Objects.equals(this.atSchemaLocation, attachmentRef.atSchemaLocation) &&
                Objects.equals(this.atType, attachmentRef.atType) &&
                Objects.equals(this.atReferredType, attachmentRef.atReferredType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, href, description, name, url, atBaseType, atSchemaLocation, atType, atReferredType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AttachmentRef {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    atBaseType: ").append(toIndentedString(atBaseType)).append("\n");
        sb.append("    atSchemaLocation: ").append(toIndentedString(atSchemaLocation)).append("\n");
        sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
        sb.append("    atReferredType: ").append(toIndentedString(atReferredType)).append("\n");
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


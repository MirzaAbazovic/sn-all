package de.bitconex.adlatus.wholebuy.provision.service.order.mapper;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ExternalReference;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceAttachment;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceCharacteristic;
import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceSpecification;
import de.bitconex.tmf.rom.model.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class CustomOrderMapper {
    private final OrderMapper orderMapper;

    public CustomOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    public ResourceOrder mapToResourceOrderTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder resourceOrder) {
        if (resourceOrder == null) {
            return null;
        }

        ResourceOrder.ResourceOrderBuilder builder = ResourceOrder.builder();

        builder.id(resourceOrder.getId());
        builder.href(resourceOrder.getHref());
        builder.atBaseType(resourceOrder.getAtBaseType());
        builder.atSchemaLocation(map(resourceOrder.getAtSchemaLocation()));
        builder.atType(resourceOrder.getAtType());
        builder.category(resourceOrder.getCategory());
        builder.completionDate(resourceOrder.getCompletionDate());
        builder.description(resourceOrder.getDescription());
        builder.expectedCompletionDate(resourceOrder.getExpectedCompletionDate());
        builder.externalId(resourceOrder.getExternalId());
        builder.name(resourceOrder.getName());
        builder.orderDate(resourceOrder.getOrderDate());
        builder.orderType(resourceOrder.getOrderType());
        builder.priority(resourceOrder.getPriority());
        builder.requestedCompletionDate(resourceOrder.getRequestedCompletionDate());
        builder.requestedStartDate(resourceOrder.getRequestedStartDate());
        builder.startDate(resourceOrder.getStartDate());
        builder.state(resourceOrder.getState().toString());

        ResourceOrder ro = builder.build();

        if (resourceOrder.getResourceOrderItems() != null) {
            ro.setOrderItem(resourceOrder.getResourceOrderItems().stream().map(item -> this.mapToResourceOrderItemTmf(item)).toList());
        }

        if (resourceOrder.getRelatedParties() != null) {
            ro.setRelatedParty(resourceOrder.getRelatedParties().stream().map(item -> this.mapToRelatedPartyTmf(item)).toList());
        }

        if (resourceOrder.getNotes() != null) {
            ro.setNote(resourceOrder.getNotes().stream().map(item -> this.mapToNoteTmf(item)).toList());
        }

        if (resourceOrder.getExternalReferences() != null) {
            ro.setExternalReference(resourceOrder.getExternalReferences().stream().map(item -> this.mapToExternalIdTmf(item)).toList());
        }

        return ro;
    }


    public de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder mapToResourceOrderAdl(ResourceOrder resourceOrder) {
        if (resourceOrder == null) {
            return null;
        }

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder.ResourceOrderBuilder<?, ?> resourceOrder1 = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder.builder();

        resourceOrder1.id(resourceOrder.getId());
        resourceOrder1.href(resourceOrder.getHref());
        resourceOrder1.atBaseType(resourceOrder.getAtBaseType());
        resourceOrder1.atSchemaLocation(map(resourceOrder.getAtSchemaLocation()));
        resourceOrder1.atType(resourceOrder.getAtType());
        resourceOrder1.category(resourceOrder.getCategory());
        resourceOrder1.completionDate(resourceOrder.getCompletionDate());
        resourceOrder1.description(resourceOrder.getDescription());
        resourceOrder1.expectedCompletionDate(resourceOrder.getExpectedCompletionDate());
        resourceOrder1.externalId(resourceOrder.getExternalId());
        resourceOrder1.name(resourceOrder.getName());
        resourceOrder1.orderDate(resourceOrder.getOrderDate());
        resourceOrder1.orderType(resourceOrder.getOrderType());
        resourceOrder1.priority(resourceOrder.getPriority());
        resourceOrder1.requestedCompletionDate(resourceOrder.getRequestedCompletionDate());
        resourceOrder1.requestedStartDate(resourceOrder.getRequestedStartDate());
        resourceOrder1.startDate(resourceOrder.getStartDate());
        if (resourceOrder.getState() != null) {
            resourceOrder1.state(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder.ResourceOrderState.fromValue(resourceOrder.getState()));
        }
        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrder resourceOrder2 = resourceOrder1.build();

        if (resourceOrder.getOrderItem() != null) {
            for (var item :
                resourceOrder.getOrderItem()) {
                de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem roi = this.mapToResourceOrderItemAdl(item);
                resourceOrder2.addResourceOrderItem(roi);
            }
        }

        if (resourceOrder.getRelatedParty() != null) {
            for (var item :
                resourceOrder.getRelatedParty()) {
                resourceOrder2.addRelatedParty(orderMapper.mapToRelatedPartyAdl(item));
            }
        }

        if (resourceOrder.getNote() != null) {
            for (var item :
                resourceOrder.getNote()) {
                resourceOrder2.addNote(orderMapper.mapToNoteAdl(item));
            }
        }

        if (resourceOrder.getExternalReference() != null) {
            for (var item :
                resourceOrder.getExternalReference()) {
                resourceOrder2.addExternalReference(this.mapToExternalReferenceAdl(item));
            }
        }
        return resourceOrder2;
    }

    private ExternalReference mapToExternalReferenceAdl(ExternalId item) {
        if (item == null) return null;

        ExternalReference.ExternalReferenceBuilder<?, ?> externalReferenceBuilder = ExternalReference.builder();

        externalReferenceBuilder.id(item.getId());
        externalReferenceBuilder.atBaseType(item.getAtBaseType());
        externalReferenceBuilder.atSchemaLocation(map(item.getAtSchemaLocation()));
        externalReferenceBuilder.atType(item.getAtType());

        return externalReferenceBuilder.build();
    }

    private de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem mapToResourceOrderItemAdl(ResourceOrderItem resourceOrderItem) {
        if (resourceOrderItem == null) return null;

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem.ResourceOrderItemBuilder<?, ?> resourceOrderItemBuilder = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem.builder();

        resourceOrderItemBuilder.id(resourceOrderItem.getId());
        resourceOrderItemBuilder.href("");
        resourceOrderItemBuilder.atBaseType(resourceOrderItem.getAtBaseType());
        resourceOrderItemBuilder.atSchemaLocation(map(resourceOrderItem.getAtSchemaLocation()));
        resourceOrderItemBuilder.atType(resourceOrderItem.getAtType());
        resourceOrderItemBuilder.action(resourceOrderItem.getAction());
        resourceOrderItemBuilder.quantity(resourceOrderItem.getQuantity());
        resourceOrderItemBuilder.state(resourceOrderItem.getState());
        resourceOrderItemBuilder.appointmentRef(mapToAppointmentRefAdl(resourceOrderItem.getAppointment()));
        resourceOrderItemBuilder.resourceSpecification(mapToResourceSpecificationAdl(resourceOrderItem.getResourceSpecification()));
        resourceOrderItemBuilder.resource(mapToResourceAdl(resourceOrderItem.getResource()));

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem resourceOrderItem2 = resourceOrderItemBuilder.build();

        if (resourceOrderItem.getOrderItemRelationship() != null) {
            for (var rel :
                resourceOrderItem.getOrderItemRelationship()) {
                resourceOrderItem2.addResourceOrderItemRelationship(mapToResourceOrderItemRelationshipAdl(rel));
            }
        }

        return resourceOrderItem2;
    }

    private de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource mapToResourceAdl(ResourceRefOrValue resource) {
        if (resource == null) return null;

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource.ResourceBuilder<?, ?> resourceBuilder = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource.builder();

        resourceBuilder.id(resource.getId());
        resourceBuilder.href(resource.getHref());
        resourceBuilder.atBaseType(resource.getAtBaseType());
        resourceBuilder.atSchemaLocation(map(resource.getAtSchemaLocation()));
        resourceBuilder.atType(resource.getAtType());
        resourceBuilder.administrativeState(map(resource.getAdministrativeState()));
        resourceBuilder.category(resource.getCategory());
        resourceBuilder.description(resource.getDescription());
        resourceBuilder.endOperatingDate(resource.getEndOperatingDate());
        resourceBuilder.name(resource.getName());
        resourceBuilder.operationalState(map(resource.getOperationalState()));
        resourceBuilder.resourceStatus(map(resource.getResourceStatus()));
        resourceBuilder.resourceVersion(resource.getResourceVersion());
        resourceBuilder.startOperatingDate(resource.getStartOperatingDate());
        resourceBuilder.usageState(map(resource.getUsageState()));
        resourceBuilder.atReferredType("");
        resourceBuilder.resourceSpecification(mapToResourceSpecificationAdl(resource.getResourceSpecification()));
        resourceBuilder.relatedPlace(orderMapper.mapToRelatedPlaceAdl(resource.getPlace()));

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource resource1 = resourceBuilder.build();

        if (resource.getRelatedParty() != null) {
            for (var item :
                resource.getRelatedParty()) {
                resource1.addRelatedParty(orderMapper.mapToRelatedPartyAdl(item));
            }
        }

        if (resource.getResourceRelationship() != null) {
            for (var item :
                resource.getResourceRelationship()) {
                resource1.addResourceRelationship(orderMapper.mapToResourceRelationshipAdl(item));
            }
        }

        if (resource.getResourceCharacteristic() != null) {
            for (var item :
                resource.getResourceCharacteristic()) {
                resource1.addResourceCharacteristic(mapToResourceCharacteristicAdl(item));
            }
        }

        if (resource.getAttachment() != null) {
            for (var item :
                resource.getAttachment()) {
                resource1.addAttachment(this.mapToResourceAttachmentAdl(item));
            }
        }

        return resource1;
    }

    private de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.AppointmentRef mapToAppointmentRefAdl(AppointmentRef appointmentRef) {
        if (appointmentRef == null) return null;

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.AppointmentRef.AppointmentRefBuilder<?, ?> appointmentRefBuilder = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.AppointmentRef.builder();

        appointmentRefBuilder.id(appointmentRef.getId());
        appointmentRefBuilder.href(appointmentRef.getHref());
        appointmentRefBuilder.atBaseType(appointmentRef.getAtBaseType());
        appointmentRefBuilder.atSchemaLocation(map(appointmentRef.getAtSchemaLocation()));
        appointmentRefBuilder.atType(appointmentRef.getAtType());
        appointmentRefBuilder.description(appointmentRef.getDescription());
        appointmentRefBuilder.atReferredType(appointmentRef.getAtReferredType());

        return appointmentRefBuilder.build();
    }

    private ResourceSpecification mapToResourceSpecificationAdl(ResourceSpecificationRef resourceSpecificationRef) {
        if (resourceSpecificationRef == null) return null;

        ResourceSpecification.ResourceSpecificationBuilder<?, ?> resourceSpecificationBuilder = ResourceSpecification.builder();

        resourceSpecificationBuilder.id(resourceSpecificationRef.getId());
        resourceSpecificationBuilder.href(resourceSpecificationRef.getHref());
        resourceSpecificationBuilder.atBaseType(resourceSpecificationRef.getAtBaseType());
        resourceSpecificationBuilder.atSchemaLocation(map(resourceSpecificationRef.getAtSchemaLocation()));
        resourceSpecificationBuilder.atType(resourceSpecificationRef.getAtType());
        resourceSpecificationBuilder.name(resourceSpecificationRef.getName());
        resourceSpecificationBuilder.version(resourceSpecificationRef.getVersion());
        resourceSpecificationBuilder.atReferredType(resourceSpecificationRef.getAtReferredType());

        return resourceSpecificationBuilder.build();
    }

    private de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRelationship mapToResourceOrderItemRelationshipAdl(ResourceOrderItemRelationship resourceOrderItemRelationship) {
        if (resourceOrderItemRelationship == null) return null;

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRelationship.ResourceOrderItemRelationshipBuilder<?, ?> resourceOrderItemRelationshipBuilder = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRelationship.builder();

        resourceOrderItemRelationshipBuilder.id(UUID.randomUUID().toString());
        resourceOrderItemRelationshipBuilder.href("");
        resourceOrderItemRelationshipBuilder.atBaseType(resourceOrderItemRelationship.getAtBaseType());
        resourceOrderItemRelationshipBuilder.atSchemaLocation(map(resourceOrderItemRelationship.getAtSchemaLocation()));
        resourceOrderItemRelationshipBuilder.atType(resourceOrderItemRelationship.getAtType());
        resourceOrderItemRelationshipBuilder.resourceOrderItemRef(mapToResourceOrderItemRefAdl(resourceOrderItemRelationship.getOrderItem()));

        return resourceOrderItemRelationshipBuilder.build();
    }

    private de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRef mapToResourceOrderItemRefAdl(ResourceOrderItemRef resourceOrderItemRef) {
        if (resourceOrderItemRef == null) return null;

        de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRef.ResourceOrderItemRefBuilder resourceOrderItemRefBuilder = de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRef.builder();

        resourceOrderItemRefBuilder.id(UUID.randomUUID().toString());
        resourceOrderItemRefBuilder.atBaseType(resourceOrderItemRef.getAtBaseType());
        resourceOrderItemRefBuilder.atSchemaLocation(map(resourceOrderItemRef.getAtSchemaLocation()));
        resourceOrderItemRefBuilder.atType(resourceOrderItemRef.getAtType());
        resourceOrderItemRefBuilder.atReferredType(resourceOrderItemRef.getAtReferredType());
        resourceOrderItemRefBuilder.itemId(resourceOrderItemRef.getItemId());
        resourceOrderItemRefBuilder.resourceOrderHref(resourceOrderItemRef.getResourceOrderHref());
        resourceOrderItemRefBuilder.resourceOrderId(resourceOrderItemRef.getResourceOrderId());

        return resourceOrderItemRefBuilder.build();
    }

    private ResourceAttachment mapToResourceAttachmentAdl(AttachmentRefOrValue resourceAttachment) {
        if (resourceAttachment == null) return null;

        ResourceAttachment.ResourceAttachmentBuilder<?, ?> resourceAttachmentBuilder = ResourceAttachment.builder();

        resourceAttachmentBuilder.id(resourceAttachment.getId());
        resourceAttachmentBuilder.href(resourceAttachment.getHref());
        resourceAttachmentBuilder.atBaseType(resourceAttachment.getAtBaseType());
        resourceAttachmentBuilder.atSchemaLocation(map(resourceAttachment.getAtSchemaLocation()));
        resourceAttachmentBuilder.atType(resourceAttachment.getAtType());
        resourceAttachmentBuilder.attachmentType(resourceAttachment.getAttachmentType());
        resourceAttachmentBuilder.content("");
        resourceAttachmentBuilder.description(resourceAttachment.getDescription());
        resourceAttachmentBuilder.mimeType(resourceAttachment.getMimeType());
        resourceAttachmentBuilder.name(resourceAttachment.getName());
        resourceAttachmentBuilder.size(orderMapper.mapToQuantityAdl(resourceAttachment.getSize()));
        resourceAttachmentBuilder.validFor(orderMapper.mapToTimePeriodAdl(resourceAttachment.getValidFor()));
        resourceAttachmentBuilder.atReferredType(resourceAttachment.getAtReferredType());

        return resourceAttachmentBuilder.build();
    }

    private ResourceCharacteristic mapToResourceCharacteristicAdl(Characteristic resourceCharacteristic) {
        if (resourceCharacteristic == null) return null;

        ResourceCharacteristic.ResourceCharacteristicBuilder<?, ?> resourceCharacteristicBuilder = ResourceCharacteristic.builder();

        resourceCharacteristicBuilder.id(resourceCharacteristic.getId());
        resourceCharacteristicBuilder.name(resourceCharacteristic.getName());
        resourceCharacteristicBuilder.value(map(resourceCharacteristic.getValue()));
        resourceCharacteristicBuilder.valueType(resourceCharacteristic.getValueType());
        resourceCharacteristicBuilder.atBaseType(resourceCharacteristic.getAtBaseType());
        resourceCharacteristicBuilder.atSchemaLocation(map(resourceCharacteristic.getAtSchemaLocation()));
        resourceCharacteristicBuilder.atType(resourceCharacteristic.getAtType());

        return resourceCharacteristicBuilder.build();
    }

    private static String map(URI uri) {
        return uri != null ? uri.toString() : null;
    }

    private static URI map(String uri) {
        return uri != null ? URI.create(uri) : null;
    }

    private static String map(ResourceAdministrativeStateType state) {
        return state != null ? state.toString() : null;
    }

    private static String map(ResourceOperationalStateType type) {
        return type != null ? type.toString() : null;
    }

    private static String map(ResourceStatusType type) {
        return type != null ? type.toString() : null;
    }

    private static String map(ResourceUsageStateType type) {
        return type != null ? type.toString() : null;
    }

    private static String map(Object value) {
        return value != null ? value.toString() : null;
    }

    private static ResourceAdministrativeStateType mapToResourceAdministrativeStateType(String state) {
        return state != null ? ResourceAdministrativeStateType.valueOf(state) : null;
    }

    private static ResourceOperationalStateType mapToResourceOperationalStateType(String state) {
        return state != null ? ResourceOperationalStateType.valueOf(state) : null;
    }

    private static ResourceStatusType mapToResourceStatusType(String state) {
        return state != null ? ResourceStatusType.valueOf(state) : null;
    }

    private static ResourceUsageStateType mapToResourceUsageStateType(String state) {
        return state != null ? ResourceUsageStateType.valueOf(state) : null;
    }

    private ResourceOrderItem mapToResourceOrderItemTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItem resourceOrderItem) {
        if (resourceOrderItem == null) return null;

        ResourceOrderItem.ResourceOrderItemBuilder builder = ResourceOrderItem.builder();

        builder.id(resourceOrderItem.getId());
        builder.action(resourceOrderItem.getAction());
        builder.quantity(resourceOrderItem.getQuantity());
        builder.state(resourceOrderItem.getState());
        if (resourceOrderItem.getAppointmentRef() != null) {
            builder.appointment(mapToAppointmentRefTmf(resourceOrderItem.getAppointmentRef()));
        }
        builder.resource(mapToResourceTmf(resourceOrderItem.getResource()));
        if (resourceOrderItem.getResourceSpecification() != null) {
            builder.resourceSpecification(mapToResourceSpecificationTmf(resourceOrderItem.getResourceSpecification()));
        }
        builder.atBaseType(resourceOrderItem.getAtBaseType());
        builder.atSchemaLocation(map(resourceOrderItem.getAtSchemaLocation()));
        builder.atType(resourceOrderItem.getAtType());

        if (resourceOrderItem.getResourceOrderItemRelationships() != null) {
            builder.orderItemRelationship(resourceOrderItem.getResourceOrderItemRelationships().stream().map(item -> this.mapToResourceOrderItemRelationshipTmf(item)).toList());
        }

        return builder.build();
    }

    private ResourceOrderItemRelationship mapToResourceOrderItemRelationshipTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRelationship item) {
        ResourceOrderItemRelationship.ResourceOrderItemRelationshipBuilder builder = ResourceOrderItemRelationship.builder();

        builder.relationshipType(item.getRelationshipType());
        builder.orderItem(mapToResourceOrderItemRefTmf(item.getResourceOrderItemRef()));
        builder.atBaseType(item.getAtBaseType());
        builder.atSchemaLocation(map(item.getAtSchemaLocation()));
        builder.atType(item.getAtType());

        return builder.build();
    }

    private ResourceOrderItemRef mapToResourceOrderItemRefTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceOrderItemRef resourceOrderItemRef) {
        ResourceOrderItemRef.ResourceOrderItemRefBuilder builder = ResourceOrderItemRef.builder();

        builder.itemId(resourceOrderItemRef.getItemId());
        builder.resourceOrderHref(resourceOrderItemRef.getResourceOrderHref());
        builder.resourceOrderId(resourceOrderItemRef.getResourceOrderId());
        builder.atBaseType(resourceOrderItemRef.getAtBaseType());
        builder.atSchemaLocation(map(resourceOrderItemRef.getAtSchemaLocation()));
        builder.atType(resourceOrderItemRef.getAtType());
        builder.atReferredType(resourceOrderItemRef.getAtReferredType());

        return builder.build();
    }

    private ResourceSpecificationRef mapToResourceSpecificationTmf(ResourceSpecification resourceSpecification) {
        ResourceSpecificationRef.ResourceSpecificationRefBuilder builder = ResourceSpecificationRef.builder();

        builder.id(resourceSpecification.getId());
        builder.href(resourceSpecification.getHref());
        builder.name(resourceSpecification.getName());
        builder.version(resourceSpecification.getVersion());
        builder.atBaseType(resourceSpecification.getAtBaseType());
        builder.atSchemaLocation(map(resourceSpecification.getAtSchemaLocation()));
        builder.atType(resourceSpecification.getAtType());
        builder.atReferredType(resourceSpecification.getAtReferredType());

        return builder.build();
    }

    private ResourceRefOrValue mapToResourceTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Resource resource) {
        ResourceRefOrValue.ResourceRefOrValueBuilder builder = ResourceRefOrValue.builder();

        builder.id(resource.getId());
        builder.href(resource.getHref());
        builder.category(resource.getCategory());
        builder.description(resource.getDescription());
        builder.endOperatingDate(resource.getEndOperatingDate());
        builder.name(resource.getName());
        builder.resourceVersion(resource.getResourceVersion());
        builder.startOperatingDate(resource.getStartOperatingDate());
        builder.administrativeState(mapToResourceAdministrativeStateType(resource.getAdministrativeState()));
        builder.operationalState(mapToResourceOperationalStateType(resource.getOperationalState()));
        builder.resourceStatus(mapToResourceStatusType(resource.getResourceStatus()));
        builder.usageState(mapToResourceUsageStateType(resource.getUsageState()));
        builder.atBaseType(resource.getAtBaseType());
        builder.atSchemaLocation(map(resource.getAtSchemaLocation()));
        builder.atType(resource.getAtType());
        builder.atReferredType(resource.getAtReferredType());
        builder.note(new ArrayList<>());

        if (resource.getRelatedPlace() != null) {
            RelatedPlaceRefOrValue place = new RelatedPlaceRefOrValue();
            place.setId(resource.getRelatedPlace().getId());
            place.setHref(resource.getRelatedPlace().getHref());
            place.setAtReferredType(resource.getRelatedPlace().getAtReferredType());
            place.setRole(resource.getRelatedPlace().getRole());
            builder.place(place);
        } else {
            builder.place(null);
        }

        if (resource.getAttachments() != null) {
            builder.attachment(resource.getAttachments().stream().map(this::mapToResourceAttachmentTmf).toList());
        }

        if (resource.getRelatedParties() != null) {
            builder.relatedParty(resource.getRelatedParties().stream().map(this::mapToRelatedPartyTmf).toList());
        }

        if (resource.getResourceRelationships() != null) {
            builder.resourceRelationship(resource.getResourceRelationships().stream().map(this::mapToResourceRelationshipTmf).toList());
        }

        if (resource.getResourceCharacteristic() != null) {
            builder.resourceCharacteristic(resource.getResourceCharacteristic().stream().map(this::mapToResourceCharacteristicTmf).toList());
        }

        return builder.build();
    }

    private Characteristic mapToResourceCharacteristicTmf(ResourceCharacteristic resourceCharacteristic) {
        Characteristic.CharacteristicBuilder builder = Characteristic.builder();

        builder.id(resourceCharacteristic.getId());
        builder.name(resourceCharacteristic.getName());
        builder.value(resourceCharacteristic.getValue());
        builder.valueType(resourceCharacteristic.getValueType());
        builder.atBaseType(resourceCharacteristic.getAtBaseType());
        builder.atSchemaLocation(map(resourceCharacteristic.getAtSchemaLocation()));
        builder.atType(resourceCharacteristic.getAtType());
        builder.characteristicRelationship(new ArrayList<>());

        return builder.build();
    }

    private ResourceRelationship mapToResourceRelationshipTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceRelationship resourceRelationship) {
        ResourceRelationship.ResourceRelationshipBuilder builder = ResourceRelationship.builder();

        builder.id(resourceRelationship.getId());
        builder.href(resourceRelationship.getHref());
        builder.relationshipType(resourceRelationship.getRelationshipType());
        builder.atBaseType(resourceRelationship.getAtBaseType());
        builder.atSchemaLocation(map(resourceRelationship.getAtSchemaLocation()));
        builder.atType(resourceRelationship.getAtType());

        return builder.build();
    }

    private AttachmentRefOrValue mapToResourceAttachmentTmf(ResourceAttachment item) {
        AttachmentRefOrValue.AttachmentRefOrValueBuilder builder = AttachmentRefOrValue.builder();

        builder.id(item.getId());
        builder.href(item.getHref());
        builder.attachmentType(item.getAttachmentType());
        builder.description(item.getDescription());
        builder.mimeType(item.getMimeType());
        builder.name(item.getName());
        builder.size(orderMapper.mapToQuantityTmf(item.getSize()));
        builder.validFor(orderMapper.mapToTimePeriodTmf(item.getValidFor()));
        builder.atBaseType(item.getAtBaseType());
        builder.atSchemaLocation(map(item.getAtSchemaLocation()));
        builder.atType(item.getAtType());
        builder.atReferredType(item.getAtReferredType());

        return builder.build();
    }

    private AppointmentRef mapToAppointmentRefTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.AppointmentRef appointmentRef) {
        AppointmentRef.AppointmentRefBuilder builder = AppointmentRef.builder();

        builder.id(appointmentRef.getId());
        builder.href(appointmentRef.getHref());
        builder.description(appointmentRef.getDescription());
        builder.atBaseType(appointmentRef.getAtBaseType());
        builder.atSchemaLocation(map(appointmentRef.getAtSchemaLocation()));
        builder.atType(appointmentRef.getAtType());
        builder.atReferredType(appointmentRef.getAtReferredType());

        return builder.build();
    }

    // create mapToRelatedPartyTmf
    private RelatedParty mapToRelatedPartyTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty relatedParty) {
        if (relatedParty == null) return null;

        RelatedParty.RelatedPartyBuilder builder = RelatedParty.builder();

        builder.id(relatedParty.getId());
        builder.href(relatedParty.getHref());
        builder.name(relatedParty.getName());
        builder.role(relatedParty.getRole());
        builder.atBaseType(relatedParty.getAtBaseType());
        builder.atSchemaLocation(map(relatedParty.getAtSchemaLocation()));
        builder.atType(relatedParty.getAtType());
        builder.atReferredType(relatedParty.getAtReferredType());

        return builder.build();
    }

    private Note mapToNoteTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Note note) {
        if (note == null) return null;

        Note.NoteBuilder builder = Note.builder();

        builder.id(note.getId());
        builder.author(note.getAuthor());
        builder.date(note.getDate());
        builder.text(note.getText());
        builder.atBaseType(note.getAtBaseType());
        builder.atSchemaLocation(map(note.getAtSchemaLocation()));
        builder.atType(note.getAtType());

        return builder.build();
    }

    private ExternalId mapToExternalIdTmf(ExternalReference externalReference) {
        if (externalReference == null) return null;

        ExternalId.ExternalIdBuilder builder = ExternalId.builder();

        builder.id(externalReference.getId());
        builder.atBaseType(externalReference.getAtBaseType());
        builder.atSchemaLocation(map(externalReference.getAtSchemaLocation()));
        builder.atType(externalReference.getAtType());

        return builder.build();
    }
}

package de.bitconex.adlatus.inbox.service;

import de.bitconex.adlatus.common.infrastructure.exception.CreateOrderInvalidException;
import de.bitconex.adlatus.wholebuy.provision.service.catalog.ResourceCatalogService;
import de.bitconex.tmf.rcm.model.ResourceSpecification;
import de.bitconex.tmf.rcm.model.ResourceSpecificationCharacteristic;
import de.bitconex.tmf.rom.model.Characteristic;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import de.bitconex.tmf.rom.model.ResourceRefOrValue;
import de.bitconex.tmf.rom.model.ResourceSpecificationRef;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderValidatorImpl implements OrderValidator {
    private final ResourceCatalogService resourceCatalogService;

    public OrderValidatorImpl(ResourceCatalogService resourceCatalogService) {
        this.resourceCatalogService = resourceCatalogService;
    }

    @Override
    public void validate(ResourceOrderCreate resourceOrderCreate) {
        validateRequiredFields(resourceOrderCreate);
        validateOrderItems(resourceOrderCreate);
    }

    private void validateRequiredFields(ResourceOrderCreate resourceOrderCreate) {
        var resourceOrderItems = resourceOrderCreate.getOrderItem();
        if (resourceOrderItems == null || resourceOrderItems.isEmpty()) throw new CreateOrderInvalidException();

        // resourceOrderItem: id, action
        resourceOrderItems.stream()
                .filter(orderItem -> orderItem.getId() == null || orderItem.getAction() == null)
                .findAny()
                .ifPresent(orderItem -> {
                    throw new CreateOrderInvalidException();
                });

        // orderItem.resource.resourceCharacteristic: name
        resourceOrderItems.stream()
                .filter(orderItem -> orderItem.getResource() != null)
                .flatMap(orderItem -> orderItem.getResource().getResourceCharacteristic().stream())
                .filter(characteristic -> characteristic.getName() == null)
                .findAny()
                .ifPresent(characteristic -> {
                    throw new CreateOrderInvalidException();
                });

        // orderItem.orderItemRelationship: orderItem
        // orderItem.orderItemRelationship.orderItem: itemId
        resourceOrderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemRelationship() != null)
                .flatMap(orderItem -> orderItem.getOrderItemRelationship().stream())
                .filter(orderItemRelationship -> orderItemRelationship.getOrderItem() == null || orderItemRelationship.getOrderItem().getItemId() == null)
                .findAny()
                .ifPresent(orderItemRelationship -> {
                    throw new CreateOrderInvalidException();
                });

        // relatedParty: id, @referredType
        // In relatedParty, the role is mandatory if the id identifies a party - optional if it is a party role.
        var relatedParties = resourceOrderCreate.getRelatedParty();
        if (relatedParties != null) {
            relatedParties.stream()
                    .filter(relatedParty -> relatedParty.getId() == null || relatedParty.getRole() == null || relatedParty.getAtReferredType() == null)
                    .findAny()
                    .ifPresent(relatedParty -> {
                        throw new CreateOrderInvalidException();
                    });
        }

        // note: text, id
        var notes = resourceOrderCreate.getNote();
        if (notes != null) {
            notes.stream()
                    .filter(note -> note.getId() == null || note.getText() == null)
                    .findAny()
                    .ifPresent(note -> {
                        throw new CreateOrderInvalidException();
                    });
        }
    }

    private void validateOrderItems(ResourceOrderCreate resourceOrderCreate) {
        ResourceRefOrValue resource = resourceOrderCreate.getOrderItem().get(0).getResource();
        if (resource == null) throw new CreateOrderInvalidException();

        ResourceSpecificationRef resSpec = resource.getResourceSpecification();
        if (resSpec == null || resSpec.getId() == null) throw new CreateOrderInvalidException();

        ResourceSpecification apiSpec = resourceCatalogService.retrieveSpecification(resSpec.getId()); // todo handle exception NoSuchElementException

        List<String> resourceCharacteristicsNames = resource.getResourceCharacteristic().stream().map(Characteristic::getName).toList();
        List<String> resourceSpecCharacteristicNames = apiSpec.getResourceSpecCharacteristic().stream().map(ResourceSpecificationCharacteristic::getName).toList();

        resourceCharacteristicsNames.stream()
                .filter(name -> !resourceSpecCharacteristicNames.contains(name))
                .findAny()
                .ifPresent(name -> {
                    throw new CreateOrderInvalidException();
                });
    }
}

package de.bitconex.adlatus.wholebuy.provision.service.order.mapper;

import de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedPlace;
import de.bitconex.tmf.rom.model.*;
import org.mapstruct.Mapper;

import java.net.URI;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    ResourceOrder mapToResourceOrder(ResourceOrderCreate resourceOrderCreate);

    RelatedPlace mapToRelatedPlaceAdl(RelatedPlaceRefOrValue relatedPlaceRefOrValue);

    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.RelatedParty mapToRelatedPartyAdl(RelatedParty relatedParty);

    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.ResourceRelationship mapToResourceRelationshipAdl(ResourceRelationship resourceRelationship);

    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Quantity mapToQuantityAdl(Quantity quantity);

    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TimePeriod mapToTimePeriodAdl(TimePeriod timePeriod);

    de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Note mapToNoteAdl(Note note);

    default String map(URI uri) {
        return uri != null ? uri.toString() : null;
    }

    Quantity mapToQuantityTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.Quantity size);

    TimePeriod mapToTimePeriodTmf(de.bitconex.adlatus.wholebuy.provision.tmf.inbox.tmf.order.model.TimePeriod validFor);
}

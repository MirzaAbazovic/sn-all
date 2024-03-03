package de.bitconex.tmf.party.mapper;

import de.bitconex.tmf.party.models.EventSubscription;
import de.bitconex.tmf.party.models.EventSubscriptionInput;
import org.mapstruct.Mapper;

@Mapper
public interface EventSubscriptionMapper {

    EventSubscription toEventSubscription(EventSubscriptionInput source);

    EventSubscriptionInput toEventSubscriptionCreate(EventSubscription source);
}

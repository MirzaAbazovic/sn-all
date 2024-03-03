package de.bitconex.hub.repository;

import de.bitconex.hub.model.EventSubscriptionEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "hub.eventRegistrationService", havingValue = "EventRegistrationServiceJpa")
public interface EventSubscriptionRepositoryJpa extends JpaRepository<EventSubscriptionEntity, String> {

    @Query("SELECT e FROM EventSubscriptionEntity e WHERE LOWER(e.query) LIKE LOWER(CONCAT('%', :event, '%')) OR e.query IS NULL")
    List<EventSubscriptionEntity> findByEvent(@Param("event") String event);
}

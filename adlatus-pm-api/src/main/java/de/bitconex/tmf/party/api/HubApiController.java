package de.bitconex.tmf.party.api;

import de.bitconex.tmf.party.mapper.EventSubscriptionMapper;
import de.bitconex.tmf.party.mapper.EventSubscriptionMapperImpl;
import de.bitconex.tmf.party.models.EventSubscription;
import de.bitconex.tmf.party.models.EventSubscriptionInput;
import de.bitconex.tmf.party.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hub")
public class HubApiController implements HubApi {
    private final SubscriptionService subscriptionService;

    private final EventSubscriptionMapper eventSubscriptionMapper = new EventSubscriptionMapperImpl();

    private final Logger logger = LoggerFactory.getLogger(HubApiController.class);

    @Autowired
    public HubApiController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }


    @PostMapping
    public ResponseEntity<EventSubscription> registerListener(EventSubscriptionInput data) {
        try {
            EventSubscription subscription = subscriptionService.subscribe(eventSubscriptionMapper.toEventSubscription(data));
            logger.debug(HubApiController.class + ": New registered listener with id: " + subscription.getId());
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unregisterListener(@PathVariable String id) {
        try {
            subscriptionService.unsubscribe(id);
            logger.debug(HubApiController.class + ": Removed registered listener with id: " + id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

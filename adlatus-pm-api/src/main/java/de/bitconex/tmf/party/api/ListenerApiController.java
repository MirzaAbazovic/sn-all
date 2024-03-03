package de.bitconex.tmf.party.api;

import de.bitconex.tmf.party.models.*;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/listener")
public class ListenerApiController implements ListenerApi {

    private final Logger logger = LoggerFactory.getLogger(ListenerApiController.class);

    public ResponseEntity<EventSubscription> listenToIndividualAttributeValueChangeEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody IndividualAttributeValueChangeEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToIndividualCreateEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody IndividualCreateEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToIndividualDeleteEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody IndividualDeleteEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToIndividualStateChangeEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody IndividualStateChangeEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToOrganizationAttributeValueChangeEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody OrganizationAttributeValueChangeEvent data) {
        try {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    public ResponseEntity<EventSubscription> listenToOrganizationCreateEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody OrganizationCreateEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToOrganizationDeleteEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody OrganizationDeleteEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToOrganizationStateChangeEvent(@ApiParam(value = "The event data", required = true) @Valid @RequestBody OrganizationStateChangeEvent data) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

}

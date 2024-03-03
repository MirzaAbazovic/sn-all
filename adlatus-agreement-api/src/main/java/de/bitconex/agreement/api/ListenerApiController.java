package de.bitconex.agreement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bitconex.agreement.model.*;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")
@Controller
public class ListenerApiController implements ListenerApi {

    private static final Logger log = LoggerFactory.getLogger(ListenerApiController.class);

    public ResponseEntity<EventSubscription> listenToAgreementAttributeValueChangeEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementAttributeValueChangeEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementCreateEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementCreateEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementDeleteEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementDeleteEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementSpecificationAttributeValueChangeEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementSpecificationAttributeValueChangeEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementSpecificationCreateEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementSpecificationCreateEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementSpecificationDeleteEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementSpecificationDeleteEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementSpecificationStateChangeEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementSpecificationStateChangeEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<EventSubscription> listenToAgreementStateChangeEvent(@ApiParam(value = "The event data" ,required=true )  @Valid @RequestBody AgreementStateChangeEvent data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

}

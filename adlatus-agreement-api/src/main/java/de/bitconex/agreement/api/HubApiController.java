package de.bitconex.agreement.api;

import de.bitconex.agreement.model.EventSubscription;
import de.bitconex.agreement.model.EventSubscriptionInput;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2023-10-02T13:11:01.216Z")
@Controller
public class HubApiController implements HubApi {

    private static final Logger log = LoggerFactory.getLogger(HubApiController.class);

    public ResponseEntity<EventSubscription> registerListener(@ApiParam(value = "Data containing the callback endpoint to deliver the information" ,required=true )  @Valid @RequestBody EventSubscriptionInput data) {
        return new ResponseEntity<EventSubscription>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> unregisterListener(@ApiParam(value = "The id of the registered listener",required=true) @PathVariable("id") String id) {
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }
}

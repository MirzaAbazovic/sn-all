package de.bitconex.hub.controller;

import de.bitconex.hub.eventing.EventRegistrationService;
import de.bitconex.hub.model.EventSubscriptionInput;
import de.bitconex.hub.model.EventSubscriptionOutput;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hub")
public class HubController {

    private final EventRegistrationService eventRegistrationService;

    public HubController(EventRegistrationService eventRegistrationService) {
        this.eventRegistrationService = eventRegistrationService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> registerListener(@RequestBody EventSubscriptionInput eventSubscriptionInput) {
        eventRegistrationService.register(eventSubscriptionInput);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unregisterListener(@PathVariable String id) {
        eventRegistrationService.unregister(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<EventSubscriptionOutput>> subscriptionList() {
        return ResponseEntity.ok(eventRegistrationService.getAll());
    }
}

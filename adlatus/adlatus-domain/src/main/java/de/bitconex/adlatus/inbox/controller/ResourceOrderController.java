package de.bitconex.adlatus.inbox.controller;

import de.bitconex.adlatus.inbox.service.OrderReceiverService;
import de.bitconex.tmf.rom.model.ResourceOrder;
import de.bitconex.tmf.rom.model.ResourceOrderCreate;
import de.bitconex.tmf.rom.model.ResourceOrderUpdate;
import de.bitconex.tmf.rom.server.ResourceOrderApi;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("")
public class ResourceOrderController implements ResourceOrderApi {

    private final OrderReceiverService orderReceiverService;

    public ResourceOrderController(OrderReceiverService orderReceiverService) {
        this.orderReceiverService = orderReceiverService;
    }

    @Override
    public ResponseEntity<ResourceOrder> createResourceOrder(@Valid @RequestBody ResourceOrderCreate resourceOrderCreate) {
        log.debug("Creating resource order: {}", resourceOrderCreate);
        ResourceOrder resourceOrder = orderReceiverService.saveOrderToInbox(resourceOrderCreate);

        return ResponseEntity
            .created(URI.create(String.format(resourceOrder.getHref())))
            .body(resourceOrder);
    }

    @Override
    public ResponseEntity<Void> deleteResourceOrder(String s) {
        return null;
    }

    @Override
    public ResponseEntity<List<ResourceOrder>> listResourceOrder(@Valid String filter, @Valid Integer page, @Valid Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<ResourceOrder> patchResourceOrder(String s, @Valid ResourceOrderUpdate resourceOrderUpdate) {
        return null;
    }

    @Override
    public ResponseEntity<ResourceOrder> retrieveResourceOrder(String s, @Valid String s1) {
        return null;
    }

}
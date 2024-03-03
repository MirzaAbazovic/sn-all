package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.HubApi;
import de.bitconex.tmf.resource_catalog.model.EventSubscription;
import de.bitconex.tmf.resource_catalog.model.EventSubscriptionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Controller
@RequestMapping("${app.api-base-path:/tmf-api/resourceCatalog/v4}")
public class HubApiController implements HubApi {

    private final NativeWebRequest request;

    @Autowired
    public HubApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<EventSubscription> registerListener(EventSubscriptionInput data) {
        return HubApi.super.registerListener(data);
    }

    @Override
    public ResponseEntity<Void> unregisterListener(String id) {
        return HubApi.super.unregisterListener(id);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

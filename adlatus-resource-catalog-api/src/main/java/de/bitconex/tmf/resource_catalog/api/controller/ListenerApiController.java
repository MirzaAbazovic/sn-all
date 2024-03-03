package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ListenerApi;
import de.bitconex.tmf.resource_catalog.model.*;
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
public class ListenerApiController implements ListenerApi {

    private final NativeWebRequest request;

    @Autowired
    public ListenerApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public ResponseEntity<EventSubscription> listenToExportJobCreateEvent(ExportJobCreateEvent data) {
        return ListenerApi.super.listenToExportJobCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToExportJobStateChangeEvent(ExportJobStateChangeEvent data) {
        return ListenerApi.super.listenToExportJobStateChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToImportJobCreateEvent(ImportJobCreateEvent data) {
        return ListenerApi.super.listenToImportJobCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToImportJobStateChangeEvent(ImportJobStateChangeEvent data) {
        return ListenerApi.super.listenToImportJobStateChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCandidateChangeEvent(ResourceCandidateChangeEvent data) {
        return ListenerApi.super.listenToResourceCandidateChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCandidateCreateEvent(ResourceCandidateCreateEvent data) {
        return ListenerApi.super.listenToResourceCandidateCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCandidateDeleteEvent(ResourceCandidateDeleteEvent data) {
        return ListenerApi.super.listenToResourceCandidateDeleteEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCatalogChangeEvent(ResourceCatalogChangeEvent data) {
        return ListenerApi.super.listenToResourceCatalogChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCatalogCreateEvent(ResourceCatalogCreateEvent data) {
        return ListenerApi.super.listenToResourceCatalogCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCatalogDeleteEvent(ResourceCatalogDeleteEvent data) {
        return ListenerApi.super.listenToResourceCatalogDeleteEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCategoryChangeEvent(ResourceCategoryChangeEvent data) {
        return ListenerApi.super.listenToResourceCategoryChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCategoryCreateEvent(ResourceCategoryCreateEvent data) {
        return ListenerApi.super.listenToResourceCategoryCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceCategoryDeleteEvent(ResourceCategoryDeleteEvent data) {
        return ListenerApi.super.listenToResourceCategoryDeleteEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceSpecificationChangeEvent(ResourceSpecificationChangeEvent data) {
        return ListenerApi.super.listenToResourceSpecificationChangeEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceSpecificationCreateEvent(ResourceSpecificationCreateEvent data) {
        return ListenerApi.super.listenToResourceSpecificationCreateEvent(data);
    }

    @Override
    public ResponseEntity<EventSubscription> listenToResourceSpecificationDeleteEvent(ResourceSpecificationDeleteEvent data) {
        return ListenerApi.super.listenToResourceSpecificationDeleteEvent(data);
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

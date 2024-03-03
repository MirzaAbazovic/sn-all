package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ImportJobApi;
import de.bitconex.tmf.resource_catalog.model.ImportJob;
import de.bitconex.tmf.resource_catalog.model.ImportJobCreate;
import de.bitconex.tmf.resource_catalog.service.ImportJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Controller
@RequestMapping("${app.api-base-path:/tmf-api/resourceCatalog/v4}")
public class ImportJobApiController implements ImportJobApi {
    private final ImportJobService importJobService;
    private final NativeWebRequest request;

    public ImportJobApiController(NativeWebRequest request, ImportJobService importJobService) {
        this.request = request;
        this.importJobService = importJobService;
    }

    @Override
    public ResponseEntity<ImportJob> createImportJob(ImportJobCreate importJob) {
        return ResponseEntity.created(null).body(importJobService.createImportJob(importJob));
    }

    @Override
    public ResponseEntity<Void> deleteImportJob(String id) {
        var deleted = importJobService.deleteImportJob(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<ImportJob>> listImportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(importJobService.listImportJob(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ImportJob> retrieveImportJob(String id, String fields) {
        var importJob = importJobService.retrieveImportJob(id, fields);
        return importJob != null ? ResponseEntity.ok(importJob) : ResponseEntity.notFound().build();
    }

    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

}

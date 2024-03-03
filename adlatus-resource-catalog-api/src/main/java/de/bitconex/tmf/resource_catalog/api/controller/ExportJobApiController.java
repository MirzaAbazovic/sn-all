package de.bitconex.tmf.resource_catalog.api.controller;


import de.bitconex.tmf.resource_catalog.api.ExportJobApi;
import de.bitconex.tmf.resource_catalog.model.ExportJob;
import de.bitconex.tmf.resource_catalog.model.ExportJobCreate;
import de.bitconex.tmf.resource_catalog.service.ExportJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Generated;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T10:29:48.460897800+02:00[Europe/Belgrade]")
@Controller
@RequestMapping("${app.api-base-path:/tmf-api/resourceCatalog/v4}")
public class ExportJobApiController implements ExportJobApi {
    private final ExportJobService exportJobService;

    private final NativeWebRequest request;

    public ExportJobApiController(NativeWebRequest request, ExportJobService exportJobService) {
        this.request = request;
        this.exportJobService = exportJobService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<ExportJob> createExportJob(ExportJobCreate exportJob) {
        return ResponseEntity.created(null).body(exportJobService.createExportJob(exportJob));
    }

    @Override
    public ResponseEntity<Void> deleteExportJob(String id) {
        var deleted = exportJobService.deleteExportJob(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<ExportJob>> listExportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams) {
        return ResponseEntity.ok(exportJobService.listExportJob(fields, offset, limit, allParams));
    }

    @Override
    public ResponseEntity<ExportJob> retrieveExportJob(String id, String fields) {
        var exportJob = exportJobService.retrieveExportJob(id, fields);
        return exportJob != null ? ResponseEntity.ok(exportJob) : ResponseEntity.notFound().build();
    }
}

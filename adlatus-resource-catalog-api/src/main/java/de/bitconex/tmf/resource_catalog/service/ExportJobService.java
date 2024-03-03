package de.bitconex.tmf.resource_catalog.service;

import de.bitconex.tmf.resource_catalog.api.ExportJobApi;
import de.bitconex.tmf.resource_catalog.model.ExportJob;
import de.bitconex.tmf.resource_catalog.model.ExportJobCreate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ExportJobService {

    ExportJob createExportJob(ExportJobCreate exportJob);


    Boolean deleteExportJob(String id);


    List<ExportJob> listExportJob(String fields, Integer offset, Integer limit, Map<String, String> allParams);


    ExportJob retrieveExportJob(String id, String fields);
}
